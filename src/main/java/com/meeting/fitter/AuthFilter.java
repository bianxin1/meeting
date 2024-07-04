//编写AuthGlobalFitter
package com.meeting.fitter;

import cn.hutool.core.text.AntPathMatcher;

import com.meeting.config.AuthProperties;
import com.meeting.expection.UnauthorizedException;
import com.meeting.utils.JwtTool;
import com.meeting.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

//@Component
//@RequiredArgsConstructor
//@EnableConfigurationProperties(AuthProperties.class)
//public class AuthFilter extends OncePerRequestFilter {
//    private final JwtTool jwtTool;
//    private final AuthProperties authProperties;
//    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String path = request.getRequestURI();
//        if (isExclude(path)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = null;
//        List<String> headers = Collections.list(request.getHeaders("Authorization"));
//        if (!CollectionUtils.isEmpty(headers)) {
//            token = headers.get(0);
//        }
//
//        Long userId = null;
//        try {
//            userId = jwtTool.parseToken(token);
//        } catch (UnauthorizedException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//        UserContext.setUser(userId);
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isExclude(String path) {
//        for (String pathPattern : authProperties.getExcludePaths()) {
//            if (antPathMatcher.match(pathPattern, path)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
