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

    public static Student student1() {
        return Student.builder()
                .name("sam")
                .age(10)
                .city("atlanta")
                .marks(List.of(1, 2, 3))
                .build();
    }

    public static Student student2() {
        return Student.builder()
                .name("jake")
                .age(30)
                .city("miami")
                .marks(List.of(10, 20, 30))
                .build();
    }
}
