package com.svalero.worklink.config;

import com.svalero.worklink.Dto.ApplicationOutDto;
import com.svalero.worklink.Dto.TurnAssignedOutDto;
import com.svalero.worklink.Dto.UserBalanceOutDto;
import com.svalero.worklink.Dto.UserOutDto;
import com.svalero.worklink.model.Application;
import com.svalero.worklink.model.TurnAssigned;
import com.svalero.worklink.model.User;
import com.svalero.worklink.model.UserBalance;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // User -> UserDto
        modelMapper.typeMap(User.class, UserOutDto.class)
                .addMappings(m -> m.map(
                        src -> src.getRol().getId(), UserOutDto::setRolId
                ));

        // UserBalance -> UserBalanceDto
        modelMapper.typeMap(UserBalance.class, UserBalanceOutDto.class)
                .addMappings(m -> m.map(
                        src -> src.getUser().getId(),
                        UserBalanceOutDto::setUserId
                ));

        // Assigned -> AssignedDto
        modelMapper.typeMap(TurnAssigned.class, TurnAssignedOutDto.class)
                .addMappings(m -> {
                    m.map(src -> src.getUser().getId(), TurnAssignedOutDto::setUserId);
                    m.map(src -> src.getTurn().getId(), TurnAssignedOutDto::setTurnId);
                });

        // Application -> ApplicationDto
        modelMapper.typeMap(Application.class, ApplicationOutDto.class)
                .addMappings(m -> {
                    m.map(src -> src.getUser().getId(), ApplicationOutDto::setUserId);
                    m.map(src -> src.getUser().getEmail(), ApplicationOutDto::setUserEmail);

                    m.map(src -> src.getApplicationType().getId(), ApplicationOutDto::setApplicationTypeId);
                    m.map(src -> src.getApplicationType().getName(), ApplicationOutDto::setApplicationTypeName);
                });

        return modelMapper;
    }
}
