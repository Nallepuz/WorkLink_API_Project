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

        if (!application.getApplicationType().getName().equals("Cambio de Turno")) {
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

        if (type.getName().equals("Cambio de Turno")) {
            User affectedUser = userRepository.findById(application.getAffectedUserId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Affected user not found"));

            Turns turnGive = turnRepository.findById(application.getTurnGiveId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Turn give not found"));

            Turns turnReceive = turnRepository.findById(application.getTurnReceiveId())
                    .orElseThrow(() -> new ApplicationNotFoundException("Turn receive not found"));

            newApp.setAffectedUser(affectedUser);
            newApp.setTurnGive(turnGive);
            newApp.setTurnReceive(turnReceive);
        }

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
    public ApplicationOutDto modifyApplication(Long id, ApplicationInDto application) throws
            ApplicationNotFoundException {
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
        return modelMapper.map(savedApplication, ApplicationOutDto.class);
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

            case "Cambio de Turno":
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
            if (balance.getVacationDays() < days) {
                throw new RuntimeException("You have " + balance.getVacationDays() + " days");
            }
            balance.setVacationDays(balance.getVacationDays() - (int) days);
        }
        if (type.equals("Dias Exceso")) {
            if (balance.getExcessDays() < days) {
                throw new RuntimeException("You have " + balance.getExcessDays() + " days");
            }
            balance.setExcessDays(balance.getExcessDays() - (int) days);
        }
        if (type.equals("No Retribuido")) {
            if (balance.getUnpaidDays() < days) {
                throw new RuntimeException("You have " + balance.getUnpaidDays() + " days");
            }
            balance.setUnpaidDays(balance.getUnpaidDays() - (int) days);
        }

        userBalanceRepository.save(balance);

        // Cambiar en Calendario a V
        List<TurnAssigned> turns = turnAssignedRepository
                .findByUserIdAndDateBetween(
                        userId,
                        existingApplication.getStartDate(),
                        existingApplication.getEndDate());

        Turns changeTurn = turnRepository.findByName("Vacaciones")
                .orElseThrow(() -> new RuntimeException("Turn Vacaciones not found"));

        for (TurnAssigned t : turns) {
            t.setTurn(changeTurn);
        }

        turnAssignedRepository.saveAll(turns);
    }

    public void applyHoursEffect(Application existingApplication) {

        Long userId = existingApplication.getUser().getId();
        int year = existingApplication.getDate().getYear();
        double hours = existingApplication.getHoursRequested();

        UserBalance balance = userBalanceRepository
                .findByUserIdAndYear(userId, year)
                .orElseThrow();

        if (balance.getHoursBalance() < hours) {
            throw new RuntimeException("You have " + balance.getHoursBalance() + " hours");
        }

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

        // Si el turno que DA el afectado es Vacaciones, se le devuelven (porque ya no las gasta)
        if (app.getTurnGive().getName().equalsIgnoreCase("Vacaciones")) {
            updateBalance(user.getId(), year, totalDays);
        }
        // Si el turno que RECIBE el solicitante es Vacaciones, se le restan a él
        if (app.getTurnReceive().getName().equalsIgnoreCase("Vacaciones")) {
            updateBalance(user.getId(), year, -totalDays);
        }

        // Si lo que Kipi RECIBE son Vacaciones -> SE LE QUITAN (Resta)
        if (app.getTurnGive().getName().trim().equalsIgnoreCase("Vacaciones")) {
            updateBalance(affectedUser.getId(), year, -totalDays);
        }
        // Si lo que Kipi SUELTA son Vacaciones -> SE LE DEVUELVEN (Suma)
        if (app.getTurnReceive().getName().trim().equalsIgnoreCase("Vacaciones")) {
            updateBalance(affectedUser.getId(), year, totalDays);
        }

        // --- INTERCAMBIO EN CALENDARIO ---
        // (Esto se queda igual porque solo mueves las piezas)
        List<TurnAssigned> userTurns = turnAssignedRepository.findByUserIdAndDateBetween(user.getId(), app.getStartDate(), app.getEndDate());
        List<TurnAssigned> affectedTurns = turnAssignedRepository.findByUserIdAndDateBetween(affectedUser.getId(), app.getStartDate(), app.getEndDate());

        for (TurnAssigned ut : userTurns) ut.setTurn(app.getTurnReceive());
        for (TurnAssigned at : affectedTurns) at.setTurn(app.getTurnGive());

        turnAssignedRepository.saveAll(userTurns);
        turnAssignedRepository.saveAll(affectedTurns);

    }

    private void updateBalance(Long userId, int year, int amount) {
        UserBalance balance = userBalanceRepository.findByUserIdAndYear(userId, year)
                .orElseThrow(() -> new RuntimeException("Balance no encontrado para usuario: " + userId));

        int currentDays = balance.getVacationDays();

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
