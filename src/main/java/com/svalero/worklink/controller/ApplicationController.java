package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.ApplicationInDto;
import com.svalero.worklink.Dto.ApplicationOutDto;
import com.svalero.worklink.exception.ApplicationNotFoundException;
import com.svalero.worklink.exception.ErrorResponse;
import com.svalero.worklink.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/application")
    public ResponseEntity<List<ApplicationOutDto>> getAll(@RequestParam(value = "status", required = false) String status,
                                                          @RequestParam(value = "user_id", required = false) Long userId) throws ApplicationNotFoundException {
        List<ApplicationOutDto> application = applicationService.findAll(status, userId);

        return ResponseEntity.ok(application);
    }

    @GetMapping("/application/{id}")
    public ResponseEntity<ApplicationOutDto> getApplicationId(@PathVariable Long id) throws ApplicationNotFoundException {
        ApplicationOutDto application = applicationService.findById(id);

        return ResponseEntity.ok(application);
    }

    @PostMapping("/application")
    public ResponseEntity<ApplicationOutDto> addApplication(@Valid @RequestBody ApplicationInDto application) throws ApplicationNotFoundException {
        ApplicationOutDto newApplication = applicationService.addApplication(application);
        return new ResponseEntity<ApplicationOutDto>(newApplication, HttpStatus.CREATED);
    }

    @PutMapping("/application/{id}")
    public ResponseEntity<ApplicationOutDto> updateApplication(@PathVariable Long id, @Valid @RequestBody ApplicationInDto application) throws ApplicationNotFoundException {
        ApplicationOutDto newApplication = applicationService.modifyApplication(id, application);
        return ResponseEntity.ok(newApplication);
    }

    @DeleteMapping("/application/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) throws ApplicationNotFoundException {
        applicationService.deleteApp(id);
        return ResponseEntity.noContent().build();
    }


    // ***************************************************
    @GetMapping("/application/user/{userId}")
    public ResponseEntity<List<ApplicationOutDto>> getByUser(@PathVariable Long userId) {
        List<ApplicationOutDto> applications = applicationService.findByUser(userId);
        return ResponseEntity.ok(applications);
    }
    // ***************************************************

    // MANEJO DE ERRORES
    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ApplicationNotFoundException anfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The application does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
