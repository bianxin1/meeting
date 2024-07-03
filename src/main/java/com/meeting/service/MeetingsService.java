package com.meeting.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingConfirmDto;
import com.meeting.domain.pojos.Meetings;

/**
* @author shanmingxi
* @description 针对表【meetings】的数据库操作Service
* @createDate 2024-07-01 16:57:41
*/
public interface MeetingsService extends IService<Meetings> {
    public void bookRoom(Meetings meeting);
    public void releaseRoom(Meetings meeting);

    /**
     * 确认会议
     * @param meetingConfirmDto
     * @return
     */
    Result confirmMeeting(MeetingConfirmDto meetingConfirmDto);
}
