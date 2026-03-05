package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOutDto {
    private Long id;
    private String name;
    private String email;
    private String phone;

    private LocalDateTime created;
    private LocalDateTime updated;

    private boolean active;
    private Long rolId;
}
