package com.uparis.ppd.repository;

import com.uparis.ppd.model.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Member findByEmail(String email);

    Optional<Member> findById(Long id);

    @Query("SELECT m FROM Member m WHERE CONCAT(UPPER(m.firstName), UPPER(m.lastName)) LIKE %?1%")
    List<Member> search(String keyword);

    @Override
    List<Member> findAll();
}
