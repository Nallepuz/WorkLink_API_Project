package com.svalero.worklink.service;

import com.svalero.worklink.Dto.TurnAssignedInDto;
import com.svalero.worklink.Dto.TurnAssignedOutDto;
import com.svalero.worklink.exception.TurnsAssignedNotFoundException;
import com.svalero.worklink.model.TurnAssigned;
import com.svalero.worklink.model.Turns;
import com.svalero.worklink.model.User;
import com.svalero.worklink.repository.TurnAssignedRepository;
import com.svalero.worklink.repository.TurnRepository;
import com.svalero.worklink.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnAssignedService {

    @Autowired
    private TurnAssignedRepository turnAssignedRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TurnRepository turnRepository;

    // GET
    public List<TurnAssignedOutDto> findAll(Long userId, Long turnId)throws TurnsAssignedNotFoundException {
        List<TurnAssigned> assigned = turnAssignedRepository.findAll();

        if (userId != null) {
            assigned = assigned.stream()
                    .filter(a -> a.getUser().getId().equals(userId))
                    .toList();
        }

        if (turnId != null) {
            assigned = assigned.stream()
                    .filter(a -> a.getTurn().getId().equals(turnId))
                    .toList();
        }

        return modelMapper.map(assigned, new TypeToken<List<TurnAssignedOutDto>>() {}.getType());
    }

    public TurnAssignedOutDto findById(Long id)throws TurnsAssignedNotFoundException {
        TurnAssigned assigned = turnAssignedRepository.findById(id)
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn assigned not found"));

        return modelMapper.map(assigned, TurnAssignedOutDto.class);
    }

    // POST
    public TurnAssignedOutDto addAssigned(TurnAssignedInDto assigned)throws TurnsAssignedNotFoundException {
        User user = userRepository.findById(assigned.getUserId())
                .orElseThrow(() -> new TurnsAssignedNotFoundException("User not found"));

        Turns turn = turnRepository.findById(assigned.getTurnId())
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn not found"));

        TurnAssigned newAssigned = modelMapper.map(assigned, TurnAssigned.class);
        newAssigned.setUser(user);
        newAssigned.setTurn(turn);

        return modelMapper.map(turnAssignedRepository.save(newAssigned), TurnAssignedOutDto.class);
    }

    // PUT
    public TurnAssignedOutDto modify(Long id, TurnAssignedInDto assigned)throws TurnsAssignedNotFoundException {
        TurnAssigned existingAssigned = turnAssignedRepository.findById(id)
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn assigned not found"));

        User user = userRepository.findById(assigned.getUserId())
                .orElseThrow(() -> new TurnsAssignedNotFoundException("User not found"));
        Turns turn = turnRepository.findById(assigned.getTurnId())
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn not found"));

        modelMapper.map(assigned, existingAssigned);

        existingAssigned.setUser(user);
        existingAssigned.setTurn(turn);

        TurnAssigned savedAssigned = turnAssignedRepository.save(existingAssigned);
        return modelMapper.map(existingAssigned, TurnAssignedOutDto.class);
    }

    // DELETE
    public void deleteAssigned(Long id)throws TurnsAssignedNotFoundException {
        TurnAssigned assigned = turnAssignedRepository.findById(id)
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn assigned not found"));

        turnAssignedRepository.deleteById(id);
    }
}
