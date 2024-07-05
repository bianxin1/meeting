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
    private Long userId;
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
    /** 0 未审批 ，1 被拒绝 ，2 未开始，3进行中，4 已结束,5 已取消
     *
     *
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}