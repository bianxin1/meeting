package com.meeting.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.commen.result.CommonConstant;
import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingConfirmDto;
import com.meeting.domain.dto.meetings.MeetingQueryRequest;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.mapper.MeetingsMapper;
import com.meeting.mapper.RoomsMapper;
import com.meeting.service.MeetingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
* @author shanmingxi
* @description 针对表【meetings】的数据库操作Service实现
* @createDate 2024-07-01 16:57:41
*/
@Service
public class MeetingsServiceImpl extends ServiceImpl<MeetingsMapper, Meetings>
    implements MeetingsService {
    @Autowired
    private RoomsMapper roomMapper;

    @Autowired
    private MeetingsMapper meetingMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 确认会议
     * @param meetingConfirmDto
     * @return
     */
    @Override
    public Result confirmMeeting(MeetingConfirmDto meetingConfirmDto) {
        // 1. 校验会议是否存在
        Meetings meeting = meetingMapper.selectById(meetingConfirmDto.getMeetingId());
        if (meeting == null||meeting.getStatus()!=0) {
            return Result.fail("会议不存在或已审批");
        }
        // 2. 使用redis校验会议时间是否合法
        boolean isAvailable = checkMeetingTime(meetingConfirmDto.getRoomId(),meetingConfirmDto.getStartTime(),meetingConfirmDto.getEndTime());
        if (!isAvailable) {
            return Result.fail("会议室时间冲突");
        }
        // 3. 更新会议状态
        String lockKey = "lock:meeting:book:" + meetingConfirmDto.getRoomId();
        boolean lockAcquired = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "lock", 10, TimeUnit.SECONDS));
        if (!lockAcquired) {
            return Result.fail("无法获取分布式锁，稍后重试");
        }

        try {
            // 再次校验时间，防止在获取锁之前有其他操作修改了数据
            isAvailable = checkMeetingTime(meetingConfirmDto.getRoomId(), meetingConfirmDto.getStartTime(), meetingConfirmDto.getEndTime());
            if (!isAvailable) {
                return Result.fail("会议室时间冲突");
            }

            // 4. 更新会议状态
            meeting.setStatus(1);
            // 只有status为0的会议才能被审批
            UpdateWrapper<Meetings> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", meeting.getId()).eq("status", 0);
            meetingMapper.update(meeting, updateWrapper);
            // 5. 更新redis缓存
            String key = "meeting:book:" + meeting.getRoomId();
            String startTimeString = String.valueOf(meetingConfirmDto.getStartTime().getTime());
            String endTimeString = String.valueOf(meetingConfirmDto.getEndTime().getTime());

            redisTemplate.opsForZSet().add(key, startTimeString, Double.parseDouble(endTimeString));

            return Result.succ("审批成功");
        } finally {
            redisTemplate.delete(lockKey); // 释放锁
        }
    }
    public boolean checkMeetingTime(Integer roomId, Date startTime, Date endTime) {
        String key = "meeting:book"+roomId;
        Set<ZSetOperations.TypedTuple<String>> bookings = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        if (bookings == null) {
            return true;
        }
        long start = startTime.getTime();
        long end = endTime.getTime();
        for (ZSetOperations.TypedTuple<String> booking : bookings) {
            long bookedStart = Long.parseLong(Objects.requireNonNull(booking.getValue()));
            long bookedEnd = Objects.requireNonNull(booking.getScore()).longValue();
            if (start < bookedEnd && end > bookedStart) {
                return false; // 时间段冲突
            }
        }
        return true;
    }

    public QueryWrapper<Meetings> getQueryWrapper(MeetingQueryRequest meetingQueryRequest) {

//        private Integer id;
//        private String name;
//        private Integer participantCount;
//        private Integer roomId;
//        private Date startTime;
//        private Date endTime;
//        private String description;
//        private Integer status;
        Integer id = meetingQueryRequest.getId();
        String name = meetingQueryRequest.getName();
        Integer roomId = meetingQueryRequest.getRoomId();
        Integer participantCount = meetingQueryRequest.getParticipantCount();
        Date startTime=meetingQueryRequest.getStartTime();
        Date endTime=meetingQueryRequest.getEndTime();
        Integer status=meetingQueryRequest.getStatus();

        QueryWrapper<Meetings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(roomId!=null, "roomId", roomId);
        queryWrapper.eq(participantCount!=null, "participantCount", participantCount);
        queryWrapper.eq(startTime!=null, "startTime", startTime);
        queryWrapper.eq(startTime!=null, "startTime", startTime);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderByAsc("id");
        return queryWrapper;
    }

}




