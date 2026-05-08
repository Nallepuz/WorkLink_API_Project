package com.svalero.worklink.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationOutDto {

    private Long id;
    private String status;
    private Long applicationTypeId;
    private Long userId;
    private String userName;
    private Long affectedUserId;
    private String affectedUserName;
    private LocalDateTime created;
    private LocalDateTime resolved;
    private String comments;
    private String resolverComments;
}