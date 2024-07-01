package com.meeting.domain;

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
    private Integer meeting_id;

    /**
     * 
     */
    private Integer user_id;

    /**
     * 0:未签到，1：已签到 
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}