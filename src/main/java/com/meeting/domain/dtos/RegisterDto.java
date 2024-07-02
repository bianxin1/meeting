package com.meeting.domain.dtos;

import lombok.Data;

@Data
public class RegisterDto {
    String name;
    String password;
    String email;
    String code;
}
