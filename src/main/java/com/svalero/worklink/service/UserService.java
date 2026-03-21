package com.svalero.worklink.service;

import com.svalero.worklink.Dto.LoginInDto;
import com.svalero.worklink.Dto.LoginOutDto;
import com.svalero.worklink.Dto.UserInDto;
import com.svalero.worklink.Dto.UserOutDto;
import com.svalero.worklink.exception.EmailAlreadyExistException;
import com.svalero.worklink.exception.RolNotFoundException;
import com.svalero.worklink.exception.UserNotFoundException;
import com.svalero.worklink.model.Rol;
import com.svalero.worklink.model.User;
import com.svalero.worklink.model.UserBalance;
import com.svalero.worklink.repository.RolRepository;
import com.svalero.worklink.repository.UserBalanceRepository;
import com.svalero.worklink.repository.UserRepository;
import com.svalero.worklink.security.JwtService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private UserBalanceRepository userBalanceRepository;
    @Autowired
    private JwtService jwtService;

    // GET
    public List<UserOutDto> findAll(String email, String name, Boolean active) throws UserNotFoundException {

        List<User> users =userRepository.findAll();

        if (email != null && !email.isBlank()) {
            users = users.stream()
                    .filter(user -> email.equals(user.getEmail()))
                    .toList();
        }
        if (name != null && !name.isBlank()) {
            users = users.stream()
                    .filter(user -> name.equals(user.getName()))
                    .toList();
        }
        if (active != null) {
            users = users.stream()
                    .filter(user -> user.isActive() == active)
                    .toList();
        }

        return modelMapper.map(users, new TypeToken<List<UserOutDto>>() {}.getType());
    }

    public UserOutDto findById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return modelMapper.map(user, UserOutDto.class);
    }

    // POST
    public User addUser(UserInDto user) throws RolNotFoundException {
        Long rolId = user.getRolId();
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RolNotFoundException("Rol Not Found"));

        User newUser = modelMapper.map(user, User.class);
        newUser.setRol(rol);
        User savedUser = userRepository.save(newUser);

        UserBalance userBalance = new UserBalance();
        userBalance.setUser(savedUser);
        userBalance.setVacationDays(40);
        userBalance.setExcessDays(2);
        userBalance.setUnpaidDays(2);
        userBalance.setHoursBalance(24);
        userBalance.setYear(LocalDateTime.now().getYear());
        userBalanceRepository.save(userBalance);

        return userRepository.save(newUser);
    }

    public LoginOutDto login(LoginInDto login) throws UserNotFoundException {

        User user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        if (!user.getPassword().equals(login.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        LoginOutDto loginOutDto = modelMapper.map(user, LoginOutDto.class);

        if (user.getRol() != null) {
            loginOutDto.setRoleId(user.getRol().getId());
        }

        String token = jwtService.generateToken(user.getEmail());
        loginOutDto.setToken(token);

        return loginOutDto;
    }

    // PUT
    public UserOutDto modifyUser(Long id, UserInDto user) throws UserNotFoundException, RolNotFoundException, EmailAlreadyExistException {
        User existingUser= userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        Long rolId = user.getRolId();
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RolNotFoundException("Rol Not Found"));

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(existingUser.getPassword());
        }

        Optional<User> userEmail = userRepository.findByEmail(user.getEmail());
        if (userEmail.isPresent() && !userEmail.get().getId().equals(id)) {
            throw new EmailAlreadyExistException("Email Already Exist");
        }

        modelMapper.map(user, existingUser);
        existingUser.setRol(rol);

        User savedUser = userRepository.save(existingUser);
        return modelMapper.map(savedUser, UserOutDto.class);
    }

    // DELETE
    public void deleteUser(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
