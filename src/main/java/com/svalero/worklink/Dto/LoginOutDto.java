package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginOutDto {
    private Long id;
    private String name;
    private String email;
    private Long roleId;
    private Boolean active;
    private String token;
}
