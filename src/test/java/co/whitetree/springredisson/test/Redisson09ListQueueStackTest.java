package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Redisson09ListQueueStackTest extends BaseTest {

    @Test
    public void list() {
        // 레디스에서 number-input 요소 확인하기
        // lrange number-input 0 -1
        RListReactive<Long> list = client.getList("number-input", LongCodec.INSTANCE);

        // 10 개의 publisher 를 한개씩 스트림으로 리턴한다.
        // 클라이언트가 수신한 순서에 따라 스트림 처리가 된다.
        Mono<Void> listAdd = Flux.range(1, 10)
                .map(Long::valueOf)
                .flatMap(list::add)
                .then();
        StepVerifier.create(listAdd)
                .verifyComplete();
        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    public void list2() {
        // 레디스에서 number-input 요소 확인하기
        // lrange number-input 0 -1
        RListReactive<Long> list = client.getList("number-input", LongCodec.INSTANCE);

        // 한 번에 보내기
        // 순서를 정해서 보내줄 수 있다.
        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        StepVerifier.create(list.addAll(longList).then())
                .verifyComplete();
        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    public void queue() {
        RQueueReactive<Long> queue = client.getQueue("number-input", LongCodec.INSTANCE);
        Mono<Void> queuePoll = queue.poll()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(queuePoll)
                .verifyComplete();
        StepVerifier.create(queue.size())
                .expectNext(6) // list2 에서 10개 넣고 여기서 4개 빼서 6개 남음
                .verifyComplete();

    }

    @Test
    public void stack() { // deque 사용
        RDequeReactive<Long> deque = client.getDeque("number-input", LongCodec.INSTANCE);
        Mono<Void> stackPoll = deque.pollLast()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(stackPoll)
                .verifyComplete();
        StepVerifier.create(deque.size())
                .expectNext(2) // queue 6개, 여기서 4개 빼서 2개 남음
                .verifyComplete();

    }
}
