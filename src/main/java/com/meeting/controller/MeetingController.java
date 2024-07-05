package com.meeting.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meeting.commen.annotation.RoleCheck;
import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.*;
import com.meeting.domain.pojos.MeetingParticipants;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;

import com.meeting.domain.pojos.Users;
import com.meeting.domain.vos.MeetingDetailsVo;
import com.meeting.domain.vos.MeetingsVo;
import com.meeting.expection.CommonException;
import com.meeting.expection.UnauthorizedException;
import com.meeting.service.MeetingParticipantsService;

import com.meeting.service.MeetingsService;
import com.meeting.service.RoomsService;
import com.meeting.service.UsersService;
import com.meeting.utils.CacheClient;
import com.meeting.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
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
    @Resource
    private UsersService usersService;
    @Resource
    private MeetingParticipantsService meetingParticipantsService;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @ApiOperation("在指定时间段内查询可用的会议室")
    @PostMapping("/available")
    public Result getAvailableRooms(@RequestBody MeetingsTimeDTO meetingsTimeDTO) {
        return Result.succ(roomsService.getAvailableRooms(meetingsTimeDTO.getStartTime(), meetingsTimeDTO.getEndTime()));
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
        meetings.setUserId(UserContext.getUser());
        boolean result = meetingsService.save(meetings);
        // 把会议的参会人员暂存到redis中
        List<Long> usersIds = meetingsRequest.getUsersIds();
        String HuiYiHumanKey = MEETING_USERS_KEY + meetings.getId();
        cacheClient.set(HuiYiHumanKey, usersIds, 1L, TimeUnit.DAYS);
        return Result.succ(200, "预定成功", meetings);
    }

    @ApiOperation("会议取消")
    @PostMapping("/cancel")
    public Result cancelMeeting(@RequestBody CancelMeetingDto cancelMeetingDto) {
        Meetings meetings = meetingsService.getById(cancelMeetingDto.getMeetingId());
        if (meetings == null) {
            return Result.fail("会议不存在");
        }
        if (meetings.getStatus() == 3) {
            return Result.fail("会议已经开始，不能取消");
        }
        if (meetings.getStatus() == 4) {
            return Result.fail("会议已经结束，不能取消");
        }
        if (meetings.getStatus() == 5) {
            return Result.fail("会议已经取消");
        }
        if (meetings.getStatus() == 1) {
            return Result.fail("会议已经被拒绝");
        }
        if (meetings.getStatus() == 0) {
            meetings.setStatus(5);
            meetingsService.updateById(meetings);
            return Result.succ(200, "取消成功", meetings);
        }
        meetings.setStatus(5);
        meetingsService.updateById(meetings);
        //删除redis中的roomId对应的时间数据
        String key = "meeting:book:" + cancelMeetingDto.getRoomId();
        redisTemplate.opsForZSet().remove(key, cancelMeetingDto.getStartTime(), cancelMeetingDto.getEndTime());
        return Result.succ(200, "取消成功", meetings);
    }

    @ApiOperation("管理员会议分页查询")
    @RoleCheck(requiredRole = 1)
    @PostMapping("/search")
    public Result listMeetingByPage(@RequestBody MeetingQueryRequest meetingQueryRequest,
                                    HttpServletRequest request) {
        long current = meetingQueryRequest.getCurrent();
        long size = meetingQueryRequest.getPageSize();
        Page<Meetings> meetingsPage = new Page<>(current, size);

        QueryWrapper<Meetings> queryWrapper = meetingsService.getQueryWrapper(meetingQueryRequest);
        Page<Meetings> pageResult = meetingsService.page(meetingsPage, queryWrapper);

        List<Meetings> meetingsList = pageResult.getRecords();
        List<MeetingsVo> meetingsVos = new ArrayList<>();

        for (Meetings meetings : meetingsList) {
            MeetingsVo meetingsVo = new MeetingsVo();
            Integer roomId = meetings.getRoomId(); // 获取会议室ID
            String roomName = roomsService.getById(roomId).getName(); // 根据会议室ID获取会议室名称
            BeanUtils.copyProperties(meetings, meetingsVo);
            String userName = usersService.getById(meetings.getUserId()).getName(); // 根据用户ID获取用户名称
            meetingsVo.setUserName(userName); // 设置用户名称
            meetingsVo.setRoomName(roomName); // 设置会议室名称9
            meetingsVos.add(meetingsVo);
        }

        Page<MeetingsVo> meetingsVoPage = new Page<>(current, size, pageResult.getTotal());
        meetingsVoPage.setRecords(meetingsVos);

        return Result.succ(200, "查询成功", meetingsVoPage);
    }

    @ApiOperation("查看会议的详细信息")
    @GetMapping("/search/{id}")
    public Result MeetingsDetails(@PathVariable Integer id) {
        return Result.succ(200, "查询成功", meetingsService.searchMeetingDetails(id));
    }

    @ApiOperation("会议审批")
    @PostMapping("/confirm")
    public Result confirmMeeting(@RequestBody MeetingConfirmDto meetingConfirmDto) {
        return meetingsService.confirmMeeting(meetingConfirmDto);
    }
    @ApiOperation("查看个人所有会议")
    @GetMapping("/list")
    public Result listMeetingByUser(@RequestParam Integer status) {
        return Result.succ(200, "查询成功", meetingsService.listMeetingByUser(status));
    }


}