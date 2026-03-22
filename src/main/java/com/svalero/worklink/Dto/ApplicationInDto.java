package com.svalero.worklink.Dto;

import com.svalero.worklink.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInDto {

    private ApplicationStatus status;
    private Long applicationTypeId;
    private Long userId;
    @NotBlank(message = "El comentario es obligatorio")
    private String comments;
    private Long resolverId;
    private String resolverComments;


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
