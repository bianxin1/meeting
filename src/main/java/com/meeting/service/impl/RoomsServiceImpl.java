package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.commen.result.Result;
import com.meeting.domain.pojos.Rooms;
import com.meeting.mapper.RoomsMapper;
import com.meeting.service.MeetingsService;
import com.meeting.service.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author shanmingxi
* @description 针对表【rooms】的数据库操作Service实现
* @createDate 2024-07-01 17:56:16
*/
@Service
public class RoomsServiceImpl extends ServiceImpl<RoomsMapper, Rooms>
    implements RoomsService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private MeetingsService meetingsService;

    public List<Rooms> getAvailableRooms(Date startTime, Date endTime) {
        Set<Integer> unavailableRooms = new HashSet<>();
        List<Rooms> allRooms =this.list();
        // 格式化时间
        long start = startTime.getTime();
        long end = endTime.getTime();


        // 检查每个会议室的有序集合
        for (Rooms room : allRooms) {
            String key = "meeting:book"+room.getId();
            Set<ZSetOperations.TypedTuple<String>> bookings = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
            if (bookings == null) {
                break;
            }
            for (ZSetOperations.TypedTuple<String> booking : bookings) {
                long bookedStart = Long.parseLong(Objects.requireNonNull(booking.getValue()));
                long bookedEnd = Objects.requireNonNull(booking.getScore()).longValue();
                if (start < bookedEnd && end > bookedStart) {
                   unavailableRooms.add(room.getId());
                }
            }
        }

        // 返回可用的会议室
        return allRooms.stream()
                .filter(room -> !unavailableRooms.contains(room.getId()))
                .collect(Collectors.toList());
    }
}




