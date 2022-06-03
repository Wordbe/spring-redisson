package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import co.whitetree.springredisson.test.dto.GeoLocation;
import co.whitetree.springredisson.test.dto.Restaurant;
import co.whitetree.springredisson.test.util.RestaurantUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class Redisson17GeoSpatialTest extends BaseTest {

    private RGeoReactive<Restaurant> restaurantRGeo;
    private RMapReactive<String, GeoLocation> geoLocationRMap;

    @BeforeAll
    public void setGeo() {
        this.restaurantRGeo = client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
        this.geoLocationRMap = client.getMap("us:texas", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }

    @Test
    void add() {
        Mono<Void> mono = Flux.fromIterable(RestaurantUtil.getRestaurants())
                .flatMap(r -> restaurantRGeo.add(r.getLongitude(), r.getLatitude(), r).thenReturn(r))
                .flatMap(r -> geoLocationRMap.fastPut(r.getZip(), GeoLocation.of(r.getLongitude(), r.getLatitude())))
                .then();

        StepVerifier.create(mono).verifyComplete();
    }

    @Test
    public void search() {
        Mono<Void> mono = geoLocationRMap.get("75247")
                .map(gl -> GeoSearchArgs.from(gl.getLongitude(), gl.getLatitude()).radius(5, GeoUnit.MILES))
                .flatMap(restaurant -> restaurantRGeo.search(restaurant))
                .flatMapIterable(Function.identity())
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(mono).verifyComplete();
    }
}
