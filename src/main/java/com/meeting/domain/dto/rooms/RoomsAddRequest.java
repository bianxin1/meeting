package com.meeting.domain.dto.rooms;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class RoomsAddRequest {

    private Integer id;
    /**
     *
     */
    private String name;

    /**
     *
     */
    private String location;

    /**
     *
     */
    private Integer maxCapacity;

    /**
     * 0:未启用，1：已启用,2:已占用
     */
    private Integer status;

    /**
     *
     */
    private String remarks;
}
