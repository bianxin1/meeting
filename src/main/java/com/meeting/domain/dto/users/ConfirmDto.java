package com.meeting.domain.dto.users;

import lombok.Data;

@Data
public class ConfirmDto {

        private Long userId;

        /**
        * 0:未通过，1：已通过
        */
        private Integer status;

        /**
        *  审核原因
        */
        private String reason;
}
