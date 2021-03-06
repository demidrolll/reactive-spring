package com.demidrolll.rsb.repository;

import com.demidrolll.rsb.model.Customer;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface SimpleCustomerRepository {

  Mono<Customer> save(Customer c);

  Flux<Customer> findAll();

  Mono<Customer> update(Customer c);

  Mono<Customer> findById(Integer id);

  Mono<Void> deleteById(Integer id);
}
