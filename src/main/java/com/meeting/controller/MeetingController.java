package com.meeting.controller;

import com.meeting.commen.result.Result;
import com.meeting.domain.dto.rooms.RoomsAddRequest;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.expection.UnauthorizedException;
import com.meeting.service.DepartmentsService;
import com.meeting.service.MeetingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/meeting")
public class MeetingController {
    @Resource
    private MeetingsService meetingsService;
    @PostMapping("/add")
    public Result addRooms(@RequestBody MeetingsAddRequest MeetingsAddRequest, HttpServletRequest request) {
        if (MeetingsAddRequest == null) {
            throw new UnauthorizedException("请求错误");
        }
        Meetings meetings = new Meetings();
        BeanUtils.copyProperties(MeetingsAddRequest, meetings);
        boolean result = meetingsService.save(meetings);
        return Result.succ(200,"添加成功",meetings);
    }
}
