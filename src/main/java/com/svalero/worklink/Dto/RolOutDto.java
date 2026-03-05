package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolOutDto {
    private Long id;
    private String name;
    private String description;
    private float accessLevel;
    private boolean active;

    private LocalDateTime created;
    private LocalDateTime updated;
}
