package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.TurnAssignedInDto;
import com.svalero.worklink.Dto.TurnAssignedOutDto;
import com.svalero.worklink.exception.ErrorResponse;
import com.svalero.worklink.exception.TurnsAssignedNotFoundException;
import com.svalero.worklink.service.TurnAssignedService;
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
public class TurnAssignedController {

    @Autowired
    private TurnAssignedService assignedService;

    @GetMapping("/assigned")
    public ResponseEntity<List<TurnAssignedOutDto>> getAll(@RequestParam(value = "user_id", required = false) Long userId,
                                                           @RequestParam(value = "turn_id", required = false) Long turnId)
            throws TurnsAssignedNotFoundException {
        List<TurnAssignedOutDto> assigned = assignedService.findAll(userId, turnId);
        return ResponseEntity.ok(assigned);
    }

    @GetMapping("/assigned/{id}")
    public ResponseEntity<TurnAssignedOutDto> getById(@PathVariable Long id) throws TurnsAssignedNotFoundException{
        TurnAssignedOutDto assigned = assignedService.findById(id);
        return ResponseEntity.ok(assigned);
    }

    @PostMapping("/assigned")
    public ResponseEntity<TurnAssignedOutDto> addAssigned(@Valid @RequestBody TurnAssignedInDto assigned)throws TurnsAssignedNotFoundException {
        TurnAssignedOutDto newAssigned = assignedService.addAssigned(assigned);
        return new ResponseEntity<TurnAssignedOutDto>(newAssigned, HttpStatus.CREATED);
    }

    @PutMapping("/assigned/{id}")
    public ResponseEntity<TurnAssignedOutDto> modify(@PathVariable Long id, @Valid @RequestBody TurnAssignedInDto assigned) throws TurnsAssignedNotFoundException{
        TurnAssignedOutDto newAssigned = assignedService.modify(id, assigned);
        return ResponseEntity.ok(newAssigned);
    }

    @DeleteMapping("/assigned/{id}")
    public ResponseEntity<Void> deleteAssigned(@PathVariable Long id) throws TurnsAssignedNotFoundException{
        assignedService.deleteAssigned(id);
        return ResponseEntity.noContent().build();
    }

    // MANEJO DE ERRORES
    @ExceptionHandler(TurnsAssignedNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(TurnsAssignedNotFoundException tanfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The turn assigned does not exist");
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
