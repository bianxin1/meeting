package com.meeting.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.mapper.MeetingsMapper;
import com.meeting.mapper.RoomsMapper;
import com.meeting.service.MeetingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public void bookRoom(Meetings meeting) {
        String key = "room:" + meeting.getRoom_id();
        String field = meeting.getStart_time().getTime() + "-" + meeting.getEnd_time().getTime();
        // 更新会议室状态为已占用
        redisTemplate.opsForHash().put(key, field, "2");

    }

    public void releaseRoom(Meetings meeting) {
        String key = "room:" + meeting.getRoom_id();
        String field = meeting.getStart_time().getTime() + "-" + meeting.getEnd_time().getTime();
        // 更新会议室状态为未占用
        redisTemplate.opsForHash().put(key, field, "0");
    }


}




