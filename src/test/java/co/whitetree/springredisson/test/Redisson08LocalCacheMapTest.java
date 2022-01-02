package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import co.whitetree.springredisson.test.config.RedissonConfig;
import co.whitetree.springredisson.test.dto.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class Redisson08LocalCacheMapTest extends BaseTest {

    private RLocalCachedMap<Integer, Student> studentsMap;

    @BeforeAll
    public void setupClient() {
        RedissonConfig config = new RedissonConfig();
        RedissonClient client = config.getClient();

        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE);
        studentsMap = client.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                mapOptions);
    }

    @Test
    public void appServer1() {
        Student student1 = Student.student1();
        Student student2 = Student.student2();

        studentsMap.put(1, student1);
        studentsMap.put(2, student2);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println(i + " ==> " + studentsMap.get(1)))
                .subscribe();

        sleep(600000); // 10분
    }

    @Test
    public void appServer2() {
        Student student1 = Student.builder()
                .name("sam-updated")
                .age(10)
                .city("atlanta")
                .marks(List.of(1, 2, 3))
                .build();
        studentsMap.put(1, student1);
    }
}
