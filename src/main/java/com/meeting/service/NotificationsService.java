package com.meeting.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.domain.pojos.Notifications;

import java.util.List;

/**
* @author shanmingxi
* @description 针对表【notifications】的数据库操作Service
* @createDate 2024-07-01 16:58:36
*/
public interface NotificationsService extends IService<Notifications> {
    /**
     * 保存通知
     * @param meetingId
     * @param userIds
     * @param meetingName
     */
    void saveNotifications(Integer meetingId, List<Long> userIds, String meetingName);
}
