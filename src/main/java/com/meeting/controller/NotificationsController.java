package com.meeting.controller;

import com.meeting.commen.result.Result;
import com.meeting.commen.result.ScrollResult;
import com.meeting.domain.vos.MeetingDetailsVo;
import com.meeting.domain.vos.NotificationVo;
import com.meeting.service.NotificationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "通知管理")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationsController {
    private final NotificationsService notificationsService;
    @ApiOperation("获取通知列表")
    @GetMapping("/scroll")
    public Result<ScrollResult> scroll(@RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return Result.succ(notificationsService.scroll(max, offset));
    }
    @ApiOperation("标记通知为已读")
    @GetMapping("/read")
    public Result<MeetingDetailsVo> read(@RequestParam("notificationId") Integer notificationId, @RequestParam("meetingId") Integer meetingId) {
        return Result.succ(notificationsService.read(notificationId, meetingId));
    }
}
