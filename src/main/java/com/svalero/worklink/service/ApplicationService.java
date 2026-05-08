package com.svalero.worklink.service;

import com.svalero.worklink.Dto.*;
import com.svalero.worklink.exception.ApplicationNotFoundException;
import com.svalero.worklink.exception.ApplicationTypeNotFoundException;
import com.svalero.worklink.model.*;
import com.svalero.worklink.repository.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @Autowired
    private TurnRepository turnRepository;
    @Autowired
    private UserBalanceRepository userBalanceRepository;
    @Autowired
    private TurnAssignedRepository turnAssignedRepository;
    @Autowired
    private NotificationService notificationService;

    // GET
    public List<ApplicationOutDto> findAll(String status, Long userId) throws ApplicationNotFoundException {
        List<Application> applications = applicationRepository.findAll();

        if (status != null) {
            applications = applications.stream()
                    .filter(application -> application.getStatus().name().equals(status))
                    .toList();
        }
        if (userId != null) {
            applications = applications.stream()
                    .filter(application -> application.getUser().getId().equals(userId))
                    .toList();
        }

        return applications.stream().map(app -> {
            ApplicationOutDto dto = modelMapper.map(app, ApplicationOutDto.class);
            dto.setUserId(app.getUser().getId());
            dto.setUserName(app.getUser().getName());
            dto.setApplicationTypeId(app.getApplicationType().getId());
            if (app.getAffectedUser() != null) {
                dto.setAffectedUserId(app.getAffectedUser().getId());
                dto.setAffectedUserName(app.getAffectedUser().getName());
            }
            return dto;
        }).toList();
    }

    public ApplicationDaysOutDto findDayById(Long id) throws ApplicationNotFoundException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (!application.getApplicationType().getName().equals("Vacaciones")
                && !application.getApplicationType().getName().equals("Dias Exceso")
                && !application.getApplicationType().getName().equals("No Retribuido")) {
            throw new IllegalArgumentException("Application is not a days type");
        }

        ApplicationDaysOutDto app = modelMapper.map(application, ApplicationDaysOutDto.class);
        app.setApplicationTypeId(application.getApplicationType().getId());
        app.setUserId(application.getUser().getId());

        if (application.getResolver() != null) {
            app.setResolverId(application.getResolver().getId());
        }
        return app;
    }

    public ApplicationHoursOutDto findHourById(Long id) throws ApplicationNotFoundException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (!application.getApplicationType().getName().equals("Bolsa de horas")) {
            throw new IllegalArgumentException("Application is not a hours type");
        }

        ApplicationHoursOutDto app = modelMapper.map(application, ApplicationHoursOutDto.class);
        app.setApplicationTypeId(application.getApplicationType().getId());
        app.setUserId(application.getUser().getId());

        if (application.getResolver() != null) {
            app.setResolverId(application.getResolver().getId());
        }
        if (application.getUser() != null) {
            app.setResolverId(application.getUser().getId());
        }
        return app;
    }

    public ApplicationChangeOutDto findChangeById(Long id) throws ApplicationNotFoundException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (!application.getApplicationType().getName().equals("Cambio de turno")) {
            throw new IllegalArgumentException("Application is not a change type");
        }

        ApplicationChangeOutDto app = modelMapper.map(application, ApplicationChangeOutDto.class);
        app.setApplicationTypeId(application.getApplicationType().getId());
        app.setUserId(application.getUser().getId());

        if (application.getResolver() != null) {
            app.setResolverId(application.getResolver().getId());
        }
        if (application.getAffectedUser() != null) {
            app.setAffectedUserId(application.getAffectedUser().getId());
        }
        if (application.getTurnGive() != null) {
            app.setTurnGiveId(application.getTurnGive().getId());
        }
        if (application.getTurnReceive() != null) {
            app.setTurnReceiveId(application.getTurnReceive().getId());
        }
        return app;
    }

    public List<ApplicationOutDto> findByUser(Long userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        return applications.stream().map(app -> {
            ApplicationOutDto dto = modelMapper.map(app, ApplicationOutDto.class);
            dto.setUserId(app.getUser().getId());
            dto.setUserName(app.getUser().getName());
            dto.setApplicationTypeId(app.getApplicationType().getId());
            if (app.getAffectedUser() != null) {
                dto.setAffectedUserId(app.getAffectedUser().getId());
                dto.setAffectedUserName(app.getAffectedUser().getName());
            }
            return dto;
        }).toList();
    }

    // POST
    public ApplicationOutDto addApplication(ApplicationInDto application) throws ApplicationNotFoundException {

        User user = userRepository.findById(application.getUserId())
                .orElseThrow(() -> new ApplicationNotFoundException("User not found"));

        ApplicationType type = applicationTypeRepository.findById(application.getApplicationTypeId())
                .orElseThrow(() -> new ApplicationTypeNotFoundException("Application type not found"));

        validateApplicationBytype(application, type);

        Application newApp = modelMapper.map(application, Application.class);
        newApp.setUser(user);
        newApp.setApplicationType(type);
        newApp.setStatus(ApplicationStatus.PENDING);
        newApp.setResolver(null);
        newApp.setResolved(null);

        if (type.getName().equals("Cambio de turno")) {
            User affectedUser = userRepository.findById(application.getAffectedUserId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Affected user not found"));

            Turns turnGive = turnRepository.findById(application.getTurnGiveId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Turn give not found"));

            Turns turnReceive = turnRepository.findById(application.getTurnReceiveId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Turn receive not found"));

            newApp.setAffectedUser(affectedUser);
            newApp.setTurnGive(turnGive);
            newApp.setTurnReceive(turnReceive);

            // Notificar al afectado de la nueva solicitud de cambio
            notificationService.createNotification(
                    affectedUser.getId(),
                    user.getName() + " ha solicitado un cambio de turno contigo del " +
                            application.getStartDate() + " al " + application.getEndDate()
            );
        }

        Application savedApp = applicationRepository.save(newApp);

        ApplicationOutDto dto = modelMapper.map(savedApp, ApplicationOutDto.class);
        dto.setUserId(savedApp.getUser().getId());
        dto.setUserName(savedApp.getUser().getName());
        dto.setApplicationTypeId(savedApp.getApplicationType().getId());
        return dto;
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
                if (application.getHoursRequested() == null || application.getDate() == null ||
                        application.getFromTime() == null || application.getToTime() == null) {
                    throw new IllegalArgumentException("Date and time range are required");
                }
                if (application.getFromTime().equals(application.getToTime())) {
                    throw new IllegalArgumentException("FromTime and ToTime cannot be the same");
                }
                break;

            case "Cambio de turno":
                if (application.getStartDate() == null || application.getEndDate() == null ||
                        application.getAffectedUserId() == null || application.getTurnGiveId() == null ||
                        application.getTurnReceiveId() == null) {
                    throw new IllegalArgumentException("StartDate, EndDate, affectedUserId, turnGiveId and turnReceiveId are required");
                }
                if (application.getStartDate().isAfter(application.getEndDate())) {
                    throw new IllegalArgumentException("StartDate cannot be after EndDate");
                }
                validateShiftChangeConsistency(application);
                break;

            default:
                throw new IllegalArgumentException("Unknown application type");
        }
    }

    private void validateShiftChangeConsistency(ApplicationInDto application) {
        LocalDate start = application.getStartDate();
        LocalDate end = application.getEndDate();
        Long userId = application.getUserId();
        Long affectedUserId = application.getAffectedUserId();

        List<TurnAssigned> userTurns = turnAssignedRepository
                .findByUserIdAndDateBetween(userId, start, end);

        if (!userTurns.isEmpty()) {
            Long actualTurnId = userTurns.get(0).getTurn().getId();
            if (!actualTurnId.equals(application.getTurnGiveId())) {
                throw new IllegalArgumentException(
                        "El turno que cedes no coincide con tu turno real en esas fechas"
                );
            }
        }

        List<TurnAssigned> affectedTurns = turnAssignedRepository
                .findByUserIdAndDateBetween(affectedUserId, start, end);

        if (!affectedTurns.isEmpty()) {
            Long actualAffectedTurnId = affectedTurns.get(0).getTurn().getId();
            if (!actualAffectedTurnId.equals(application.getTurnReceiveId())) {
                throw new IllegalArgumentException(
                        "El turno que recibes no coincide con el turno real del compañero en esas fechas"
                );
            }
        }
    }

    // PUT
    public ApplicationOutDto modifyApplication(Long id, ApplicationPutInDto application) throws ApplicationNotFoundException {
        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        User resolver = null;
        if (application.getResolverId() != null) {
            resolver = userRepository.findById(application.getResolverId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Resolver not found"));
        }

        existingApplication.setStatus(application.getStatus());
        existingApplication.setResolver(resolver);
        existingApplication.setResolverComments(application.getResolverComments());
        existingApplication.setResolved(LocalDateTime.now());

        if (existingApplication.getStatus() == ApplicationStatus.APPROVED) {
            applyEffects(existingApplication);
        }

        Application savedApplication = applicationRepository.save(existingApplication);

        // Notificar al solicitante
        String statusText = existingApplication.getStatus() == ApplicationStatus.APPROVED ? "aprobada" : "rechazada";
        String typeName = existingApplication.getApplicationType().getName();

        notificationService.createNotification(
                existingApplication.getUser().getId(),
                "Tu solicitud de " + typeName + " ha sido " + statusText + "." +
                        (application.getResolverComments() != null && !application.getResolverComments().isBlank()
                                ? " Comentario: " + application.getResolverComments() : "")
        );

        // Notificar al afectado si es cambio de turno
        if (typeName.equals("Cambio de turno") && existingApplication.getAffectedUser() != null) {
            notificationService.createNotification(
                    existingApplication.getAffectedUser().getId(),
                    "El cambio de turno solicitado por " + existingApplication.getUser().getName() +
                            " ha sido " + statusText + "." +
                            (application.getResolverComments() != null && !application.getResolverComments().isBlank()
                                    ? " Comentario: " + application.getResolverComments() : "")
            );
        }

        ApplicationOutDto dto = modelMapper.map(savedApplication, ApplicationOutDto.class);
        dto.setUserId(savedApplication.getUser().getId());
        dto.setUserName(savedApplication.getUser().getName());
        dto.setApplicationTypeId(savedApplication.getApplicationType().getId());
        if (savedApplication.getAffectedUser() != null) {
            dto.setAffectedUserId(savedApplication.getAffectedUser().getId());
            dto.setAffectedUserName(savedApplication.getAffectedUser().getName());
        }
        return dto;
    }

    // CAMBIOS DE CALENDARIO
    public void applyEffects(Application existingApplication) {
        String typeName = existingApplication.getApplicationType().getName();
        switch (typeName) {
            case "Vacaciones":
            case "Dias Exceso":
            case "No Retribuido":
                applyDaysEffect(existingApplication);
                break;
            case "Bolsa de horas":
                applyHoursEffect(existingApplication);
                break;
            case "Cambio de turno":
                applyChangeEffect(existingApplication);
                break;
        }
    }

    public void applyDaysEffect(Application existingApplication) {
        Long userId = existingApplication.getUser().getId();
        int year = existingApplication.getStartDate().getYear();

        UserBalance balance = userBalanceRepository
                .findByUserIdAndYear(userId, year)
                .orElseThrow();

        long days = ChronoUnit.DAYS.between(
                existingApplication.getStartDate(),
                existingApplication.getEndDate()) + 1;

        String type = existingApplication.getApplicationType().getName();

        if (type.equals("Vacaciones")) {
            if (balance.getVacationDays() < days)
                throw new RuntimeException("No tienes suficientes días de vacaciones");
            balance.setVacationDays(balance.getVacationDays() - (int) days);
        }
        if (type.equals("Dias Exceso")) {
            if (balance.getExcessDays() < days)
                throw new RuntimeException("No tienes suficientes días de exceso");
            balance.setExcessDays(balance.getExcessDays() - (int) days);
        }
        if (type.equals("No Retribuido")) {
            if (balance.getUnpaidDays() < days)
                throw new RuntimeException("No tienes suficientes días no retribuidos");
            balance.setUnpaidDays(balance.getUnpaidDays() - (int) days);
        }

        userBalanceRepository.save(balance);

        List<TurnAssigned> turns = turnAssignedRepository
                .findByUserIdAndDateBetween(userId,
                        existingApplication.getStartDate(),
                        existingApplication.getEndDate());

        String turnName = switch (type) {
            case "Vacaciones" -> "Vacaciones";
            case "Dias Exceso" -> "Día Exceso";
            case "No Retribuido" -> "No Retribuido";
            default -> "Vacaciones";
        };

        Turns changeTurn = turnRepository.findByName(turnName)
                .orElseThrow(() -> new RuntimeException("Turn " + turnName + " not found"));

        for (TurnAssigned t : turns) t.setTurn(changeTurn);
        turnAssignedRepository.saveAll(turns);
    }

    public void applyHoursEffect(Application existingApplication) {
        Long userId = existingApplication.getUser().getId();
        int year = existingApplication.getDate().getYear();
        double hours = existingApplication.getHoursRequested();

        UserBalance balance = userBalanceRepository
                .findByUserIdAndYear(userId, year)
                .orElseThrow();

        if (balance.getHoursBalance() < hours)
            throw new RuntimeException("You have " + balance.getHoursBalance() + " hours");

        balance.setHoursBalance(balance.getHoursBalance() - (int) hours);
        userBalanceRepository.save(balance);

        List<TurnAssigned> turns = turnAssignedRepository.findByUserIdAndDateBetween(
                userId, existingApplication.getDate(), existingApplication.getDate());

        for (TurnAssigned t : turns) {
            String info = (t.getInfo() == null) ? "" : t.getInfo();
            t.setInfo(info + " [Balances Hours: " + hours + "h]");
        }
        turnAssignedRepository.saveAll(turns);
    }

    public void applyChangeEffect(Application app) {
        User user = app.getUser();
        User affectedUser = app.getAffectedUser();
        int totalDays = (int) ChronoUnit.DAYS.between(app.getStartDate(), app.getEndDate()) + 1;
        int year = app.getStartDate().getYear();

        if (app.getTurnGive().getName().equalsIgnoreCase("Vacaciones"))
            updateBalance(user.getId(), year, totalDays);
        if (app.getTurnReceive().getName().equalsIgnoreCase("Vacaciones"))
            updateBalance(user.getId(), year, -totalDays);
        if (app.getTurnGive().getName().trim().equalsIgnoreCase("Vacaciones"))
            updateBalance(affectedUser.getId(), year, -totalDays);
        if (app.getTurnReceive().getName().trim().equalsIgnoreCase("Vacaciones"))
            updateBalance(affectedUser.getId(), year, totalDays);

        List<TurnAssigned> userTurns = turnAssignedRepository
                .findByUserIdAndDateBetween(user.getId(), app.getStartDate(), app.getEndDate());
        List<TurnAssigned> affectedTurns = turnAssignedRepository
                .findByUserIdAndDateBetween(affectedUser.getId(), app.getStartDate(), app.getEndDate());

        for (TurnAssigned ut : userTurns) ut.setTurn(app.getTurnReceive());
        for (TurnAssigned at : affectedTurns) at.setTurn(app.getTurnGive());

        turnAssignedRepository.saveAll(userTurns);
        turnAssignedRepository.saveAll(affectedTurns);
    }

    private void updateBalance(Long userId, int year, int amount) {
        UserBalance balance = userBalanceRepository.findByUserIdAndYear(userId, year)
                .orElseThrow(() -> new RuntimeException("Balance no encontrado para usuario: " + userId));
        balance.setVacationDays(balance.getVacationDays() + amount);
        userBalanceRepository.saveAndFlush(balance);
    }

    // DELETE
    public void deleteApp(Long id) throws ApplicationNotFoundException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));
        applicationRepository.deleteById(id);
    }
}