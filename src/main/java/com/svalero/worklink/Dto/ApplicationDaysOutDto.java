package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDaysOutDto {

    private Long id;
    private String status;

    // TYPE
    private Long applicationTypeId;

    // USUARIOS
    private Long userId;
    private LocalDateTime created;
    private String comments;

    // DÍAS
    private LocalDate startDate;
    private LocalDate endDate;

    // USUARIO QUE RESUELVE
    private Long resolverId;
    private LocalDateTime resolved;
    private String resolverComments;
}
