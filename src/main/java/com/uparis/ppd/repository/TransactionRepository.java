package com.uparis.ppd.repository;

import com.uparis.ppd.model.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.member.id = :id")
    void deleteAllByCustomerId(@Param("id") long id);
}
