package com.svalero.worklink.repository;

import com.svalero.worklink.model.TurnAssigned;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TurnAssignedRepository extends CrudRepository<TurnAssigned, Integer> {

    List<TurnAssigned> findAll();
    Optional<TurnAssigned> findById(Long id);
}
