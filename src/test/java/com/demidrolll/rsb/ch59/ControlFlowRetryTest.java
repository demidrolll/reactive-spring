package com.demidrolll.rsb.ch59;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class ControlFlowRetryTest {

  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(ControlFlowRetryTest.class);

  @Test
  void retry() {
    var errored = new AtomicBoolean();

    Flux<String> producer = Flux
        .create(sink -> {
          if (!errored.get()) {
            errored.set(true);
            sink.error(new RuntimeException("Nope!"));
            log.info("returning a " + RuntimeException.class.getName() + "!");
          } else {
            log.info("we've already errored so here's the value");
            sink.next("hello");
          }
          sink.complete();
        });
    Flux<String> retryOnError = producer.retry();
    StepVerifier.create(retryOnError).expectNext("hello").verifyComplete();
  }
}
