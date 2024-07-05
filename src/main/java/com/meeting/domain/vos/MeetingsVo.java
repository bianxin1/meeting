package com.meeting.domain.vos;

import lombok.Data;

import java.util.Date;
@Data
public class MeetingsVo {
    private Integer id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Integer participantCount;
    /**
     *
     */
    private Integer roomId;

    /**
     *
     */
    private String roomName;
    private Long userId;
    private String userName;
    /**
     *
     */
    private Date startTime;

    /**
     *
     */
    private Date endTime;

    /**
     *
     */
    private String description;
    private Integer status;
    /** 0 未审批 ，1 被拒绝 ，2 未开始，3进行中，4 已结束,5 已取消
     *
     *
     */
}
