package com.meeting.domain.vos;

import lombok.Data;

@Data
public class LoginVo {
    String token;
    Integer id;
    String account;
}
