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
public class ApplicationPutInDto {
    private ApplicationStatus status;
    private Long resolverId;
    @NotBlank(message = "El comentario es obligatorio")
    private String resolverComments;
}
