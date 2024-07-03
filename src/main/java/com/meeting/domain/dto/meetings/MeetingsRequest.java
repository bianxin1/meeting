package com.meeting.domain.dto.meetings;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class MeetingsRequest {
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
    private Integer participant_count;

    /**
     *
     */
    private Integer room_id;

    /**
     *
     */
    private Date start_time;

    /**
     *
     */
    private Date end_time;

    /**
     *
     */
    private String description;
}
