package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.UserBalanceInDto;
import com.svalero.worklink.Dto.UserBalanceOutDto;
import com.svalero.worklink.exception.ErrorResponse;
import com.svalero.worklink.exception.UserBalanceNotFoundException;
import com.svalero.worklink.repository.UserBalanceRepository;
import com.svalero.worklink.service.UserBalanceService;
import com.svalero.worklink.service.UserService;
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
public class UserBalanceController {

    @Autowired
    private UserBalanceService userBalanceService;

    @GetMapping("/userBalances")
    public ResponseEntity<List<UserBalanceOutDto>> getAll(@RequestParam(value = "user_id", required = false) Long userId,
                                                          @RequestParam(value = "year", required = false) Integer year)throws UserBalanceNotFoundException {
        List<UserBalanceOutDto> userBalanceDto = userBalanceService.findAll(userId, year);
        return ResponseEntity.ok(userBalanceDto);
    }
    @GetMapping("/userBalances/{id}")
    public ResponseEntity<UserBalanceOutDto> getUserId(@PathVariable Long id) throws UserBalanceNotFoundException {
        UserBalanceOutDto usersDto = userBalanceService.findById(id);

        return ResponseEntity.ok(usersDto);
    }

    @PostMapping("/userBalances")
    public ResponseEntity<UserBalanceOutDto> addUser(@Valid @RequestBody UserBalanceInDto user) throws UserBalanceNotFoundException {
        UserBalanceOutDto newUser = userBalanceService.addUser(user);
        return new ResponseEntity<UserBalanceOutDto>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/userBalances/{id}")
    public ResponseEntity<UserBalanceOutDto> updateUser(@PathVariable Long id, @Valid  @RequestBody UserBalanceInDto user) throws UserBalanceNotFoundException {
        UserBalanceOutDto newUser = userBalanceService.modifyUserBalance(id, user);
        return ResponseEntity.ok(newUser);
    }

    @DeleteMapping("/userBalances/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws UserBalanceNotFoundException {
        userBalanceService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // MANEJO DE ERRORES
    @ExceptionHandler(UserBalanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserBalanceNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The userBalance does not exist");
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
