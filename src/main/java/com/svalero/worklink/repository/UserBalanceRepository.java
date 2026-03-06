package com.svalero.worklink.repository;

import com.svalero.worklink.model.ApplicationType;
import com.svalero.worklink.model.User;
import com.svalero.worklink.model.UserBalance;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserBalanceRepository extends CrudRepository<UserBalance, Long> {

    List<UserBalance> findAll();
    Optional<UserBalance> findByUserId(Long userId);
    List<UserBalance> findByYear(Integer year);
    Optional<UserBalance> findByUserIdAndYear(Long userId, Integer year);
}
