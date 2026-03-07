package com.svalero.worklink.service;

import com.svalero.worklink.Dto.ApplicationInDto;
import com.svalero.worklink.Dto.ApplicationOutDto;
import com.svalero.worklink.exception.ApplicationNotFoundException;
import com.svalero.worklink.exception.ApplicationTypeNotFoundException;
import com.svalero.worklink.model.Application;
import com.svalero.worklink.model.ApplicationStatus;
import com.svalero.worklink.model.ApplicationType;
import com.svalero.worklink.model.User;
import com.svalero.worklink.repository.ApplicationRepository;
import com.svalero.worklink.repository.ApplicationTypeRepository;
import com.svalero.worklink.repository.RolRepository;
import com.svalero.worklink.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ApplicationTypeRepository applicationTypeRepository;
    @Autowired
    private UserRepository userRepository;

    // GET
    public List<ApplicationOutDto> findAll(String status, Long userId) throws ApplicationNotFoundException {
        List<Application> applications = applicationRepository.findAll();

        if (status != null) {
            applications = applications.stream()
                    .filter(application -> application.getStatus().equals(status))
                    .toList();
        }
        if (userId != null) {
            applications = applications.stream()
                    .filter(application -> application.getUser().getId().equals(userId))
                    .toList();
        }

        return modelMapper.map(applications, new TypeToken<List<ApplicationOutDto>>() {
        }.getType());
    }

    public ApplicationOutDto findById(Long id) throws ApplicationNotFoundException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        return modelMapper.map(application, ApplicationOutDto.class);
    }

    public List<ApplicationOutDto> findByUser(Long userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        return modelMapper.map(applications, new TypeToken<List<ApplicationOutDto>>() {
        }.getType());
    }

    // POST
    public ApplicationOutDto addApplication(ApplicationInDto application) throws ApplicationNotFoundException {

        User user = userRepository.findById(application.getUserId())
                .orElseThrow(() -> new ApplicationNotFoundException("User not found"));
        ;
        ApplicationType type = applicationTypeRepository.findById(application.getApplicationTypeId())
                .orElseThrow(() -> new ApplicationTypeNotFoundException("Application type not found"));

        validateApplicationBytype(application, type);

        Application newApp = modelMapper.map(application, Application.class);
        newApp.setUser(user);
        newApp.setApplicationType(type);
        newApp.setStatus(ApplicationStatus.PENDING);
        newApp.setResolver(null);
        newApp.setResolved(null);

        Application savedApp = applicationRepository.save(newApp);

        return modelMapper.map(savedApp, ApplicationOutDto.class);
    }

    private void validateApplicationBytype(ApplicationInDto application, ApplicationType type) {
        String typeName = type.getName();
        switch (typeName) {
            case "Vacaciones":
            case "Dias Exceso":
            case "No Retribuido":
                if (application.getStartDate() == null || application.getEndDate() == null) {
                    throw new IllegalArgumentException("StartDate and EndDate are required");
                }
                if (application.getStartDate().isAfter(application.getEndDate())) {
                    throw new IllegalArgumentException("StartDate cannot be after EndDate");
                }
                break;

            case "Bolsa de horas":
                if (application.getHoursRequested() == null || application.getDate() == null || application.getFromTime() == null || application.getToTime() == null) {
                    throw new IllegalArgumentException("Date and time range are required");
                }
                if (application.getFromTime().isAfter(application.getToTime()) ||
                        application.getFromTime().equals(application.getToTime())) {
                    throw new IllegalArgumentException("FromTime must be before ToTime");
                }
                break;

            case "Cambio de Turno":
                if (application.getStartDate() == null || application.getEndDate() == null ||
                        application.getAffectedUserId() == null || application.getTurnGiveId() == null ||
                        application.getTurnReceiveId() == null) {
                    throw new IllegalArgumentException("StartDate, EndDate, affectedUserId, turnGiveId and turnReceiveId are required");
                }
                if (application.getStartDate().isAfter(application.getEndDate())) {
                    throw new IllegalArgumentException("StartDate cannot be after EndDate");
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown application type");
        }
    }

    // PUT
    public ApplicationOutDto modifyApplication(Long id, ApplicationInDto application) throws ApplicationNotFoundException {
        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        User resolver = null;
        if (application.getResolverId() != null) {
            resolver = userRepository.findById(application.getResolverId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Resolver not found"));
        }

        modelMapper.map(application, existingApplication);

        existingApplication.setResolver(resolver);
        existingApplication.setResolved(LocalDateTime.now());
        existingApplication.setFromTime(application.getFromTime());
        existingApplication.setToTime(application.getToTime());

        Application savedApplication = applicationRepository.save(existingApplication);

        return modelMapper.map(savedApplication, ApplicationOutDto.class);
    }

    // DELETE
    public void deleteApp(Long id) throws ApplicationNotFoundException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));
        ;

        applicationRepository.deleteById(id);
    }
}
