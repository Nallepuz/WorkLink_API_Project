package com.svalero.worklink.repository;

import com.svalero.worklink.model.Application;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends CrudRepository<Application, Integer> {

    List<Application> findAll();
    Optional<Application> findById(Long id);
    List<Application> findByUserId(Long userId);
    List<Application> findByStatus(String status);
    List<Application> findByApplicationTypeId(Long applicationTypeId);
}
