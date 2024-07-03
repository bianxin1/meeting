package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Rooms;
import com.meeting.mapper.RoomsMapper;
import com.meeting.service.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

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


    public List<Rooms> getAvailableRooms(Date startTime, Date endTime) {
        Set<Integer> unavailableRooms = new HashSet<>();
        List<Rooms> allRooms =this.list();
        // 格式化时间
        long start = startTime.getTime();
        long end = endTime.getTime();


        // 检查每个会议室的有序集合
        for (Rooms room : allRooms) {
            String key = "room:" + room.getId();
            Set<ZSetOperations.TypedTuple<String>> meetings = redisTemplate.opsForZSet().rangeByScoreWithScores(key, Double.MIN_VALUE, end);

            if (meetings != null && !meetings.isEmpty()) {
                // 检查时间重叠
                for (ZSetOperations.TypedTuple<String> meeting : meetings) {
                    long meetingStart = Long.parseLong(meeting.getValue());
                    double meetingEnd = meeting.getScore();

                    if (meetingEnd > start&&meetingStart<end) {
                        unavailableRooms.add(room.getId());
                        break;
                    }
                }
            }
        }


        // 返回可用的会议室
        return allRooms.stream()
                .filter(room -> !unavailableRooms.contains(room.getId()))
                .collect(Collectors.toList());
    }
}




