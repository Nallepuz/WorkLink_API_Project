package com.svalero.worklink.service;

import com.svalero.worklink.Dto.TurnInDto;
import com.svalero.worklink.Dto.TurnOutDto;
import com.svalero.worklink.exception.RolNotFoundException;
import com.svalero.worklink.exception.TurnsNotFoundException;
import com.svalero.worklink.model.Turns;
import com.svalero.worklink.repository.TurnRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnService {

    @Autowired
    private TurnRepository turnRepository;
    @Autowired
    private ModelMapper modelMapper;

    // GET
    public List<TurnOutDto> findAll(String name, String colorHex, Boolean nights) throws TurnsNotFoundException{
        List<Turns> turns = turnRepository.findAll();

        if (name != null) {
            turns = turns.stream()
                    .filter(turn -> turn.getName().equals(name))
                    .toList();
        }
        if (colorHex != null) {
            turns = turns.stream()
                    .filter(turn -> turn.getColorHex().equals(colorHex))
                    .toList();
        }
        if (nights != null) {
            turns = turns.stream()
                    .filter(turn -> turn.getNights().equals(nights))
                    .toList();
        }

        return modelMapper.map(turns, new TypeToken<List<TurnOutDto>>() {}.getType());
    }

    public TurnOutDto findById(Long id) throws TurnsNotFoundException {
        Turns turn = turnRepository.findById(id)
                .orElseThrow(() -> new TurnsNotFoundException("Turn not found"));

        return modelMapper.map(turn, TurnOutDto.class);
    }

    // POST
    public TurnOutDto addTurn(TurnInDto turn) {
        Turns newTurn = modelMapper.map(turn, Turns.class);
        Turns savedTurn = turnRepository.save(newTurn);
        return modelMapper.map(savedTurn, TurnOutDto.class);
    }

    // PUT
    public TurnOutDto modify(Long id, TurnInDto turn) throws TurnsNotFoundException {
        Turns existingTurn = turnRepository.findById(id)
                .orElseThrow(() -> new TurnsNotFoundException("Turn not found"));

        modelMapper.map(turn, existingTurn);
        Turns savedTurn = turnRepository.save(existingTurn);

        return modelMapper.map(savedTurn, TurnOutDto.class);
    }

    // DELETE
    public void delteTurn(Long id) throws RolNotFoundException {
        Turns turn = turnRepository.findById(id)
                .orElseThrow(() -> new TurnsNotFoundException("Turn not found"));
        turnRepository.deleteById(id);
    }
}
