package com.demidrolll.rsb.ch59;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Log4j2
class OnErrorMapTest {

  @Test
  public void onErrorMap() throws Exception {

    class GenericException extends RuntimeException {

    }

    var counter = new AtomicInteger();
    Flux<Integer> resultsInError = Flux.error(new IllegalArgumentException("oops!"));
    Flux<Integer> errorHandlingStream = resultsInError
        .onErrorMap(IllegalArgumentException.class, ex -> new GenericException())
        .doOnError(GenericException.class, ge -> counter.incrementAndGet());

    StepVerifier.create(errorHandlingStream).expectError().verify();

    Assertions.assertEquals(1, counter.get());
  }
}
