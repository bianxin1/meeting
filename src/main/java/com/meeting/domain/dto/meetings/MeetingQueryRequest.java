package com.meeting.domain.dto.meetings;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.meeting.domain.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class MeetingQueryRequest extends PageRequest implements Serializable {
    /**
     *
     */
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
}
