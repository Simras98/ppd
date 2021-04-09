package com.uparis.ppd.repository;

import com.uparis.ppd.model.Association;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.model.Subscription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    @Override
    List<Subscription> findAll();

    Optional<Subscription> findById(Long id);

    Subscription findByMemberAndAssociation(Member member, Association association);

    List<Subscription> findByAssociation(Association association);
}
