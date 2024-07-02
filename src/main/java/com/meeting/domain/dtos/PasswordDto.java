package com.meeting.domain.dtos;

import lombok.Data;

@Data
public class PasswordDto {
    private String oldPassword;
    private String newPassword;
}
