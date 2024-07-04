package com.meeting.domain.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName meeting_participants
 */
@TableName(value ="meeting_participants")
@Data
public class MeetingParticipants implements Serializable {
    /**
     * 
     */
    private Integer meetingId;

    /**
     * 
     */
    private Long userId;

    /**
     * 0:未签到，1：已签到 
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}