package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTypeOutDto {

    private Long id;
    private String name;
    private String description;
    private boolean affectsBalance;
    private boolean active;

    private LocalDateTime created;
    private LocalDateTime updated;
}

