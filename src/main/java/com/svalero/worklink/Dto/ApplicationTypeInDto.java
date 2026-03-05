package com.svalero.worklink.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTypeInDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String description;
    private Boolean affectsBalance;
}
