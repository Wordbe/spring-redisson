package co.whitetree.springredisson;

import org.junit.jupiter.api.RepeatedTest;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RedissonApplicationTests {

    @Autowired
    private ReactiveStringRedisTemplate template;

    @Autowired
    private RedissonReactiveClient client;

    /**
     * Spring Data Reactive Redis Issue
     * - Performance Issues
     * - No support for Reactive CRUD repository
     * - Some annotations do not work with reactive type
     */
    @RepeatedTest(3)
    void springDataRedisTest() {
        ReactiveValueOperations<String, String> operations = template.opsForValue();

        long start = System.currentTimeMillis();

        Mono<Void> mono = Flux.range(1, 500_000)
                .flatMap(i -> operations.increment("user:1:visit")) // incr
                .then();
        StepVerifier.create(mono)
                .verifyComplete();

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

    @RepeatedTest(3)
    void redissonTest() {
        RAtomicLongReactive atomicLong = client.getAtomicLong("user:2:visit");

        long start = System.currentTimeMillis();

        Mono<Void> mono = Flux.range(1, 500_000)
                .flatMap(i -> atomicLong.incrementAndGet()) // incre
                .then();
        StepVerifier.create(mono)
                .verifyComplete();

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

}
