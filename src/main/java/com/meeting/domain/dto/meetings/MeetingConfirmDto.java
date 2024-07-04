package com.meeting.domain.dto.meetings;

import lombok.Data;

import java.util.Date;

@Data
public class MeetingConfirmDto {
    private Integer meetingId;
    private Integer roomId;
    private Date startTime;
    private Date endTime;
    private Integer confirm;
}
