package com.demidrolll.rsb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

class AsyncApiIntegrationTest {

  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AsyncApiIntegrationTest.class);
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);

  @Test
  void async() {

    Flux<Integer> integers = Flux.create(emitter -> this.launch(emitter, 5));

    StepVerifier
        .create(integers.doFinally(signalType -> this.executorService.shutdown()))
        .expectNextCount(5)
        .verifyComplete();
  }

  private void launch(FluxSink<Integer> integerFluxSink, int count) {
    this.executorService.submit(() -> {
      var integer = new AtomicInteger();
      while (integer.get() < count) {
        double random = Math.random();
        integerFluxSink.next(integer.incrementAndGet());
        this.sleep((long) (random * 1_000));
      }
      integerFluxSink.complete();
    });
  }

  private void sleep(long s) {
    try {
      Thread.sleep(s);
    } catch (Exception e) {
      log.error(e);
    }
  }
}
