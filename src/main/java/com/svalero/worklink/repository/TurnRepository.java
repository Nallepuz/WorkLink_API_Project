package com.svalero.worklink.repository;

import com.svalero.worklink.model.Turns;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TurnRepository extends CrudRepository<Turns, Long> {

    List<Turns> findAll();
    Optional<Turns> findById(Long id);
    List<Turns> findByName(String name);
    List<Turns> findByNights(Boolean nights);
}
