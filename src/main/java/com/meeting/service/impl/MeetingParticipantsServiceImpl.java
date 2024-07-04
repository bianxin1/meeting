package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.MeetingParticipants;
import com.meeting.mapper.MeetingParticipantsMapper;
import com.meeting.service.MeetingParticipantsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author shanmingxi
* @description 针对表【meeting_participants】的数据库操作Service实现
* @createDate 2024-07-01 16:55:09
*/
@Service
public class MeetingParticipantsServiceImpl extends ServiceImpl<MeetingParticipantsMapper, MeetingParticipants>
    implements MeetingParticipantsService {

    /**
     * 保存会议参与者
     * @param meetingId
     * @param userIds
     */
    @Override
    @Transactional
    public void saveParticipants(Integer meetingId, List<Long> userIds) {
        List<MeetingParticipants> meetingParticipants = userIds.stream().map(userId -> {
            MeetingParticipants meetingParticipant = new MeetingParticipants();
            meetingParticipant.setMeetingId(meetingId);
            meetingParticipant.setUserId(userId);
            meetingParticipant.setStatus(0); // 默认状态为未签到
            return meetingParticipant;
        }).collect(Collectors.toList());
        this.saveBatch(meetingParticipants);
    }
}




