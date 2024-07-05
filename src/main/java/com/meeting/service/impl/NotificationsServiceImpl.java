package com.meeting.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.commen.result.ScrollResult;
import com.meeting.domain.pojos.Notifications;
import com.meeting.domain.vos.MeetingDetailsVo;
import com.meeting.domain.vos.NotificationVo;
import com.meeting.mapper.NotificationsMapper;
import com.meeting.service.MeetingsService;
import com.meeting.service.NotificationsService;
import com.meeting.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.meeting.commen.constants.RedisKey.MEETING_NOTIFICATION_KET;

/**
* @author shanmingxi
* @description 针对表【notifications】的数据库操作Service实现
* @createDate 2024-07-01 16:58:36
*/
@Service
@RequiredArgsConstructor
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, Notifications>
    implements NotificationsService {
    private final StringRedisTemplate redisTemplate;
    private static final long UNREAD_WEIGHT = 1_000_000_000_000L;
    private final MeetingsService meetingsService;
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
            notifications.setUserId(userId);
            notifications.setMessage("您有一个新的会议邀请：" + meetingName);
            notifications.setIsRead(0);
            this.save(notifications);
            redisTemplate.opsForZSet().add(MEETING_NOTIFICATION_KET + userId, String.valueOf(notifications.getId()), System.currentTimeMillis()+UNREAD_WEIGHT);
        });
    }

    /**
     * 滚动查询通知
     * @param max
     * @param offset
     * @return
     */
    @Override
    public ScrollResult scroll(Long max, Integer offset) {
        // 1.获取当前用户
        Long user = UserContext.getUser();
        String key = MEETING_NOTIFICATION_KET + user;
        // 2.查询当前用户的通知列表
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, max, offset, 10);
        // 2.1判断是否有通知
        if (typedTuples == null || typedTuples.isEmpty()) {
            return new ScrollResult();
        }
        // 3.获取通知id
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 0;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            // 4.1.获取id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if (time == minTime) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }
        os = minTime == max ? os : os + offset;
        // 5.查询通知
        List<Notifications> notifications = this.list(new QueryWrapper<Notifications>().in("id", ids));

        // 6.创建一个map，以通知id为键，通知对象为值
        Map<Long, Notifications> notificationsMap = notifications.stream()
                .collect(Collectors.toMap(Notifications::getId, notification -> notification));

        // 7.按id顺序重新排序通知
        List<NotificationVo> notificationVos = ids.stream()
                .map(id -> new NotificationVo(notificationsMap.get(id)))
                .collect(Collectors.toList());

        // 8.返回结果
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setOffset(os);
        scrollResult.setList(notificationVos);
        scrollResult.setMinTime(minTime);
        return scrollResult;
    }


    /**
     * 标记通知为已读
     * @param notificationId
     * @param meetingId
     * @return
     */
    @Override
    public MeetingDetailsVo read(Integer notificationId, Integer meetingId) {
        // 1.标记为已读
        Notifications notifications = this.getById(notificationId);
        if (notifications.getIsRead() == 1) {
            return meetingsService.searchMeetingDetails(meetingId);
        }
        notifications.setIsRead(1);
        this.updateById(notifications);
        // 2.更新redis中的通知
        Long user = UserContext.getUser();
        Double score = redisTemplate.opsForZSet().score(MEETING_NOTIFICATION_KET + user, String.valueOf(notificationId));
        if (score != null) {
            redisTemplate.opsForZSet().add(MEETING_NOTIFICATION_KET + user, String.valueOf(notificationId), score - UNREAD_WEIGHT);
        }
        // 3.返回会议详情
        return meetingsService.searchMeetingDetails(meetingId);
    }


}




