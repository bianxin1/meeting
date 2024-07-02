package com.meeting.controller;

import com.meeting.commen.result.Result;
import com.meeting.domain.dtos.LoginDto;
import com.meeting.domain.dtos.RegisterDto;
import com.meeting.service.UsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UsersService userService;

    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto registerDto) {
        // 注册
        return userService.register(registerDto);
    }
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto) {
        // 登录
        return userService.login(loginDto);
    }
    @ApiOperation("发送验证码")
    @PostMapping("/sendCode")
    public Result sendCode(@RequestBody RegisterDto registerDto) {
        // 发送验证码
        return userService.sendCode(registerDto);
    }

}
