package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

public class Redisson05ExpiredEventTest extends BaseTest {
    @Test
    public void expiredEventThenNotify() {
        // Redis-cli
        // config set notify-keyspace-events AKE 하여 알람 등록.
        // "AKE" string means all the events except "m".
        // m     Key miss events (events generated when a key that doesn't exist is accessed)

        RBucketReactive<Object> bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("sam", 5, TimeUnit.SECONDS);
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();
        Mono<Void> event = bucket.addListener(new ExpiredObjectListener() {
            @Override
            public void onExpired(String s) {
                System.out.println("Expired: " + s);
            }
        }).then();

        StepVerifier.create(set.concatWith(get).concatWith(event))
                .verifyComplete();

        // wait for expired event
        sleep(10000);
    }
}
