package com.meeting.domain.dto.meetings;

import lombok.Data;

import java.util.Date;
@Data
public class MeetingsTimeDTO {

    private Date start_time;

    /**
     *
     */
    private Date end_time;
}
