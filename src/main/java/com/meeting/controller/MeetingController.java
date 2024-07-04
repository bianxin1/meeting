package com.meeting.controller;

import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingConfirmDto;
import com.meeting.domain.dto.meetings.MeetingsRequest;
import com.meeting.domain.dto.meetings.MeetingsTimeDTO;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.service.MeetingsService;
import com.meeting.service.RoomsService;
import com.meeting.utils.CacheClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.meeting.commen.constants.RedisKey.MEETING_BOOK_KEY;
import static com.meeting.commen.constants.RedisKey.MEETING_USERS_KEY;
@Api(tags = "会议管理")
@RestController
@Slf4j
@RequestMapping("/meeting")
public class MeetingController {
    @Resource
    private MeetingsService meetingsService;
    @Resource
    private RoomsService roomsService;
    @Resource
    private CacheClient cacheClient;

    @ApiOperation("在指定时间段内查询可用的会议室")
    @PostMapping("/available")
    public List<Rooms> getAvailableRooms(@RequestBody MeetingsTimeDTO meetingsTimeDTO) {
        return roomsService.getAvailableRooms(meetingsTimeDTO.getStart_time(), meetingsTimeDTO.getEnd_time());
    }

    @ApiOperation("会议预订")
    @PostMapping("/book")
    public Result bookMeeting(@RequestBody MeetingsRequest meetingsRequest) {
        if (meetingsRequest == null) {
            return Result.fail("请求错误");
        }
        String key = MEETING_BOOK_KEY + meetingsRequest.getRoomId();
        Date startTime = meetingsRequest.getStartTime();
        Date endTime = meetingsRequest.getEndTime();
        boolean a = meetingsService.checkMeetingTime(meetingsRequest.getRoomId(), startTime, endTime);
        if (!a) {
            return Result.fail("会议室时间冲突，请重新选择");
        }
        Meetings meetings = new Meetings();
        BeanUtils.copyProperties(meetingsRequest, meetings);
        meetings.setStatus(0);
        boolean result = meetingsService.save(meetings);
        // 把会议的参会人员暂存到redis中
        List<Long> usersIds = meetingsRequest.getUsersIds();
        String HuiYiHumanKey = MEETING_USERS_KEY + meetings.getId();
        cacheClient.set(HuiYiHumanKey, usersIds, 1L, TimeUnit.DAYS);
        return Result.succ(200, "预定成功", meetings);
    }

    @ApiOperation("会议审批")
    @PostMapping("/confirm")
    public Result confirmMeeting(@RequestBody MeetingConfirmDto meetingConfirmDto) {
        return meetingsService.confirmMeeting(meetingConfirmDto);
    }
}
