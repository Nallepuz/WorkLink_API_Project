package com.svalero.worklink.repository;

import com.svalero.worklink.model.TurnAssigned;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TurnAssignedRepository extends CrudRepository<TurnAssigned,Long> {

    List<TurnAssigned> findAll();
    Optional<TurnAssigned> findById(Long id);
    List<TurnAssigned> findByUserIdAndDateBetween(Long id, LocalDate startDate, LocalDate endDate);
}
