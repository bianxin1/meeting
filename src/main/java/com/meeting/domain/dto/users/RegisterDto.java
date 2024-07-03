package com.meeting.domain.dto.users;

import lombok.Data;

@Data
public class RegisterDto {
    String name;
    String password;
    String email;
    String code;
}
