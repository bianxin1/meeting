package com.meeting.domain.dto.users;

import lombok.Data;

@Data
public class LoginDto {
    String email;
    String account;
    String password;
}
