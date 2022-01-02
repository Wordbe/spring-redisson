package co.whitetree.springredisson.test.priorityqueue;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSetReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PriorityQueue {

    private final RScoredSortedSetReactive<UserOrder> queue;

    public Mono<Void> add(UserOrder userOrder) {
        return queue.add(
//                userOrder.getCategory().ordinal(),
                getScore(userOrder.getCategory()),
                userOrder
        ).then();
    }

    public Flux<UserOrder> takeItems() {
        return queue.takeFirstElements()
                .limitRate(1);
    }

    private double getScore(Category category) {
        return category.ordinal() + Double.parseDouble("0." + System.nanoTime()); // 먼저 만들어진 것 순서대로 정렬하려고
    }
}
