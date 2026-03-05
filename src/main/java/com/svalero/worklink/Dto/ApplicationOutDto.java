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
public class ApplicationOutDto {

    private Long id;
    private String status;

    private LocalDateTime created;
    private LocalDateTime resolved;

    // COMENTARIOS
    private String comments;
    private String resolverComments;

    // TYPE (MINI)
    private Long applicationTypeId;
    private String applicationTypeName;

    // USUARIOS (MINI)
    private Long userId;
    private String userEmail;

    private Long resolverId;
    private String resolverEmail;

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
