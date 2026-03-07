package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInDto {

    private Long applicationTypeId;
    private Long userId;
    private Long resolverId;
    private String comments;

    // DÍAS
    private LocalDate startDate;
    private LocalDate endDate;

    // HORAS
    private Float hoursRequested;
    private LocalDate date;
    private LocalTime fromTime;
    private LocalTime toTime;

    // CAMBIO TURNO
    private Long affectedUserId;
    private Long turnGiveId;
    private Long turnReceiveId;
}
