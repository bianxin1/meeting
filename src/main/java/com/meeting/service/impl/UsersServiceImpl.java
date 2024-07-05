package com.meeting.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.commen.result.Result;
import com.meeting.config.JwtProperties;

import com.meeting.domain.dto.users.*;
import com.meeting.domain.pojos.Users;
import com.meeting.domain.vos.LoginVo;
import com.meeting.mapper.UsersMapper;
import com.meeting.service.UsersService;
import com.meeting.utils.EmailTool;
import com.meeting.utils.JwtTool;
import com.meeting.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.meeting.commen.constants.RedisKey.USER_CODE_KEY;

/**
* @author shanmingxi
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-07-01 17:56:56
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService {
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtTool jwtTool;
    private final JwtProperties jwtProperties;
    private final UsersMapper usersMapper;
    private final EmailTool emailTool;
    @Autowired
    private HttpSession session;

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
        user.setRole(2);//未激活
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
        if (user.getRole()==2){
            return Result.fail("用户未激活");
        }
        // 2. 校验密码
        if (!user.getPassword().equals(loginDto.getPassword())) {
            return Result.fail("密码错误");
        }
        //生成jwt
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        LoginVo loginVo = new LoginVo();
        loginVo.setAccount(user.getName());
        if (user.getRole()==1){
            loginVo.setRole("admin");
        }else {
            loginVo.setRole("user");
        }
        loginVo.setToken(token);
        log.info("登录成功:token={}", token);
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
        // 2. 判断邮箱账号是否被注册
        QueryWrapper<Users> queryWrapper = Wrappers.query();
        queryWrapper.eq("email", registerDto.getEmail());
        Users user = usersMapper.selectOne(queryWrapper);
        if (user != null) {
            return Result.fail("邮箱已被注册");
        }
        // 3. 生成验证码
        String createCode = RandomUtil.randomNumbers(6);
        // 4. 保存验证码
        stringRedisTemplate.opsForValue().set(USER_CODE_KEY + registerDto.getEmail(), createCode, 5, TimeUnit.MINUTES);
        // 5. 发送邮件
        String content = "您的验证码是：" + createCode + "，请在5分钟内输入";
        String title = "会议室预约系统";
        emailTool.sendEmail(registerDto.getEmail(), content, title);
        // 6. 返回
        return Result.succ("验证码发送成功");
    }

    /**
     * 修改密码
     * @param passwordDto
     * @return
     */
    @Override
    public Result updatePassword(PasswordDto passwordDto) {
        // 1. 校验密码
        Long userId = UserContext.getUser();
        Users user = usersMapper.selectById(userId);
        if (!user.getPassword().equals(passwordDto.getOldPassword())) {
            return Result.fail("原密码错误");
        }
        // 2. 修改密码
        user.setPassword(passwordDto.getNewPassword());
        usersMapper.updateById(user);
        return Result.succ("修改成功");
    }

    /**
     * 修改个人信息
     * @param updateInfoDto
     * @return
     */
    @Override
    public Result updateInfo(UpdateInfoDto updateInfoDto) {
        // 1. 修改信息
        Long userId = UserContext.getUser();
        Users user = usersMapper.selectById(userId);
        BeanUtils.copyProperties(updateInfoDto, user);
        usersMapper.updateById(user);
        return Result.succ("修改成功");
    }

    /**
     * 审核通知
     * @param confirmDto
     * @return
     */
    //TODO 应做成批量处理更为合理
    @Override
    public Result confirm(ConfirmDto confirmDto) {
        // 1. 校验权限
        Long userId = UserContext.getUser();
        Users user = usersMapper.selectById(userId);
        if (user.getRole() != 1) {
            return Result.fail("权限不足");
        }
        // 2. 修改用户状态
        Integer status = confirmDto.getStatus();
        if (status == 0) {
            // 未通过
            Users updateUser = new Users();
            updateUser.setId(confirmDto.getUserId());
            updateUser.setRole(3);
            usersMapper.updateById(updateUser);
            Users u = usersMapper.selectById(confirmDto.getUserId());
            String title = "会议室预约系统";
            String content = "您的账号未通过审核，未通过审核原因："+confirmDto.getReason();
            emailTool.sendEmail(u.getEmail(), content, title);
            return Result.succ("审核成功");
        }
        Users updateUser = new Users();
        updateUser.setId(confirmDto.getUserId());
        updateUser.setRole(0);
        usersMapper.updateById(updateUser);
        // 3. 发送邮件
        Users u = usersMapper.selectById(confirmDto.getUserId());
        String title = "会议室预约系统";
        String content = "您的账号已通过审核，可以正常使用系统.用户唯一id为："+u.getAccount();
        emailTool.sendEmail(u.getEmail(), content, title);
        return Result.succ("审核成功");
    }
}




