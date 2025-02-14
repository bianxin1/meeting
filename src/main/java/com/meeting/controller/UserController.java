package com.meeting.controller;

import cn.hutool.system.UserInfo;
import com.meeting.commen.result.Result;

import com.meeting.domain.dto.users.*;
import com.meeting.domain.pojos.Departments;
import com.meeting.domain.pojos.Users;
import com.meeting.domain.vos.UnUserInfo;
import com.meeting.domain.vos.UserInfoVo;
import com.meeting.service.UsersService;
import com.meeting.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "用户管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UsersService userService;
    @Autowired
    private HttpSession session;

    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto registerDto) {
        // 注册
        return userService.register(registerDto);
    }
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto) {
        log.info("登录");
        // 登录
        return userService.login(loginDto);
    }
    @ApiOperation("发送验证码")
    @PostMapping("/sendCode")
    public Result sendCode(@RequestBody RegisterDto registerDto) {
        // 发送验证码
        return userService.sendCode(registerDto);
    }
    @ApiOperation("修改密码")
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody PasswordDto passwordDto) {
        log.info("修改密码");
        // 修改密码
        return userService.updatePassword(passwordDto);
    }
    @ApiOperation("修改个人信息")
    @PutMapping("/updateInfo")
    public Result updateInfo(@RequestBody UpdateInfoDto updateInfoDto) {
        // 修改个人信息
        return userService.updateInfo(updateInfoDto);
    }
    @ApiOperation("审核通知")
    @PostMapping("/confirm")
    public Result confirm(@RequestBody ConfirmDto confirmDto) {
        // 审核通知
        return userService.confirm(confirmDto);
    }
    @GetMapping ("/getUserInfo")
    public Result getUserInfo() {
        Long Id = UserContext.getUser();
        return userService.getInfo(Id);
    }
    @ApiOperation("未审批的用户")
    @GetMapping ("/getUnapprovedUserInfo")
    public Result getUnapprovedUserInfo() {
        List<UnUserInfo> UnUserInfoList =new ArrayList<>();
        List<Users> list = userService.list();
        for (Users user : list) {
            if (user.getRole()==2){
                UnUserInfo userInfoVo = new UnUserInfo();
                BeanUtils.copyProperties(user,userInfoVo);
                UnUserInfoList.add(userInfoVo);
            }
        }
        return Result.succ(UnUserInfoList);
    }
    @GetMapping("/list")
    public Result listAll() {
        List<Users> list = userService.list();
        return Result.succ(list);
    }

}
