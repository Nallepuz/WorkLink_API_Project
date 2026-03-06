package com.svalero.worklink.repository;

import com.svalero.worklink.model.Rol;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends CrudRepository<Rol, Long> {

    List<Rol> findAll();
    Optional<Rol> findById(Long id);
    List<Rol> findByName(String name);
    List<Rol> findByAccessLevel(Float accessLevel);
    List<Rol> findByActive(Boolean active);
}
