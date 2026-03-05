package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnOutDto {
    private Long id;
    private String name;
    private String description;

    private LocalTime workStart;
    private LocalTime workEnd;

    private String colorHex;
    private Boolean nights;
    private Boolean active;
}
