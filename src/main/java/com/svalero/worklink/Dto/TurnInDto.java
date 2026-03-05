package com.svalero.worklink.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnInDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @Size(min = 10, message = "El turno debe tener al menos 10 caracteres")
    private String description;

    private LocalTime workStart;
    private LocalTime workEnd;

    @NotBlank(message = "El color es obligatorio")
    private String colorHex;
    private boolean nights;
}
