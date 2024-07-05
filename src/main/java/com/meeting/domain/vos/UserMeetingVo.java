package com.meeting.domain.vos;

import lombok.Data;

import java.util.Date;

@Data
public class UserMeetingVo {
    private Integer id;
    private String name;
    private Integer participantCount;
    private Integer roomId;
    private String roomName;
    private Date startTime;
    private Date endTime;
    private String description;
    private Integer status;
}
