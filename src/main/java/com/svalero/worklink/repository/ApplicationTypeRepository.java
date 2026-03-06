package com.svalero.worklink.repository;

import com.svalero.worklink.model.ApplicationType;
import com.svalero.worklink.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationTypeRepository extends CrudRepository<ApplicationType, Long> {

    List<ApplicationType> findAll();
    Optional<ApplicationType> findById(Long id);
    List<ApplicationType> findByName(String name);
    List<ApplicationType> findByActive(boolean active);

}
