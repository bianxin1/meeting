package com.meeting.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.commen.result.Result;
import com.meeting.config.JwtProperties;
import com.meeting.domain.dtos.LoginDto;
import com.meeting.domain.dtos.RegisterDto;
import com.meeting.domain.pojos.Users;
import com.meeting.domain.vos.LoginVo;
import com.meeting.mapper.UsersMapper;
import com.meeting.service.UsersService;
import com.meeting.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.meeting.commen.constants.RedisKey.USER_CODE_KEY;

/**
* @author shanmingxi
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-07-01 17:56:56
*/
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService {
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtTool jwtTool;
    private final JwtProperties jwtProperties;
    private final UsersMapper usersMapper;


    @Override
    public Result register(RegisterDto registerDto) {
        // 1. 校验验证码
        String code = stringRedisTemplate.opsForValue().get(USER_CODE_KEY + registerDto.getEmail());
        if (code == null || !code.equals(registerDto.getCode())) {
            return Result.fail("验证码错误");
        }
        // 2. 注册
        Users user = new Users();
        BeanUtils.copyProperties(registerDto, user);
        // 3. 生成唯一account
        user.setAccount("U" + System.currentTimeMillis());
        user.setRole(3);//未激活
        // 4. 保存
        save(user);
        return Result.succ("注册成功,请等待管理员审核");
    }

    /**
     * 登录
     * @param loginDto
     * @return
     */
    @Override
    public Result login(LoginDto loginDto) {
        // 1. 校验用户权限 判断是否激活
        Users user = usersMapper.selectByAccountOrEmail(loginDto.getEmail(),loginDto.getAccount());
        if (user == null) {
            return Result.fail("用户不存在");
        }
        if (user.getRole()==3){
            return Result.fail("用户未激活");
        }
        // 2. 校验密码
        if (!user.getPassword().equals(loginDto.getPassword())) {
            return Result.fail("密码错误");
        }
        //生成jwt
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        LoginVo loginVo = new LoginVo();
        BeanUtils.copyProperties(user, loginVo);
        loginVo.setToken(token);
        return Result.succ(loginVo);
    }

    /**
     * 发送验证码
     * @param registerDto
     * @return
     */
    @Override
    public Result sendCode(RegisterDto registerDto) {
        // 1. 判断是否已经发送过验证码
        String code = stringRedisTemplate.opsForValue().get(USER_CODE_KEY + registerDto.getEmail());
        if (code != null) {
            return Result.fail("验证码已发送，请稍后再试");
        }
        // 2. 生成验证码
        String createCode = RandomUtil.randomNumbers(6);
        // 3. 保存验证码
        stringRedisTemplate.opsForValue().set(USER_CODE_KEY + registerDto.getEmail(), createCode, 5, TimeUnit.MINUTES);
        // TODO 4. 通过邮件发送验证码
        // 5. 返回
        return Result.succ("验证码发送成功");
    }
}




