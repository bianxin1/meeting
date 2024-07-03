package com.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.commen.result.Result;

import com.meeting.domain.dto.users.LoginDto;
import com.meeting.domain.dto.users.PasswordDto;
import com.meeting.domain.dto.users.RegisterDto;
import com.meeting.domain.dto.users.UpdateInfoDto;
import com.meeting.domain.pojos.Users;

/**
* @author shanmingxi
* @description 针对表【users】的数据库操作Service
* @createDate 2024-07-01 17:56:56
*/
public interface UsersService extends IService<Users> {
    /**
     * 注册
     * @param registerDto
     */
    Result register(RegisterDto registerDto);

    /**
     * 登录
     * @param loginDto
     * @return
     */
    Result login(LoginDto loginDto);

    /**
     * 发送验证码
     * @param registerDto
     * @return
     */
    Result sendCode(RegisterDto registerDto);

    /**
     * 修改密码
     * @param passwordDto
     * @return
     */
    Result updatePassword(PasswordDto passwordDto);

    /**
     * 修改个人信息
     * @param updateInfoDto
     * @return
     */
    Result updateInfo(UpdateInfoDto updateInfoDto);
}
