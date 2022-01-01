package co.whitetree.springredisson.test.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Student {
    private String name;
    private int age;
    private String city;
    private List<Integer> marks;
}
