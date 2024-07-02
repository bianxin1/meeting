package com.meeting.domain.dto.deparement;

import com.meeting.domain.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = true)
public class DepartmentQueryRequest  extends PageRequest implements Serializable {
    private Integer id;

    private String name;
}
