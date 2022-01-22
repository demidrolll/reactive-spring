package com.demidrolll.rsb.ch511;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

public class HooksOnOperatorDebugTest {

  private static String stackTraceToString(Throwable throwable) {
    try (var sw = new StringWriter(); var pw = new PrintWriter(sw)) {
      throwable.printStackTrace(pw);
      return sw.toString();
    } catch (Exception ioEx) {
      throw new RuntimeException(ioEx);
    }
  }

  @Test
  public void onOperatorDebug() {
    Hooks.onOperatorDebug();
    var stackTrace = new AtomicReference<String>();
    var errorFlux = Flux
        .error(new IllegalArgumentException("Oops!"))//
        .checkpoint()
        .delayElements(Duration.ofMillis(1));

    StepVerifier
        .create(errorFlux)
        .expectErrorMatches(ex -> {
          stackTrace.set(stackTraceToString(ex));
          return ex instanceof IllegalArgumentException;
        })
        .verify();

    Assertions.assertTrue(stackTrace.get()
        .contains("Flux.error ⇢ at " + HooksOnOperatorDebugTest.class.getName()
        ));
  }
}
