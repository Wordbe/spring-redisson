package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RSetReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Redisson13BatchTest extends BaseTest {

    @Test
    public void batchTest() {
        RBatchReactive batch = client.createBatch(BatchOptions.defaults());
        RListReactive<Long> numbersList = batch.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> numbersSet = batch.getSet("numbers-set", LongCodec.INSTANCE);

        for (long i = 0; i < 500_000; i++) { // 7.6초 정도 소요
            numbersList.add(i);
            numbersSet.add(i);
        }

        StepVerifier.create(batch.execute().then())
                .verifyComplete();
    }

    @Test
    public void regularTest() { // 27초 정도 소요 (without batch)
        RListReactive<Long> numbersList = client.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> numbersSet = client.getSet("numbers-set", LongCodec.INSTANCE);

        Mono<Void> mono = Flux.range(1, 500_000)
                .map(Long::valueOf)
                .flatMap(i -> numbersList.add(i).then(numbersSet.add(i)))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
