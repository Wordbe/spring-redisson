package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Redisson11HyperLogLogTest extends BaseTest {

    @Test
    public void count() {
        RHyperLogLogReactive<Long> counter = client.getHyperLogLog("user:visits", LongCodec.INSTANCE);

//        List<Long> longs = LongStream.rangeClosed(1, 25)
//                .boxed()
//                .collect(Collectors.toList());

        List<Long> longs1 = LongStream.rangeClosed(1, 25000)
                .boxed()
                .collect(Collectors.toList());

        List<Long> longs2 = LongStream.rangeClosed(25001, 50000)
                .boxed()
                .collect(Collectors.toList());

        List<Long> longs3 = LongStream.rangeClosed(1, 75000)
                .boxed()
                .collect(Collectors.toList());

        List<Long> longs4 = LongStream.rangeClosed(50000, 100_000)
                .boxed()
                .collect(Collectors.toList());

        Mono<Void> mono = Flux.just(longs1, longs2, longs3, longs4)
                .flatMap(counter::addAll)
                .then();

//        StepVerifier.create(counter.addAll(longs).then())
//                .verifyComplete();

        StepVerifier.create(mono)
                .verifyComplete();

        // 데이터 사이즈가 작을 때는 정확히 일치
        counter.count()
                .doOnNext(System.out::println) // 데이터 사이즈 커지면 다르다. 99562 -> 대략적인 추정치 하되 메모리를 아끼고 싶으면 HyperLogLog 사용
                .subscribe();
    }
}
