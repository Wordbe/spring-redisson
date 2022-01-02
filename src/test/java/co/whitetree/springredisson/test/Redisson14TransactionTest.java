package co.whitetree.springredisson.test;

import co.whitetree.springredisson.test.config.BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Redisson14TransactionTest extends BaseTest {

    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    @BeforeAll
    public void accountSetUp() {
        this.user1Balance = client.getBucket("user:1:balance", LongCodec.INSTANCE);
        this.user2Balance = client.getBucket("user:2:balance", LongCodec.INSTANCE);
        Mono<Void> mono = user1Balance.set(100L)
                .then(user2Balance.set(0L))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @AfterAll
    public void accountBalanceStatus() {
        Mono<Void> mono = Flux.zip(user1Balance.get(), user2Balance.get())
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void nonTransactional() {
        transfer(user1Balance, user2Balance, 60)
                .thenReturn(0)
                .map(i -> (5 / i)) // some error
                .doOnError(System.out::println)
                .subscribe();
    }

    @Test
    public void transactional() {
        RTransactionReactive transaction = client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);
        transfer(user1Balance, user2Balance, 60)
                .thenReturn(0)
                .map(i -> (5 / i)) // some error
                .then(transaction.commit())
                .doOnError(System.out::println)
                .doOnError(ex ->transaction.rollback())
                .subscribe();
    }

    private Mono<Void> transfer(RBucketReactive<Long> fromAccount, RBucketReactive<Long> toAccount, int amount) {
        return Flux.zip(fromAccount.get(), toAccount.get())
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> fromAccount.set(t.getT1() - amount).thenReturn(t))
                .flatMap(t -> toAccount.set(t.getT2() + amount))
                .then();
    }
}
