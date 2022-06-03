package co.whitetree.springredisson.geo.service;

import co.whitetree.springredisson.geo.dto.GeoLocation;
import co.whitetree.springredisson.geo.dto.Restaurant;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Service
public class RestaurantLocatorService {

    private final RGeoReactive<Restaurant> restaurantRGeo;
    private final RMapReactive<String, GeoLocation> geoLocationRMap;

    public RestaurantLocatorService(RedissonReactiveClient client) {
        this.restaurantRGeo = client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
        this.geoLocationRMap = client.getMap("usa", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }

    public Flux<Restaurant> getRestaurants(String zipcode) {
        return geoLocationRMap.get(zipcode)
                .map(gl -> GeoSearchArgs.from(gl.getLongitude(), gl.getLatitude()).radius(5, GeoUnit.MILES))
                .flatMap(restaurantRGeo::search)
                .flatMapIterable(Function.identity());
    }
}
