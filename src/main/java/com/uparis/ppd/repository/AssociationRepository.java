package com.uparis.ppd.repository;

import com.uparis.ppd.model.Association;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AssociationRepository extends CrudRepository<Association, Long> {

    @Override
    List<Association> findAll();

    Optional<Association> findById(Long id);

    Association findByName(String name);
}