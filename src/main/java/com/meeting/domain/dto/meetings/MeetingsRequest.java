package com.meeting.domain.dto.meetings;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

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

    private List<Long> usersIds;
}
