package co.whitetree.springredisson.fib.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FibService {

    // strategy: cache evict
    @Cacheable(value = "math:fib", key = "#index")
    public int fibResult(int index, String name) {
        System.out.println("Calculating fib for " + index + ", name: " + name);
        return fib(index);
    }

    // POST / PUT / PATCH / DELETE
    @CacheEvict(value = "math:fib", key = "#index")
    public void clearCache(int index) {
        System.out.println("clearing hash key: " + index);
    }

//    @Scheduled(fixedRate = 10_000) // 10 sec
    @CacheEvict(value = "math:fib", allEntries = true)
    public void clearCache() {
        System.out.println("clearing all math:fib keys");
    }

    // intentionally O(2^N)
    private int fib(int n) {
        if (n < 2)
            return n;
        return fib(n - 1) + fib(n - 2);
    }
}
