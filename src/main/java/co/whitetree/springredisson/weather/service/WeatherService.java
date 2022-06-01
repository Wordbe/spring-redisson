package co.whitetree.springredisson.weather.service;

import co.whitetree.springredisson.weather.client.ExternalServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final ExternalServiceClient externalServiceClient;

    /**
     * 캐시된 zip 1~5 가 들어오면, redis 로부터 값을 찾아와 바로 리턴한다. (0을 리턴하지 않는다.)
     */
    @Cacheable(value = "weather", key = "#zip")
    public int weatherInfo(int zip) {
        return 0;
    }

    /**
     * weather 해시에 key 1~5 는 10초마다 지속적으로 업데이트 됨
     */
//    @Scheduled(fixedRate = 10_000)
    public void update() {
        System.out.println("updating weather... for zip 1~5");
        IntStream.rangeClosed(1, 5)
                .forEach(externalServiceClient::weatherInfo);
    }
}
