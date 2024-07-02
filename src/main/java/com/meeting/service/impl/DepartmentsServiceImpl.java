package com.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Departments;
import com.meeting.service.DepartmentsService;
import com.meeting.mapper.DepartmentsMapper;
import org.springframework.stereotype.Service;

/**
* @author shanmingxi
* @description 针对表【departments】的数据库操作Service实现
* @createDate 2024-07-01 16:45:07
*/
@Service
public class DepartmentsServiceImpl extends ServiceImpl<DepartmentsMapper, Departments>
    implements DepartmentsService{

}




