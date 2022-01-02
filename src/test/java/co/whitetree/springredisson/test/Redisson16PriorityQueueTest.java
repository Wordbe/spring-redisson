package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import co.whitetree.springredisson.test.priorityqueue.Category;
import co.whitetree.springredisson.test.priorityqueue.PriorityQueue;
import co.whitetree.springredisson.test.priorityqueue.UserOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Redisson16PriorityQueueTest extends BaseTest {

    private PriorityQueue priorityQueue;

    @BeforeAll
    public void setupQueue() {
        RScoredSortedSetReactive<UserOrder> scoredSortedSet = client.getScoredSortedSet("user:order", new TypedJsonJacksonCodec(UserOrder.class));
        this.priorityQueue = new PriorityQueue(scoredSortedSet);
    }

    @Test
    public void producer() {
//        UserOrder u1 = new UserOrder(1, Category.GUEST);
//        UserOrder u2 = new UserOrder(2, Category.STD);
//        UserOrder u3 = new UserOrder(3, Category.PRIME);
//        UserOrder u4 = new UserOrder(4, Category.STD);
//        UserOrder u5 = new UserOrder(5, Category.GUEST);
//        Mono<Void> mono = Flux.just(u1, u2, u3, u4, u5)
//                .flatMap(priorityQueue::add)
//                .then();
//        StepVerifier.create(mono)
//                .verifyComplete();

        Flux.interval(Duration.ofSeconds(1))
                .map(l -> l.intValue() * 5)
                .doOnNext(i -> {
                    UserOrder u1 = new UserOrder(i + 1, Category.GUEST);
                    UserOrder u2 = new UserOrder(i + 2, Category.STD);
                    UserOrder u3 = new UserOrder(i + 3, Category.PRIME);
                    UserOrder u4 = new UserOrder(i + 4, Category.STD);
                    UserOrder u5 = new UserOrder(i + 5, Category.GUEST);
                    Mono<Void> mono = Flux.just(u1, u2, u3, u4, u5)
                            .flatMap(priorityQueue::add)
                            .then();
                    StepVerifier.create(mono)
                            .verifyComplete();
                }).subscribe();
        sleep(60_000); // 60 seconds
    }

    @Test
    public void consumer() {
        priorityQueue.takeItems()
                .delayElements(Duration.ofMillis(500))
                .doOnNext(System.out::println)
                .subscribe();
        sleep(600_000);
    }
}
