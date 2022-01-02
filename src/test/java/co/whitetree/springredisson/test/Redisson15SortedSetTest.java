package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class Redisson15SortedSetTest extends BaseTest {

    @Test
    public void sortedSet() {
        RScoredSortedSetReactive<String> sortedSet = client.getScoredSortedSet("student:score", StringCodec.INSTANCE);
        Mono<Void> mono = sortedSet.addScore("sam", 12.25)
                .then(sortedSet.add(23.25, "mike"))
                .then(sortedSet.addScore("jake", 7))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();

        sortedSet.entryRange(0, 1) // 오름차순 ASC
                .flatMapIterable(Function.identity()) // flux
                .map(scoredEntity -> scoredEntity.getScore() + ":" + scoredEntity.getValue())
                .doOnNext(System.out::println)
                .subscribe();

        sleep(1000);
        // 처음 실행할 때는 jake, sam 순으로 나옴
        // 그 다음 실행하면 jake, mike 순으로 나옴 (same 이 스코어(addScore)가 높아져서 순서가 뒤로 감)
    }
}
