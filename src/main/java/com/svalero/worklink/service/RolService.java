package com.svalero.worklink.service;

import com.svalero.worklink.Dto.RolInDto;
import com.svalero.worklink.Dto.RolInV2Dto;
import com.svalero.worklink.Dto.RolOutDto;
import com.svalero.worklink.exception.RolNotFoundException;
import com.svalero.worklink.model.Rol;
import com.svalero.worklink.repository.RolRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private ModelMapper modelMapper;

    // GET CON VERSIONADO
    public List<RolOutDto> findAll(String name, Float accessLevel, Boolean active) throws RolNotFoundException {
        List<Rol> rols = rolRepository.findAll();
        if (name != null && !name.isBlank()) {
            rols = rols.stream()
                    .filter(rol -> name.equals(rol.getName()))
                    .toList();
        }
        if (accessLevel != null) {
            rols = rols.stream()
                    .filter(rol -> accessLevel.equals(rol.getAccessLevel()))
                    .toList();
        }
        if (active != null) {
            rols = rols.stream()
                    .filter(rol -> active.equals(rol.getActive()))
                    .toList();
        }

        return modelMapper.map(rols, new TypeToken<List<RolOutDto>>() {}.getType());
    }

    public List<RolOutDto> findAllV2(String name, Float accessLevel, Boolean active) throws RolNotFoundException {
        List<Rol> rols = rolRepository.findAll();

        if (name != null && !name.isBlank()) {
            String nameFilter = name.toLowerCase();

            rols = rols.stream()
                    .filter(rol -> rol.getName() != null && rol.getName().toLowerCase().contains(nameFilter))
                    .toList();
        }

        if (accessLevel != null) {
            rols = rols.stream()
                    .filter(rol -> accessLevel.equals(rol.getAccessLevel()))
                    .toList();
        }

        if (active != null) {
            rols = rols.stream()
                    .filter(rol -> active.equals(rol.getActive()))
                    .toList();
        }

        return modelMapper.map(rols, new TypeToken<List<RolOutDto>>() {}.getType());
    }
    // -----------------------------------------------------------------------------------------------------

    public RolOutDto findById(Long id) throws RolNotFoundException {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));
        return modelMapper.map(rol, RolOutDto.class);
    }

    // POST CON VERSIONADO
    public RolOutDto addRol(RolInDto rol) throws RolNotFoundException {
        Rol newRol = modelMapper.map(rol, Rol.class);
        Rol savedRol = rolRepository.save(newRol);
        return modelMapper.map(savedRol, RolOutDto.class);
    }

    public RolOutDto addRolV2(RolInDto rol) throws RolNotFoundException {
        if (rolRepository.existsByNameIgnoreCase(rol.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        Rol newRol = modelMapper.map(rol, Rol.class);
        Rol savedRol = rolRepository.save(newRol);
        return modelMapper.map(savedRol, RolOutDto.class);
    }
    // -------------------------------------------------------------------------------------------------------------------------------

    // PUT CON VERSIONADO
    public RolOutDto modifyRol(Long id, RolInDto rol) throws RolNotFoundException {
        System.out.println(">>> EJECUTANDO MODIFY ROL V1 - SIN ACTIVE");
        Rol existingRol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));

        existingRol.setName(rol.getName());
        existingRol.setDescription(rol.getDescription());
        existingRol.setAccessLevel(rol.getAccessLevel());

        Rol savedRol = rolRepository.save(existingRol);
        return modelMapper.map(savedRol, RolOutDto.class);
    }

    public RolOutDto modifyRolV2(Long id, RolInV2Dto rol) throws RolNotFoundException {
        Rol existingRol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));

        boolean nameExists = rolRepository.findAll().stream()
                .anyMatch(otherRol ->
                        !otherRol.getId().equals(id) &&
                                otherRol.getName() != null &&
                                rol.getName() != null &&
                                otherRol.getName().equalsIgnoreCase(rol.getName()));

        if (nameExists) {
            throw new IllegalArgumentException("Role name already exists");
        }

        existingRol.setName(rol.getName());
        existingRol.setDescription(rol.getDescription());
        existingRol.setAccessLevel(rol.getAccessLevel());
        existingRol.setActive(rol.getActive());

        Rol savedRol = rolRepository.save(existingRol);
        return modelMapper.map(savedRol, RolOutDto.class);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------

    // DELETE CON VERSIONADO
    public void deleteRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));

        rolRepository.deleteById(id);
    }

    public RolOutDto deleteRolV2(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));

        rol.setActive(false);

        Rol savedRol = rolRepository.save(rol);
        return modelMapper.map(savedRol, RolOutDto.class);
    }
}
