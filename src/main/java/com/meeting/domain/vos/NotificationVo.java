package com.meeting.domain.vos;

import com.meeting.domain.pojos.Notifications;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationVo {
    private Long id;
    private Integer meetingId;
    private String message;
    private Date timestamp;
    private Integer isRead;
    public NotificationVo(Notifications notification) {
        this.id = notification.getId();
        this.meetingId = notification.getMeetingId();
        this.message = notification.getMessage();
        this.timestamp = notification.getTimestamp();
        this.isRead = notification.getIsRead();
    }
}
