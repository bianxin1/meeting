package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Rooms;
import com.meeting.mapper.RoomsMapper;
import com.meeting.service.RoomsService;
import org.springframework.stereotype.Service;

/**
* @author shanmingxi
* @description 针对表【rooms】的数据库操作Service实现
* @createDate 2024-07-01 17:56:16
*/
@Service
public class RoomsServiceImpl extends ServiceImpl<RoomsMapper, Rooms>
    implements RoomsService {

}




