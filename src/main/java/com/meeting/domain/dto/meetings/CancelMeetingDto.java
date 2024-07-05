package com.meeting.domain.dto.meetings;

import lombok.Data;

import java.util.Date;

@Data
public class CancelMeetingDto {
    private Integer meetingId;
    private Integer roomId;
    private Date startTime;
    private Date endTime;
    private Integer status;
}
