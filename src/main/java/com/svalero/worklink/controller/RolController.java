package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.RolInDto;
import com.svalero.worklink.Dto.RolOutDto;
import com.svalero.worklink.exception.ErrorResponse;
import com.svalero.worklink.exception.RolNotFoundException;
import com.svalero.worklink.service.RolService;
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
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping("/rol")
    public List<RolOutDto> getAll(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "accessLevel", required = false) Float accessLevel,
                                  @RequestParam(value = "active", required = false) Boolean active) {
        return rolService.findAll(name, accessLevel, active);
    }

    @GetMapping("/rol/{id}")
    public ResponseEntity<RolOutDto> getRolId(@PathVariable Long id) throws RolNotFoundException {
        RolOutDto rol = rolService.findById(id);

        return ResponseEntity.ok(rol);
    }

    @PostMapping("/rol")
    public ResponseEntity<RolOutDto> addRol(@Valid @RequestBody RolInDto rol) throws RolNotFoundException {
        RolOutDto newRol = rolService.addRol(rol);
        return new ResponseEntity<RolOutDto>(newRol, HttpStatus.CREATED);
    }

    @PutMapping("/rol/{id}")
    public ResponseEntity<RolOutDto> updateRol(@PathVariable Long id, @Valid @RequestBody RolInDto rol) throws RolNotFoundException {
        RolOutDto newRol = rolService.modifyRol(id, rol);
        return ResponseEntity.ok(newRol);
    }

    @DeleteMapping("/rol/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) throws RolNotFoundException {
        rolService.deleteRol(id);
        return ResponseEntity.noContent().build();
    }

    // MANEJO DE ERRORES
    @ExceptionHandler(RolNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(RolNotFoundException rnfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The rol does not exist");
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

