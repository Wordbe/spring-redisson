package co.whitetree.springredisson.fib.controller;

import co.whitetree.springredisson.fib.service.FibService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class FibController {

    private final FibService fibService;

    @GetMapping("/fib/{index}/{name}")
    Mono<Integer> getFib(@PathVariable int index, @PathVariable String name) {
        return Mono.fromSupplier(() -> fibService.fibResult(index, name));
    }

    @DeleteMapping("/fib/clear/{index}")
    Mono<Void> clearCache(@PathVariable int index) {
        return Mono.fromRunnable(() -> fibService.clearCache(index));
    }
}
