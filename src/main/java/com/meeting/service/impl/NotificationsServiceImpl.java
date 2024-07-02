package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojo.Notifications;
import com.meeting.mapper.NotificationsMapper;
import com.meeting.service.NotificationsService;
import org.springframework.stereotype.Service;

/**
* @author shanmingxi
* @description 针对表【notifications】的数据库操作Service实现
* @createDate 2024-07-01 16:58:36
*/
@Service
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, Notifications>
    implements NotificationsService {

}




