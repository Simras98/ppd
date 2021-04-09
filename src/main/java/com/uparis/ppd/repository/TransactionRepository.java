package com.uparis.ppd.repository;

import com.uparis.ppd.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Override
    List<Transaction> findAll();

    Optional<Transaction> findById(Long id);
}
