package com.meeting.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.domain.pojos.MeetingParticipants;

import java.util.List;

/**
* @author shanmingxi
* @description 针对表【meeting_participants】的数据库操作Service
* @createDate 2024-07-01 16:55:09
*/
public interface MeetingParticipantsService extends IService<MeetingParticipants> {
    /**
     * 保存会议参与者
     * @param meetingId
     * @param userIds
     */
    void saveParticipants(Integer meetingId, List<Long> userIds);
}
