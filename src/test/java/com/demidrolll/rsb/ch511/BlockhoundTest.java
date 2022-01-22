package com.demidrolll.rsb.ch511;

import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class BlockhoundTest {

  private final static AtomicBoolean BLOCKHOUND = new AtomicBoolean();

  @BeforeAll
  static void before() {
    BLOCKHOUND.set(true);
    var integrations = new ArrayList<BlockHoundIntegration>();
    var services = ServiceLoader.load(BlockHoundIntegration.class);
    services.forEach(integrations::add);
    integrations.add(builder -> builder.blockingMethodCallback(blockingMethod -> {
      if (BLOCKHOUND.get()) {
        throw new BlockingCallError(blockingMethod.toString());
      }
    }));

    BlockHound.install(integrations.toArray(new BlockHoundIntegration[0]));
  }

  @AfterAll
  static void after() {
    BLOCKHOUND.set(false);
  }

  @Test
  public void notOk() {
    StepVerifier
        .create(this.buildBlockingMono().subscribeOn(Schedulers.parallel()))
        .expectErrorMatches(e -> e instanceof BlockingCallError)
        .verify();
  }

  @Test
  public void ok() {
    StepVerifier//
        .create(this.buildBlockingMono().subscribeOn(Schedulers.boundedElastic()))
        .expectNext(1L)//
        .verifyComplete();
  }

  Mono<Long> buildBlockingMono() {
    return Mono.just(1L).doOnNext(it -> block());
  }

  void block() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static class BlockingCallError extends Error {

    BlockingCallError(String msg) {
      super(msg);
    }
  }
}
