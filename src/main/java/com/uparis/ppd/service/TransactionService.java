package com.uparis.ppd.service;

import com.uparis.ppd.model.Member;
import com.uparis.ppd.model.Transaction;
import com.uparis.ppd.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public void create(Member member, Long time, double price) {
        Transaction transaction = new Transaction(member, time, price);
        transactionRepository.save(transaction);
    }

    public boolean checkExpirationDate(String expirationDate) {
        LocalDate localDate = LocalDate.now();
        int currentMonth = localDate.getMonthValue();
        int currentYear = localDate.getYear();
        int month = Integer.parseInt(expirationDate.substring(0,2));
        int year = Integer.parseInt((Integer.toString(currentYear)).substring(0,2) + expirationDate.substring(3,5));
        return currentYear <= year && currentMonth <= month;
    }
}
