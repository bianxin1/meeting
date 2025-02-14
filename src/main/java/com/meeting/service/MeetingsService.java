package com.meeting.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingConfirmDto;
import com.meeting.domain.dto.meetings.MeetingQueryRequest;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.vos.MeetingDetailsVo;
import com.meeting.domain.vos.UserMeetingVo;

import java.util.Date;
import java.util.List;

/**
* @author shanmingxi
* @description 针对表【meetings】的数据库操作Service
* @createDate 2024-07-01 16:57:41
*/
public interface MeetingsService extends IService<Meetings> {

    /**
     * 确认会议
     * @param meetingConfirmDto
     * @return
     */
    Result confirmMeeting(MeetingConfirmDto meetingConfirmDto);
    boolean checkMeetingTime(Integer roomId, Date startTime, Date endTime);
    public QueryWrapper<Meetings> getQueryWrapper(MeetingQueryRequest meetingQueryRequest);

    MeetingDetailsVo searchMeetingDetails(Integer id);

    /**
     * 查询会议列表
     * @return
     */
    List<UserMeetingVo> listMeetingByUser(Integer status);

}
