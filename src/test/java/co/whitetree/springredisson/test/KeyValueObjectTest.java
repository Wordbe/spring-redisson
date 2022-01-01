package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import co.whitetree.springredisson.test.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

public class KeyValueObjectTest extends BaseTest {

    @Test
    public void keyValueObject() {
        Student student = Student.builder()
                .name("marshal")
                .age(10)
                .city("atlanta")
                .marks(Arrays.asList(1, 2, 3))
                .build();

        RBucketReactive<Object> bucket = client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        Mono<Void> set = bucket.set(student);
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }
}
