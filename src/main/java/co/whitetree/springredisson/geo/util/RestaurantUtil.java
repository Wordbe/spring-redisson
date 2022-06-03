package co.whitetree.springredisson.geo.util;

import co.whitetree.springredisson.geo.dto.Restaurant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
public class RestaurantUtil {

    public static List<Restaurant> getRestaurants() {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream stream = RestaurantUtil.class.getClassLoader().getResourceAsStream("restaurant.json");
        try {
            return objectMapper.readValue(stream, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("[ObjectMapper Error]", e);
        }
        return Collections.emptyList();
    }
}
