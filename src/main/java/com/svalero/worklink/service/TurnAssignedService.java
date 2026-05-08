package com.svalero.worklink.service;

import com.svalero.worklink.Dto.TurnAssignedInDto;
import com.svalero.worklink.Dto.TurnAssignedOutDto;
import com.svalero.worklink.exception.TurnsAssignedNotFoundException;
import com.svalero.worklink.model.TurnAssigned;
import com.svalero.worklink.model.Turns;
import com.svalero.worklink.model.User;
import com.svalero.worklink.model.UserBalance;
import com.svalero.worklink.repository.TurnAssignedRepository;
import com.svalero.worklink.repository.TurnRepository;
import com.svalero.worklink.repository.UserBalanceRepository;
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
    @Autowired
    UserBalanceRepository userBalanceRepository;

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

        UserBalance balance = userBalanceRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("user balance has no vacations days"));

        if (turn.getName().toLowerCase().contains("vacaciones")) {
            if (balance.getVacationDays() > 0) {
                balance.setVacationDays(balance.getVacationDays() - 1);
                userBalanceRepository.save(balance); // Guardamos el balance actualizado
            } else {
                throw new RuntimeException("No quedan días de vacaciones disponibles");
            }
        }

        TurnAssigned newAssigned = modelMapper.map(assigned, TurnAssigned.class);
        newAssigned.setUser(user);
        newAssigned.setTurn(turn);
        TurnAssigned existingAssigned = turnAssignedRepository.save(newAssigned);

        TurnAssignedOutDto savedAssigned = modelMapper.map(existingAssigned, TurnAssignedOutDto.class);
        savedAssigned.setUserName(existingAssigned.getUser().getName());
        savedAssigned.setTurnName(existingAssigned.getTurn().getName());

        return savedAssigned;
    }

    // PUT
    public TurnAssignedOutDto modify(Long id, TurnAssignedInDto assigned) throws TurnsAssignedNotFoundException {
        TurnAssigned existingAssigned = turnAssignedRepository.findById(id)
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn assigned not found"));

        User user = userRepository.findById(assigned.getUserId())
                .orElseThrow(() -> new TurnsAssignedNotFoundException("User not found"));
        Turns newTurn = turnRepository.findById(assigned.getTurnId())
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn not found"));

        Turns oldTurn = existingAssigned.getTurn();
        UserBalance balance = userBalanceRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        // Si el turno anterior era vacaciones → devolver día
        if (oldTurn.getName().toLowerCase().contains("vacaciones")) {
            balance.setVacationDays(balance.getVacationDays() + 1);
        }

        // Si el nuevo turno es vacaciones → restar día
        if (newTurn.getName().toLowerCase().contains("vacaciones")) {
            if (balance.getVacationDays() <= 0) {
                throw new RuntimeException("No quedan días de vacaciones disponibles");
            }
            balance.setVacationDays(balance.getVacationDays() - 1);
        }

        userBalanceRepository.save(balance);

        existingAssigned.setUser(user);
        existingAssigned.setTurn(newTurn);
        existingAssigned.setDate(assigned.getDate());

        TurnAssigned savedAssigned = turnAssignedRepository.save(existingAssigned);
        return modelMapper.map(savedAssigned, TurnAssignedOutDto.class);
    }

    // DELETE
    // DELETE - al eliminar, si era vacaciones devolver días
    public void deleteAssigned(Long id) throws TurnsAssignedNotFoundException {
        TurnAssigned assigned = turnAssignedRepository.findById(id)
                .orElseThrow(() -> new TurnsAssignedNotFoundException("Turn assigned not found"));

        // Si el turno era vacaciones → devolver día al balance
        if (assigned.getTurn().getName().toLowerCase().contains("vacaciones")) {
            UserBalance balance = userBalanceRepository.findByUser(assigned.getUser())
                    .orElseThrow(() -> new RuntimeException("Balance not found"));
            balance.setVacationDays(balance.getVacationDays() + 1);
            userBalanceRepository.save(balance);
        }

        turnAssignedRepository.deleteById(id);
    }
}
