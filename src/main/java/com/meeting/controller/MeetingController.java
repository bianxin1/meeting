package com.meeting.controller;

import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingConfirmDto;
import com.meeting.domain.dto.meetings.MeetingsRequest;
import com.meeting.domain.dto.rooms.RoomsAddRequest;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.expection.CommonException;
import com.meeting.expection.UnauthorizedException;
import com.meeting.service.MeetingsService;
import com.meeting.service.RoomsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
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
    private StringRedisTemplate redisTemplate;

    @GetMapping("/available")
    public List<Rooms> getAvailableRooms(@RequestParam("start") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime,
                                         @RequestParam("end") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime) {
        return roomsService.getAvailableRooms(startTime, endTime);
    }
    @ApiOperation("会议预订")
    @PostMapping("/book")
    public Result bookMeeting(@RequestBody MeetingsRequest meetingsRequest) {
        if (meetingsRequest == null) {
            return Result.fail("请求错误");
        }
        String key = "meeting:book:" + meetingsRequest.getRoom_id();
        Date startTime = meetingsRequest.getStart_time();
        Date endTime   = meetingsRequest.getEnd_time();
         boolean a = meetingsService.checkMeetingTime(meetingsRequest.getRoom_id(),startTime,endTime);
         if (!a) {
             return Result.fail("会议室时间冲突，请重新选择");
         }
        Meetings meetings = new Meetings();
        BeanUtils.copyProperties(meetingsRequest, meetings);
        meetings.setStatus(0);
        boolean result = meetingsService.save(meetings);
        return Result.succ(200,"预定成功",meetings);
    }

    @ApiOperation("会议审批")
    @PostMapping("/confirm")
    public Result confirmMeeting(@RequestBody MeetingConfirmDto meetingConfirmDto) {
        return meetingsService.confirmMeeting(meetingConfirmDto);
    }
}
