package com.meeting.domain.dto.meetings;

import lombok.Data;

import java.util.List;

@Data
public class UserMeetingMessage {
    private List<Long> userIds;
    private Integer meetingId;
    private String meetingName;
}
