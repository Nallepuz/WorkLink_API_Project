package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationOutDto {
    private Long id;
    private Long userId;
    private String message;
    private boolean read;
    private String type;
    private LocalDateTime createdAt;
}