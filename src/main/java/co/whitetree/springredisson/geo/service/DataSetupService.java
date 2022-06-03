package co.whitetree.springredisson.geo.service;

import co.whitetree.springredisson.geo.dto.GeoLocation;
import co.whitetree.springredisson.geo.dto.Restaurant;
import co.whitetree.springredisson.geo.util.RestaurantUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class DataSetupService implements CommandLineRunner {
    private final RedissonReactiveClient client;
    private RGeoReactive<Restaurant> restaurantRGeo;
    private RMapReactive<String, GeoLocation> geoLocationRMap;

    @Override
    public void run(String... args) throws Exception {
        this.restaurantRGeo = client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
        this.geoLocationRMap = client.getMap("usa", new TypedJsonJacksonCodec(String.class, GeoLocation.class));

        Flux.fromIterable(RestaurantUtil.getRestaurants())
                .flatMap(r -> restaurantRGeo.add(r.getLongitude(), r.getLatitude(), r).thenReturn(r))
                .flatMap(r -> geoLocationRMap.fastPut(r.getZip(), GeoLocation.of(r.getLongitude(), r.getLatitude())))
                .doFinally(signalType -> System.out.println("[Restaurant added " + signalType))
                .subscribe();
    }
}
