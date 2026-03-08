package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.TurnAssignedOutDto;
import com.svalero.worklink.Dto.TurnInDto;
import com.svalero.worklink.Dto.TurnOutDto;
import com.svalero.worklink.exception.ErrorResponse;
import com.svalero.worklink.exception.TurnsNotFoundException;
import com.svalero.worklink.service.TurnService;
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
public class TurnController {

    @Autowired
    private TurnService turnService;

    @GetMapping("/turns")
    public ResponseEntity<List<TurnOutDto>> getAll(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "color_hex", required = false) String colorHex,
                                                   @RequestParam(value = "nights", required = false) Boolean nights)
            throws TurnsNotFoundException {
        List <TurnOutDto> turn = turnService.findAll(name, colorHex, nights);

        return ResponseEntity.ok(turn);
    }

    @GetMapping("/turns/{id}")
    public ResponseEntity<TurnOutDto> getTurnId(@PathVariable Long id) throws TurnsNotFoundException {
        TurnOutDto turn = turnService.findById(id);

        return ResponseEntity.ok(turn);
    }

    @PostMapping("/turns")
    public ResponseEntity<TurnOutDto> addTurn(@Valid @RequestBody TurnInDto turn) throws TurnsNotFoundException {
        TurnOutDto newTurn = turnService.addTurn(turn);
        return new ResponseEntity<TurnOutDto>(newTurn, HttpStatus.CREATED);
    }

    @PutMapping("/turns/{id}")
    public ResponseEntity<TurnOutDto> updateTurn(@PathVariable Long id, @Valid @RequestBody TurnInDto turn) throws TurnsNotFoundException {
        TurnOutDto newTurn = turnService.modify(id, turn);
        return ResponseEntity.ok(newTurn);
    }

    @DeleteMapping("/turns/{id}")
    public ResponseEntity<Void> deleteTurn(@PathVariable Long id) throws TurnsNotFoundException {
        turnService.deleteTurn(id);
        return ResponseEntity.noContent().build();
    }

    // MANEJO DE ERRORES
    @ExceptionHandler(TurnsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(TurnsNotFoundException tnfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The turn does not exist");
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
