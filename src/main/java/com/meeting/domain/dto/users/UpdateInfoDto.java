package com.meeting.domain.dto.users;

import lombok.Data;

@Data
public class UpdateInfoDto {
    private String telephone;
    private int gender;
    private int departmentId;
}
