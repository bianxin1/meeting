package com.meeting.utils;

import com.meeting.commen.annotation.RoleCheck;
import com.meeting.domain.pojos.Users;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

@Aspect
@Component
public class RoleCheckAspect {

    @Autowired
    private HttpSession session;

    @Before("@annotation(com.meeting.commen.annotation.RoleCheck)")
    public void checkRole(JoinPoint joinPoint) throws Throwable {
        // 获取当前用户的角色
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new RuntimeException("User not logged in");
        }

        // 获取被拦截的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的注解
        RoleCheck roleCheck = method.getAnnotation(RoleCheck.class);
        if (roleCheck != null) {
            // 获取注解中的 requiredRole 值
            int requiredRole = roleCheck.requiredRole();

            // 检查用户角色是否满足要求
            if (currentUser.getRole() < requiredRole) {
                throw new RuntimeException("角色不满足要求");
            }
        }
    }
}