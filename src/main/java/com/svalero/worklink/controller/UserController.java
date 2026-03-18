package com.svalero.worklink.controller;

import com.svalero.worklink.Dto.LoginInDto;
import com.svalero.worklink.Dto.LoginOutDto;
import com.svalero.worklink.Dto.UserInDto;
import com.svalero.worklink.Dto.UserOutDto;
import com.svalero.worklink.exception.ErrorResponse;
import com.svalero.worklink.exception.UserNotFoundException;
import com.svalero.worklink.model.User;
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
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserOutDto>> getAll(@RequestParam(value = "email", defaultValue = "") String email,
                                                   @RequestParam(value = "name", defaultValue = "") String name,
                                                   @RequestParam(value = "active", required = false) Boolean active) throws UserNotFoundException {
        List<UserOutDto> usersDto = userService.findAll(email, name, active);

        return ResponseEntity.ok(usersDto);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserOutDto> getUserId(@PathVariable Long id) throws UserNotFoundException {
        UserOutDto usersDto = userService.findById(id);

        return ResponseEntity.ok(usersDto);
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@Valid @RequestBody UserInDto user) throws UserNotFoundException {
        User newUser = userService.addUser(user);
        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOutDto> login(@RequestBody LoginInDto login) throws UserNotFoundException {

        LoginOutDto user = userService.login(login);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserOutDto> updateUser(@PathVariable Long id, @Valid  @RequestBody UserInDto user) throws UserNotFoundException {
        UserOutDto newUser = userService.modifyUser(id, user);
        return ResponseEntity.ok(newUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // MANEJO DE ERRORES
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The user does not exist");
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


