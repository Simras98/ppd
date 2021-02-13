package com.uparis.ppd.repository;

import com.uparis.ppd.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

  User findByEmail(String email);

  Optional<User> findById(Long id);

  @Query("SELECT c FROM User c WHERE CONCAT(UPPER(c.firstName), UPPER(c.lastName)) LIKE %?1%")
  List<User> search(String keyword);

  @Override
  List<User> findAll();
}
