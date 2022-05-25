package co.whitetree.springredisson.city.service;

import co.whitetree.springredisson.city.client.CityClient;
import co.whitetree.springredisson.city.dto.City;
import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CityService {

    private final CityClient cityClient;
//    private final RMapCacheReactive<String, City> cityMap;
    private final RMapReactive<String, City> cityMap;

    public CityService(CityClient cityClient, RedissonReactiveClient redissonReactiveClient) {
        this.cityClient = cityClient;
//        this.cityMap = redissonReactiveClient.getMapCache("city", new TypedJsonJacksonCodec(String.class, City.class));
        this.cityMap = redissonReactiveClient.getMap("city", new TypedJsonJacksonCodec(String.class, City.class));
    }

    /*
        get from cache
        if empty - get from db or source
                    put it in cache
        return
     */
//    public Mono<City> getCity(String zipCode) {
//        return cityMap.get(zipCode)
//                .switchIfEmpty(cityClient.getCity(zipCode)
//                        .flatMap(city -> cityMap.fastPut(zipCode, city, 10, TimeUnit.SECONDS)
//                                .thenReturn(city)));
//    }

    public Mono<City> getCity(String zipCode) {
        return cityMap.get(zipCode)
                .onErrorResume(ex -> cityClient.getCity(zipCode));
    }

    @Scheduled(fixedRate = 10_000)
    public void updateCity() {
        System.out.println("updating cities...");
        cityClient.getAll()
                .collectList()
                .map(list -> list.stream().collect(Collectors.toMap(City::getZip, Function.identity())))
                .flatMap(cityMap::putAll)
                .subscribe();
    }
}
