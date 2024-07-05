package com.meeting.domain.dto.meetings;

import lombok.Data;

import java.util.Date;
@Data
public class MeetingsTimeDTO {

    private Date startTime;

    /**
     *
     */
    private Date endTime;
}
