package com.svalero.worklink.service;

import com.svalero.worklink.Dto.ApplicationTypeInDto;
import com.svalero.worklink.Dto.ApplicationTypeOutDto;
import com.svalero.worklink.exception.ApplicationTypeNotFoundException;
import com.svalero.worklink.model.ApplicationType;
import com.svalero.worklink.repository.ApplicationTypeRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationTypeService {

    @Autowired
    private ApplicationTypeRepository applicationTypeRepository;
    @Autowired
    private ModelMapper modelMapper;

    // GET
    public List<ApplicationTypeOutDto> findAll(String name, Boolean active) {

        List<ApplicationType> types = applicationTypeRepository.findAll();

        if (name != null && !name.isEmpty()) {
            types = types.stream()
                    .filter(type -> name.equals(type.getName()))
                    .toList();
        }
        if (active != null) {
            types = types.stream()
                    .filter(type -> active.equals(type.isActive()))
                    .toList();
        }

        return modelMapper.map(types, new TypeToken<List<ApplicationTypeOutDto>>() {}.getType());
    }

    public ApplicationTypeOutDto findById(Long id) throws ApplicationTypeNotFoundException {
        ApplicationType type = applicationTypeRepository.findById(id)
                .orElseThrow(() -> new ApplicationTypeNotFoundException("Application Type Not Found"));

        return modelMapper.map(type, ApplicationTypeOutDto.class);
    }

    // POST
    public ApplicationTypeOutDto addType(ApplicationTypeInDto type) throws ApplicationTypeNotFoundException {
        ApplicationType newType = modelMapper.map(type, ApplicationType.class);
        ApplicationType savedType = applicationTypeRepository.save(newType);

        return modelMapper.map(savedType, ApplicationTypeOutDto.class);
    }

    // PUT
    public ApplicationTypeOutDto modifyType(Long id, ApplicationTypeInDto type) throws ApplicationTypeNotFoundException {
        ApplicationType existingType = applicationTypeRepository.findById(id)
                .orElseThrow(() -> new ApplicationTypeNotFoundException("Application Type Not Found"));

        modelMapper.map(type, existingType);

        ApplicationType savedType = applicationTypeRepository.save(existingType);
        return modelMapper.map(savedType, ApplicationTypeOutDto.class);
    }

    // DELETE
    public void deleteType(Long id) throws ApplicationTypeNotFoundException {
        ApplicationType type = applicationTypeRepository.findById(id)
                .orElseThrow(() -> new ApplicationTypeNotFoundException("Application Type Not Found"));

        applicationTypeRepository.delete(type);
    }
}
