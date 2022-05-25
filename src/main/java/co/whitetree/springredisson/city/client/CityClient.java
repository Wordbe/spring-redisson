package co.whitetree.springredisson.city.client;

import co.whitetree.springredisson.city.dto.City;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CityClient {

    private final WebClient webClient;

    public CityClient(@Value("${city.service.url}") String url) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public Mono<City> getCity(String zipCode) {
        return webClient.get()
                .uri("{zipCode}", zipCode)
                .retrieve()
                .bodyToMono(City.class);
    }

    public Flux<City> getAll() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(City.class);
    }
}
