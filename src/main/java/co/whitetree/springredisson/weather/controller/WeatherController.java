package co.whitetree.springredisson.weather.controller;

import co.whitetree.springredisson.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/weather/{zip}")
    Mono<Integer> getWeather(@PathVariable int zip) {
        return Mono.fromSupplier(() -> weatherService.weatherInfo(zip));
    }
}
