package com.svalero.worklink.service;

import com.svalero.worklink.Dto.UserBalanceInDto;
import com.svalero.worklink.Dto.UserBalanceOutDto;
import com.svalero.worklink.exception.UserBalanceNotFoundException;
import com.svalero.worklink.exception.UserNotFoundException;
import com.svalero.worklink.model.User;
import com.svalero.worklink.model.UserBalance;
import com.svalero.worklink.repository.UserBalanceRepository;
import com.svalero.worklink.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBalanceService {

    @Autowired
    UserBalanceRepository userBalanceRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;

    // GET
    public List<UserBalanceOutDto> findAll(Long userId, Integer year) throws UserBalanceNotFoundException {

        List<UserBalance> users = userBalanceRepository.findAll();

        if (userId != null) {
            users = users.stream()
                    .filter(user -> user.getUser().getId().equals(userId))
                    .toList();
        }
        if (year != null) {
            users = users.stream()
                    .filter(user -> year.equals(user.getYear()))
                    .toList();
        }


        return modelMapper.map(users, new TypeToken<List<UserBalanceOutDto>>() {
        }.getType());
    }

    public UserBalanceOutDto findById(Long id) throws UserBalanceNotFoundException {
        UserBalance user = userBalanceRepository.findById(id)
                .orElseThrow(() -> new UserBalanceNotFoundException("User Balance not found"));
        return modelMapper.map(user, UserBalanceOutDto.class);
    }

    // POST
    public UserBalanceOutDto addUser(UserBalanceInDto userBalance) throws UserBalanceNotFoundException {
        Long userId = userBalance.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        UserBalance newUser = modelMapper.map(userBalance, UserBalance.class);
        newUser.setUser(user);

        UserBalance savedUserBalance = userBalanceRepository.save(newUser);

        return modelMapper.map(savedUserBalance, UserBalanceOutDto.class);
    }

    // PUT
    public UserBalanceOutDto modifyUserBalance(Long id, UserBalanceInDto user) throws UserBalanceNotFoundException {
        UserBalance existingUser = userBalanceRepository.findById(id)
                .orElseThrow(() -> new UserBalanceNotFoundException("User Balance NotFound"));

        User userId = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserBalanceNotFoundException("User NotFound"));

        modelMapper.map(user, existingUser);
        existingUser.setUser(userId);

        UserBalance savedUser = userBalanceRepository.save(existingUser);

        return modelMapper.map(savedUser, UserBalanceOutDto.class);
    }

    // DELETE
    public void deleteUser(Long id) throws UserBalanceNotFoundException {
       userBalanceRepository.findById(id)
                .orElseThrow(() -> new UserBalanceNotFoundException("User Balance not found"));

       userBalanceRepository.deleteById(id);
    }
}
