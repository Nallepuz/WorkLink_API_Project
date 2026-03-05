package com.svalero.worklink.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolInDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @Size(min = 15, message = "La descripcion debe tener al menos 15 caracteres")
    private String description;
    private float accessLevel;
}
