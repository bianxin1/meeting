package com.meeting.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingConfirmDto;
import com.meeting.domain.dto.meetings.MeetingQueryRequest;
import com.meeting.domain.dto.meetings.MeetingsRequest;
import com.meeting.domain.dto.meetings.MeetingsTimeDTO;
import com.meeting.domain.dto.rooms.RoomsAddRequest;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.expection.CommonException;
import com.meeting.expection.UnauthorizedException;
import com.meeting.service.MeetingParticipantsService;
import com.meeting.service.MeetingsService;
import com.meeting.service.RoomsService;
import com.meeting.utils.CacheClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(tags = "会议管理")
@RestController
@Slf4j
@RequestMapping("/meeting")
public class MeetingController {
    @Resource
    private MeetingsService meetingsService;
    @Resource
    private RoomsService roomsService;
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private MeetingParticipantsService meetingParticipantsService;

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
        String key = "meeting:book:" + meetingsRequest.getRoomId();
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
        String HuiYiHumanKey = "meeting:users:" + meetingsRequest.getId();
        cacheClient.set(HuiYiHumanKey, usersIds, 1L, TimeUnit.DAYS);
        return Result.succ(200, "预定成功", meetings);
    }

    @ApiOperation("会议取消")
    @PostMapping("/cancel")
    public Result cancelMeeting(@RequestBody MeetingsRequest meetingsRequest) {
        Meetings meetings = new Meetings();
        BeanUtils.copyProperties(meetingsRequest, meetings);
        meetings.setStatus(5);
        boolean result = meetingsService.save(meetings);
        //删除redis中的roomId对应的时间数据
        String key = "meeting:book:" + meetingsRequest.getRoomId();
        redisTemplate.opsForZSet().remove(key, meetingsRequest.getStartTime(), meetingsRequest.getEndTime());
        return Result.succ(200, "取消成功", meetings);
    }

    @ApiOperation("管理员会议分页查询")
    @PostMapping("/search")
    public Result listMeetingByPage(@RequestBody MeetingQueryRequest meetingQueryRequest,
                                 HttpServletRequest request) {
        long current = meetingQueryRequest.getCurrent();
        long size = meetingQueryRequest.getPageSize();
        Page<Meetings> meetingsPage = meetingsService.page(new Page<>(current, size),
                meetingsService.getQueryWrapper(meetingQueryRequest));
        return Result.succ(meetingsPage);
    }

    @ApiOperation("查看会议的详细信息")
    @GetMapping("/search/{id}")
    public Result MeetingsDetails(@PathVariable Integer id) {
        Meetings m = meetingsService.getById(id);

        return Result.succ(meetingsPage);
    }
///**
//    @ApiOperation("会议审批")
//    @PostMapping("/confirm")
//    public Result confirmMeeting(@RequestBody MeetingConfirmDto meetingConfirmDto) {
//        return meetingsService.confirmMeeting(meetingConfirmDto);
//    }


}