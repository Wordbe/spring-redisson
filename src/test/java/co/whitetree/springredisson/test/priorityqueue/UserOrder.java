package co.whitetree.springredisson.test.priorityqueue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserOrder {
    private final int id;
    private final Category category;
}
