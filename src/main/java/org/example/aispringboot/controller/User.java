package org.example.aispringboot.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.aispringboot.DTO.command.UserLoginCommandDTO;
import org.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.service.UserService;
import org.example.aispringboot.util.JwtTokenUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class User {
    @Resource
    private UserService userService;
    //用户登录接口
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO) {
        System.out.println(commandDTO);
        UserLoginResponseDTO result = userService.login(commandDTO);
        return Result.ok(result);
    }
    //用户注册接口
    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailDTO> register(@Valid @RequestBody UserRegisterCommandDTO commandDTO) {
        UserLoginResponseDTO.UserDetailDTO result = userService.register(commandDTO);
        return Result.ok(result);
    }
    @GetMapping("/current")
    public Result<UserLoginResponseDTO.UserDetailDTO> getCurrentUser() {
        //获取token
        String token= JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.validateToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        //调取service获取用户详情
        UserLoginResponseDTO.UserDetailDTO userDetailDTO = userService.getUserById(userId);
        return Result.ok(userDetailDTO);
    }
}
