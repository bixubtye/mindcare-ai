package org.example.aispringboot.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.command.UserLoginCommandDTO;
import org.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.enums.UserType;
import org.example.aispringboot.exception.BusinessException;
import org.example.aispringboot.mapper.UserMapper;
import org.example.aispringboot.service.convert.UserConvert;
import org.example.aispringboot.util.JwtTokenUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public UserLoginResponseDTO login(UserLoginCommandDTO commandDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, commandDTO.getUsername())
                .or()
                .eq(User::getEmail, commandDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        //验证用户是否存在
        if(user == null) {
            throw new BusinessException("用户不存在");
        }
        //验证密码
        String inputPassword = commandDTO.getPassword().trim();
        if(!passwordEncoder.matches(inputPassword, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        //验证用户状态
        if(!user.isActive()) {
            throw new BusinessException("用户状态异常");
        }

        //生成token
        String token = JwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        //转换为响应DTO
        UserLoginResponseDTO loginResponseDTO = UserConvert.entityToLoginResponse(token, UserConvert.entityToDetailResponse(user));
        return loginResponseDTO;
    }
    public UserLoginResponseDTO.UserDetailDTO register(UserRegisterCommandDTO commandDTO) {
        //验证密码是否一致
        if(!commandDTO.getPassword().equals(commandDTO.getConfirmPassword())) {
            throw new BusinessException("密码不一致");
        }
        //验证用户名是否存在
        LambdaQueryWrapper<User> userNameQuery = new LambdaQueryWrapper<>();
        userNameQuery.eq(User::getUsername, commandDTO.getUsername());
        if(userMapper.selectCount(userNameQuery)>0) {
            throw new BusinessException("用户名已存在");
        }
        //验证邮箱是否存在
        LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(User::getEmail, commandDTO.getEmail());
        if(userMapper.selectCount(emailQuery)>0) {
            throw new BusinessException("邮箱已存在");}
        //用户类型
        if(!UserType.isValidCode(commandDTO.getUserType())) {
            throw new BusinessException("用户类型错误");
        }
        //创建用户
        String password=commandDTO.getPassword().trim();
        String encodedPassword = passwordEncoder.encode(password);
        User user = UserConvert.RegisterCommandToEntity(commandDTO,encodedPassword);
        userMapper.insert(user);
        return UserConvert.entityToDetailResponse(user);
    }
    public UserLoginResponseDTO.UserDetailDTO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if(user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }
}
