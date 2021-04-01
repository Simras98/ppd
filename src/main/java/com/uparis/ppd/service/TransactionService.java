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

    @Autowired
    private MemberService memberService;

    public Transaction create(Member member, String duration) {
        long time = System.currentTimeMillis();
        Transaction transaction;
        switch (duration) {
            case "1":
                transaction = new Transaction(member, time, 14.99);
                member.setEndSubscription(time + (60*1000));
                // member.setEndSubscription(time + ((31556952L / 12) * 1000));
                break;
            case "3":
                transaction = new Transaction(member, time, 42.99);
                member.setEndSubscription(time + (60*1000));
                // member.setEndSubscription(time + ((3 * 31556952L / 12) * 1000));
                break;
            case "12":
                transaction = new Transaction(member, time, 149.99);
                member.setEndSubscription(time + (60*1000));
                // member.setEndSubscription(time + ((12 * 31556952L / 12) * 1000));
                break;
            default:
                return null;
        }
        transactionRepository.save(transaction);
        member.setStartSubscription(time);
        member.setDelaySubscription(0);
        member.setDelayedSubscription(false);
        memberService.update(member);
        return transaction;
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
