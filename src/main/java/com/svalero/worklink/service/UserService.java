package com.svalero.worklink.service;

import com.svalero.worklink.Dto.UserInDto;
import com.svalero.worklink.Dto.UserOutDto;
import com.svalero.worklink.exception.EmailAlreadyExistException;
import com.svalero.worklink.exception.RolNotFoundException;
import com.svalero.worklink.exception.UserNotFoundException;
import com.svalero.worklink.model.Rol;
import com.svalero.worklink.model.User;
import com.svalero.worklink.repository.RolRepository;
import com.svalero.worklink.repository.UserRepository;
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

        return userRepository.save(newUser);
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
