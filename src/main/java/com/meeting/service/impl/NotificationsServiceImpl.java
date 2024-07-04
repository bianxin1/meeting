package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Notifications;
import com.meeting.mapper.NotificationsMapper;
import com.meeting.service.NotificationsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author shanmingxi
* @description 针对表【notifications】的数据库操作Service实现
* @createDate 2024-07-01 16:58:36
*/
@Service
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, Notifications>
    implements NotificationsService {
    /**
     * 保存通知
     * @param meetingId
     * @param userIds
     * @param meetingName
     */
    @Override
    public void saveNotifications(Integer meetingId, List<Long> userIds, String meetingName) {
        userIds.forEach(userId -> {
            Notifications notifications = new Notifications();
            notifications.setMeetingId(meetingId);
            notifications.setUserId(userId.intValue());
            notifications.setMessage("您有一个新的会议邀请：" + meetingName);
            notifications.setIsRead(0);
            this.save(notifications);
        });
    }
}




