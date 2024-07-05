package com.meeting.domain.vos;

import com.meeting.domain.pojos.Users;
import lombok.Data;

@Data
public class UserInfoVo {

    private String name;

    /**
     *
     */
    private Integer gender;

    /**
     *
     */
    private String telephone;

    /**
     *
     */
    private String email;

    /**
     *
     */

    private String department;
}
