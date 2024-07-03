package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Rooms;
import com.meeting.mapper.RoomsMapper;
import com.meeting.service.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void initializeRoomStatus(List<Rooms> rooms) {
        for (Rooms room : rooms) {
            String key = "room:" + room.getId();
            // 初始化每个会议室的状态为未占用
            redisTemplate.opsForHash().put(key, "status", "0");
        }
    }
    public List<Rooms> getAvailableRooms(Date startTime, Date endTime) {
        List<Rooms> availableRooms = new ArrayList<>();
        String start = String.valueOf(startTime.getTime());
        String end = String.valueOf(endTime.getTime());
        String field = start + "-" + end;

        // 假设你有一个方法来获取所有会议室信息
        List<Rooms> allRooms =this.list();

        for (Rooms room : allRooms) {
            String key = "room:" + room.getId();
            String status = (String) redisTemplate.opsForHash().get(key, field);

            if (status == null || status.equals("1")) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }
}




