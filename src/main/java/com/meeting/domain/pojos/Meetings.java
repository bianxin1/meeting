package com.meeting.domain.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName meetings
 */
@TableName(value ="meetings")
@Data
public class Meetings implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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

    /**
     * 
     */
    private String description;
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}