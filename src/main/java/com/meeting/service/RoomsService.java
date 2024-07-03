package com.meeting.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.domain.pojos.Rooms;

import java.util.Date;
import java.util.List;

/**
* @author shanmingxi
* @description 针对表【rooms】的数据库操作Service
* @createDate 2024-07-01 17:56:16
*/
public interface RoomsService extends IService<Rooms> {
    public void initializeRoomStatus(List<Rooms> rooms);
    public List<Rooms> getAvailableRooms(Date startTime, Date endTime);
}
