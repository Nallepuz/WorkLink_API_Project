package com.svalero.worklink.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnAssignedInDto {

    @NotNull(message = "El día es obligatorio")
    private LocalDate date;
    @NotNull(message = "El user es obligatorio")
    private Long userId;
    @NotNull(message = "El turno es obligatorio")
    private Long turnId;
}
