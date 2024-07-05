package com.meeting.domain.vos;

import com.meeting.domain.pojos.Users;
import lombok.Data;
import org.apache.catalina.User;

import java.util.Date;
import java.util.List;

@Data
public class MeetingDetailsVo { private Integer id;
    private Long userId;
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
    private String roomName;

    /**
     *
     */
    private Date startTime;

    /**
     *
     */
    private Date endTime;


    /** 0 未审批 ，1 被拒绝 ，2 未开始，3进行中，4 已结束,5 已取消
     *
     *
     */
    private Integer status;
    private List<UserInfoVo> users;
}
