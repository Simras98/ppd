package com.uparis.ppd.repository;

import com.uparis.ppd.model.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends CrudRepository<Status, Long> {

    @Override
    List<Status> findAll();

    Optional<Status> findById(Long id);
}
