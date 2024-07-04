package com.meeting.listener;

import cn.hutool.json.JSONUtil;
import com.meeting.domain.dto.meetings.UserMeetingMessage;
import com.meeting.service.MeetingParticipantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MeetingParticipantsListener {
    public final MeetingParticipantsService meetingParticipantsService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "meeting.participants.queue", durable = "true"),
            exchange = @Exchange(name = "meeting.direct"),
            key = "meeting.participants"
    ))
    public void listenParticipants(String messageJson) {
        UserMeetingMessage message = JSONUtil.toBean(messageJson, UserMeetingMessage.class);
        Integer meetingId = message.getMeetingId();
        List<Long> userIds = message.getUserIds();
        meetingParticipantsService.saveParticipants(meetingId,userIds);
    }
}
