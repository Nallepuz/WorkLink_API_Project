package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceOutDto {

    private Long id;

    private Long userId;
    private String userName;

    private int year;
    private int vacationDays;
    private int excessDays;
    private int unpaidDays;
    private float hoursBalance;

    private LocalDateTime created;
    private LocalDateTime updated;
}
