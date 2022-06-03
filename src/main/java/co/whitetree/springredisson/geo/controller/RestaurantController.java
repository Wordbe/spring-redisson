package co.whitetree.springredisson.geo.controller;

import co.whitetree.springredisson.geo.dto.Restaurant;
import co.whitetree.springredisson.geo.service.RestaurantLocatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantLocatorService restaurantLocatorService;

    @GetMapping("geo/{zip}")
    public Flux<Restaurant> getRestaurants(@PathVariable String zip) {
        return restaurantLocatorService.getRestaurants(zip);
    }

}
