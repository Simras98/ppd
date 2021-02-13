package com.uparis.ppd.service;

import com.uparis.ppd.model.Transaction;
import com.uparis.ppd.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  @Autowired private TransactionRepository transactionRepository;

  public void save(Transaction transaction) {
    transactionRepository.save(transaction);
  }
}
