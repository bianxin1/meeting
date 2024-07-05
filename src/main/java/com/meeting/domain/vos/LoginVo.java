package com.meeting.domain.vos;

import lombok.Data;

@Data
public class LoginVo {
    String token;
    String role;
    String account;
    Long id;
}
