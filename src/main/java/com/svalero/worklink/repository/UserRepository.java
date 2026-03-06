package com.svalero.worklink.repository;

import com.svalero.worklink.model.User;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findByName(String name);
    List<User> findByActive(boolean active);
}
