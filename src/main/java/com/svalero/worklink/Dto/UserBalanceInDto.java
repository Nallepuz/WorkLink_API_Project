package com.svalero.worklink.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBalanceInDto {

    @NotNull
    private Long userId;
    @NotNull
    private Integer year;
    private Integer vacationDays;
    private Integer excessDays;
    private Integer unpaidDays;
    private Float hoursBalance;
}
