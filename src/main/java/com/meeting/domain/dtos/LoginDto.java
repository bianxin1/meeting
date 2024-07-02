package com.meeting.domain.dtos;

import lombok.Data;

@Data
public class LoginDto {
    String email;
    String account;
    String password;
}
