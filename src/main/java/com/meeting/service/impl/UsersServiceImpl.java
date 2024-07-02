package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojos.Users;
import com.meeting.mapper.UsersMapper;
import com.meeting.service.UsersService;
import org.springframework.stereotype.Service;

/**
* @author shanmingxi
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-07-01 17:56:56
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService {

}




