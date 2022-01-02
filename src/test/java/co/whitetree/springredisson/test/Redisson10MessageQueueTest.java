package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Redisson10MessageQueueTest extends BaseTest {

    private RBlockingDequeReactive<Long> msgQueue;

    @BeforeAll
    public void setupQueue() {
        this.msgQueue = client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    @Test
    public void consumer1() {
        msgQueue.takeElements()
                .doOnNext(i -> System.out.println("consumer1: " + i))
                .doOnError(System.out::println)
                .subscribe();
        sleep(600_000);
    }

    @Test
    public void consumer2() {
        msgQueue.takeElements()
                .doOnNext(i -> System.out.println("consumer2: " + i))
                .doOnError(System.out::println)
                .subscribe();
        sleep(600_000);
    }

    @Test
    public void producer() {
        Mono<Void> mono = Flux.range(1, 100)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> System.out.println("going to add " + i))
                .flatMap(i -> msgQueue.add(Long.valueOf(i)))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
