package com.meeting.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Meetings;
import com.meeting.domain.pojos.Rooms;
import com.meeting.mapper.MeetingsMapper;
import com.meeting.mapper.RoomsMapper;
import com.meeting.service.MeetingsService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 查询指定时间段内的会议室状态
     */
    public List<Rooms> getRoomsStatus(Date startTime, Date endTime) {
        // 查询所有会议室
        List<Rooms> rooms = roomMapper.selectList(null);

        for (Rooms room : rooms) {
            // 检查会议室在指定时间段内是否有会议
            QueryWrapper<Meetings> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_id", room.getId())
                    .and(wrapper -> wrapper.between("start_time", startTime, endTime)
                            .or()
                            .between("end_time", startTime, endTime)
                            .or()
                            .and(innerWrapper -> innerWrapper.le("start_time", startTime)
                                    .ge("end_time", endTime)));
            List<Meetings> meetings = meetingMapper.selectList(queryWrapper);
            if (meetings.isEmpty()) {
                room.setStatus(1); // 可用
            } else {
                room.setStatus(2); // 已占用
            }
        }

        return rooms;
    }
}




