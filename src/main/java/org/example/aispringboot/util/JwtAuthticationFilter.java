package org.example.aispringboot.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.ResultCode;
import org.example.aispringboot.config.SecurityConfig;
import org.example.aispringboot.enums.UserStatus;
import org.example.aispringboot.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthticationFilter extends OncePerRequestFilter {
    private final UserService userService;

    public JwtAuthticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean publicPath = SecurityConfig.isPublicPath(request.getRequestURI());
        String token=JwtTokenUtil.extractToken(request);
        if(StringUtils.hasText(token)){
            ResultCode authenticationFailure = null;
            try {
                //验证token
                JwtTokenUtil.JwtToken validationResult=JwtTokenUtil.TokenVerificationResult(token);
                if(validationResult==null || !validationResult.getValid()){
                    authenticationFailure = ResultCode.TOKEN_INVALID;
                } else {
                    UserLoginResponseDTO.UserDetailDTO user = userService.getUserById(validationResult.getUserId());
                    if(user!=null&& UserStatus.NORMAL.getCode().equals(user.getStatus())){
                    //创建spring security认证对象
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_"+validationResult.getRoleType()));
                    //创建usernamePasswordAuthenticationToken对象
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getUsername(), null, authorities);
                    //设置认证信息到spring security上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    //将token存贮到请求属性
                        request.setAttribute("jwtToken", token);
                    }else {
                        authenticationFailure = ResultCode.TOKEN_ACCESS_FORBIDDEN;
                    }
                }
            } catch (RuntimeException exception) {
                authenticationFailure = ResultCode.TOKEN_INVALID;
            }
            if (authenticationFailure != null) {
                continuePublicOrReject(publicPath, request, response, filterChain, authenticationFailure);
                return;
            }
        }else{
            clearSecurityContext();
            if (!publicPath) {
                ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
                return;
            }
        }
        //继续过滤器链
        filterChain.doFilter(request, response);
    }

    private void continuePublicOrReject(boolean publicPath, HttpServletRequest request,
                                        HttpServletResponse response, FilterChain filterChain,
                                        ResultCode resultCode) throws ServletException, IOException {
        clearSecurityContext();
        if (publicPath) {
            filterChain.doFilter(request, response);
            return;
        }
        ResponseUtil.writeError(response, resultCode);
    }

    //清理spring security上下文
    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
