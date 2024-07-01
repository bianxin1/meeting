package com.meeting.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users implements Serializable {
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
    private String account;

    /**
     * 
     */
    private Integer gender;

    /**
     * 
     */
    private String password;

    /**
     * 0:普通用户，1：管理员
     */
    private Integer role;

    /**
     * 
     */
    private String telephone;

    /**
     * 
     */
    private String email;

    /**
     * 
     */
    private Integer is_deleted;

    /**
     * 
     */
    private Integer department_id;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}