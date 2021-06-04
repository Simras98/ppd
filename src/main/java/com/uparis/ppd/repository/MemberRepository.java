package com.uparis.ppd.repository;

import com.uparis.ppd.model.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    @Override
    List<Member> findAll();

    Member findByEmail(String email);

    Optional<Member> findById(Long id);
}
