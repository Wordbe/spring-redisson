package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class NumberTest extends BaseTest {
    @Test
    public void keyValueIncrease() {
        RAtomicLongReactive atomicLong = client.getAtomicLong("user:1:visit");
        // 자바의 atomicLong 과 비슷
        Mono<Void> mono = Flux.range(1, 30)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(i -> atomicLong.incrementAndGet())
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }
}
