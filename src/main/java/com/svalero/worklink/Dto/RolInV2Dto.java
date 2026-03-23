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
public class RolInV2Dto {

    @NotBlank(message = "EL nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(min = 15, message = "La descripcion debe tener al menos 15 caracteres")
    private String description;

    private float accessLevel;

    @NotNull(message = "El campo active es obligatorio")
    private Boolean active;
}
