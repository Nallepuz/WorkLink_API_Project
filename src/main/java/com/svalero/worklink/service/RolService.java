package com.svalero.worklink.service;

import com.svalero.worklink.Dto.RolInDto;
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

    // GET
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

    public RolOutDto findById(Long id) throws RolNotFoundException {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));
        return modelMapper.map(rol, RolOutDto.class);
    }

    // POST
    public RolOutDto addRol(RolInDto rol) throws RolNotFoundException {
        Rol newRol = modelMapper.map(rol, Rol.class);
        Rol savedRol = rolRepository.save(newRol);
        return modelMapper.map(savedRol, RolOutDto.class);
    }

    // PUT
    public RolOutDto modifyRol(Long id, RolInDto rol) throws RolNotFoundException {
        Rol existingRol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));

        modelMapper.map(rol, existingRol);

        Rol savedRol = rolRepository.save(existingRol);
        return modelMapper.map(savedRol, RolOutDto.class);
    }

    // DELETE
    public void deleteRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found"));

        rolRepository.deleteById(id);
    }
}
