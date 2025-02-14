package com.meeting.service.impl;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.commen.result.CommonConstant;
import com.meeting.commen.result.Result;
import com.meeting.domain.dto.meetings.MeetingConfirmDto;
import com.meeting.domain.dto.meetings.MeetingQueryRequest;
import com.meeting.domain.dto.meetings.UserMeetingMessage;
import com.meeting.domain.pojos.MeetingParticipants;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Users;
import com.meeting.domain.vos.MeetingDetailsVo;
import com.meeting.domain.vos.UserInfoVo;
import com.meeting.domain.vos.UserMeetingVo;
import com.meeting.mapper.MeetingsMapper;
import com.meeting.mapper.RoomsMapper;
import com.meeting.mapper.UsersMapper;
import com.meeting.service.MeetingParticipantsService;
import com.meeting.service.MeetingsService;
import com.meeting.service.UsersService;
import com.meeting.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.meeting.commen.constants.RedisKey.*;

/**
* @author shanmingxi
* @description 针对表【meetings】的数据库操作Service实现
* @createDate 2024-07-01 16:57:41
*/
@Slf4j
@Service
public class MeetingsServiceImpl extends ServiceImpl<MeetingsMapper, Meetings>
    implements MeetingsService {
    @Autowired
    private MeetingsMapper meetingMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RoomsMapper roomMapper;
    @Resource
    private UsersService usersService;
    @Resource
    private MeetingParticipantsService meetingParticipantsService;
    @Resource
    private MeetingsMapper meetingsMapper;
    /**
     * 确认会议
     * @param meetingConfirmDto
     * @return
     */
    @Override
    public Result confirmMeeting(MeetingConfirmDto meetingConfirmDto) {
        if (usersMapper.selectById(UserContext.getUser()).getRole()!=1) {
            return Result.fail("无权限");
        }
        // 1. 校验会议是否存在
        Meetings meeting = meetingMapper.selectById(meetingConfirmDto.getMeetingId());
        if (meeting == null||meeting.getStatus()!=0) {
            return Result.fail("会议不存在或已审批");
        }
        if (meetingConfirmDto.getConfirm()!=1){
            meeting.setStatus(1);
            meetingMapper.updateById(meeting);
            return Result.succ("审批成功");
        }
        // 2. 使用redis校验会议时间是否合法
        boolean isAvailable = checkMeetingTime(meetingConfirmDto.getRoomId(),meetingConfirmDto.getStartTime(),meetingConfirmDto.getEndTime());
        if (!isAvailable) {
            return Result.fail("会议室时间冲突");
        }
        // 3. 更新会议状态
        String lockKey = MEETING_BOOK_LOCK_KEY + meetingConfirmDto.getRoomId();
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
            meeting.setStatus(2);
            // 只有status为0的会议才能被审批
            UpdateWrapper<Meetings> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", meeting.getId()).eq("status", 0);
            meetingMapper.update(meeting, updateWrapper);
            // 5. 更新redis缓存
            String key = MEETING_BOOK_KEY + meeting.getRoomId();
            String startTimeString = String.valueOf(meetingConfirmDto.getStartTime().getTime());
            String endTimeString = String.valueOf(meetingConfirmDto.getEndTime().getTime());
            redisTemplate.opsForZSet().add(key, startTimeString, Double.parseDouble(endTimeString));
            // 6. 持久化参会人员信息到数据库,发送人员列表到消息队列
            String key2 = MEETING_USERS_KEY + meeting.getId();
            String userIdsJson = redisTemplate.opsForValue().get(key2);
            List<Long> userIds = JSONUtil.toList(userIdsJson, Long.class);
            UserMeetingMessage message = new UserMeetingMessage();
            message.setMeetingId(meeting.getId());
            message.setUserIds(userIds);
            rabbitTemplate.convertAndSend("meeting.direct", "meeting.participants", JSONUtil.toJsonStr(message));
            // 7. 发送通知消息并存储到数据库
            message.setMeetingName(meeting.getName());
            rabbitTemplate.convertAndSend("meeting.direct", "meeting.confirm", JSONUtil.toJsonStr(message));
            return Result.succ("审批成功");
        } finally {
            redisTemplate.delete(lockKey); // 释放锁
        }
    }
    public boolean checkMeetingTime(Integer roomId, Date startTime, Date endTime) {
        String key = MEETING_BOOK_KEY+roomId;
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
        queryWrapper.eq(roomId!=null, "room_id", roomId);
        queryWrapper.eq(participantCount!=null, "participant_count", participantCount);
        queryWrapper.eq(status!=null, "status", status);
        queryWrapper.ge(startTime!=null, "start_time", startTime);
        queryWrapper.le(endTime!=null, "end_time", endTime);
        queryWrapper.like(StringUtils.isNotBlank(name)&& !name.isEmpty(), "name", name);
        queryWrapper.orderByAsc("id");
        return queryWrapper;
    }

    @Override
    public MeetingDetailsVo searchMeetingDetails(Integer id) {
        Meetings meetings = getById(id);
        QueryWrapper<MeetingParticipants> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("meeting_id", id);
        List<MeetingParticipants> meetingParticipants = meetingParticipantsService.list(queryWrapper);
        List<Users> usersList = new ArrayList<>();
        for (MeetingParticipants participant : meetingParticipants) {
            Long userId = participant.getUserId();
            Users users = usersService.getById(userId);
            if (users != null) {
                usersList.add(users);
            }
        }
        //将userlist转换为userinfovo
        List<UserInfoVo> userInfoVos = usersList.stream().map(users -> {
            UserInfoVo userInfoVo = new UserInfoVo();
            BeanUtils.copyProperties(users, userInfoVo);
            return userInfoVo;
        }).collect(Collectors.toList());
        MeetingDetailsVo meetingDetailsVo = new MeetingDetailsVo();
        BeanUtils.copyProperties(meetings, meetingDetailsVo);
        String roomName = roomMapper.selectById(meetingDetailsVo.getRoomId()).getName();
        meetingDetailsVo.setRoomName(roomName);;
        meetingDetailsVo.setUsers(userInfoVos);
        return meetingDetailsVo;
    }

    @Override
    public List<UserMeetingVo> listMeetingByUser(Integer status) {
        // 1.获取当前用户
        Long userId = UserContext.getUser();
        // 2.查询当前用户的会议信息
        QueryWrapper<Meetings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq(status != null, "status", status);
        List<Meetings> meetings = list(queryWrapper);
        // 3.创建一个UserMeetingVo列表
        List<UserMeetingVo> userMeetingVos = new ArrayList<>();
        for (Meetings meeting : meetings) {
            UserMeetingVo userMeetingVo = new UserMeetingVo();
            BeanUtils.copyProperties(meeting, userMeetingVo);
            userMeetingVos.add(userMeetingVo);
        }
        // 4.封装roomname
        for (UserMeetingVo userMeetingVo : userMeetingVos) {
            String roomName = roomMapper.selectById(userMeetingVo.getRoomId()).getName();
            userMeetingVo.setRoomName(roomName);
        }
        return userMeetingVos;
    }

    @Scheduled(cron = "0 0 * * * ?") // 每个小时开始的第0分钟执行一次
    public void updateMeetingStatus() {
        Date now = new Date();
        List<Meetings> meetings = this.list(); // 获取所有会议

        for (Meetings meeting : meetings) {
            if (meeting.getStatus() == 0||meeting.getStatus()==1) {
                continue; // 跳过未审批的会议
            }
            if (meeting.getStartTime().before(now) && meeting.getEndTime().after(now)) {
                // 会议正在进行中
                meeting.setStatus(3);
            } else if (meeting.getEndTime().before(now)) {
                // 会议已结束
                meeting.setStatus(4);
            }
            boolean b = this.updateById(meeting);


        }
    }
}




