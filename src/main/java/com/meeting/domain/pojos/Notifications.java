package com.meeting.domain.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName notifications
 */
@TableName(value ="notifications")
@Data
public class Notifications implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private Integer meetingId;

    /**
     * 
     */
    private String message;

    /**
     * 
     */
    private Integer isRead;

    /**
     * 
     */
    private Date timestamp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}