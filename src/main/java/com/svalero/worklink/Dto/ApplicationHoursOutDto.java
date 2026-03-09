package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationHoursOutDto {

    private Long id;
    private String status;

    // TYPE
    private Long applicationTypeId;

    // USUARIOS
    private Long userId;
    private LocalDateTime created;
    private String comments;

    // HORAS
    private Float hoursRequested;
    private LocalDate date;
    private LocalTime fromTime;
    private LocalTime toTime;

    // USUARIO QUE RESUELVE
    private Long resolverId;
    private LocalDateTime resolved;
    private String resolverComments;
}
