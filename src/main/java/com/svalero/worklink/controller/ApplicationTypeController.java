package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.ApplicationTypeInDto;
import com.svalero.worklink.Dto.ApplicationTypeOutDto;
import com.svalero.worklink.exception.ApplicationTypeNotFoundException;
import com.svalero.worklink.exception.ErrorResponse;
import com.svalero.worklink.service.ApplicationTypeService;
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
public class ApplicationTypeController {

    @Autowired
    private ApplicationTypeService applicationTypeService;

    @GetMapping("/applicationType")
    public ResponseEntity<List<ApplicationTypeOutDto>> getAll(@RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(value = "active", required = false) Boolean active) throws ApplicationTypeNotFoundException {
        List<ApplicationTypeOutDto> applicationType = applicationTypeService.findAll(name, active);

        return ResponseEntity.ok(applicationType);
    }

    @GetMapping("/applicationType/{id}")
    public ResponseEntity<ApplicationTypeOutDto> getApplicationId(@PathVariable Long id) throws ApplicationTypeNotFoundException {
        ApplicationTypeOutDto applicationType = applicationTypeService.findById(id);

        return ResponseEntity.ok(applicationType);
    }

    @PostMapping("/applicationType")
    public ResponseEntity<ApplicationTypeOutDto> addApplication(@Valid @RequestBody ApplicationTypeInDto application) throws ApplicationTypeNotFoundException {
        ApplicationTypeOutDto newApplication = applicationTypeService.addType(application);
        return new ResponseEntity<ApplicationTypeOutDto>(newApplication, HttpStatus.CREATED);
    }

    @PutMapping("/applicationType/{id}")
    public ResponseEntity<ApplicationTypeOutDto> updateApplication(@PathVariable Long id, @Valid @RequestBody ApplicationTypeInDto application) throws ApplicationTypeNotFoundException {
        ApplicationTypeOutDto newApplication = applicationTypeService.modifyType(id, application);
        return ResponseEntity.ok(newApplication);
    }

    @DeleteMapping("/applicationType/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) throws ApplicationTypeNotFoundException {
        applicationTypeService.deleteType(id);
        return ResponseEntity.noContent().build();
    }

    // MANEJO DE ERRORES
    @ExceptionHandler(ApplicationTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ApplicationTypeNotFoundException anfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The application type does not exist");
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
