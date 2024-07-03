package com.meeting.controller;

import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingsRequest;
import com.meeting.domain.dto.rooms.RoomsAddRequest;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.expection.CommonException;
import com.meeting.expection.UnauthorizedException;
import com.meeting.service.MeetingsService;
import com.meeting.service.RoomsService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/meeting")
public class MeetingController {
    @Resource
    private MeetingsService meetingsService;
    @Resource
    private RoomsService roomsService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/available")
    public List<Rooms> getAvailableRooms(@RequestParam("start") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime,
                                         @RequestParam("end") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime) {
        return roomsService.getAvailableRooms(startTime, endTime);
    }

    @PostMapping("/book")
    public Result bookMeeting(@RequestBody MeetingsRequest meetingsRequest) {
        if (meetingsRequest == null) {
            throw new UnauthorizedException("请求错误");
        }
        String key = "room:" + meetingsRequest.getRoom_id();
        String field = meetingsRequest.getStart_time().getTime() + "-" + meetingsRequest.getEnd_time().getTime();
        String status = (String) redisTemplate.opsForHash().get(key, field);
        if (status !="1") {
            throw new UnauthorizedException("会议室不可使用");
        }

        Meetings meetings = new Meetings();
        BeanUtils.copyProperties(meetingsRequest, meetings);
        boolean result = meetingsService.save(meetings);
        meetingsService.bookRoom(meetings);//更新会议室状态为2
        return Result.succ(200,"预定成功",meetings);
    }

}
