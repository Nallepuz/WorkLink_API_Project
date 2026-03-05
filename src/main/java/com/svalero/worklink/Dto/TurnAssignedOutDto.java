package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnAssignedOutDto {

    private Long id;
    private LocalDate date;
    private LocalDateTime created;

    private Long userId;
    private String userName;

    private Long turnId;
    private String turnName;
}
