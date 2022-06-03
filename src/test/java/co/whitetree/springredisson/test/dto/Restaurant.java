package co.whitetree.springredisson.test.dto;

import lombok.Data;

@Data
public class Restaurant {
    private String id;
    private String city;
    private Double latitude;
    private Double longitude;
    private String name;
    private String zip;
}
