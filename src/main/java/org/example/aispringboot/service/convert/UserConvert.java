package org.example.aispringboot.service.convert;

import org.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.enums.UserStatus;

import java.time.LocalDateTime;

public class UserConvert
{
    public static UserLoginResponseDTO.UserDetailDTO entityToDetailResponse(User user) {
        return UserLoginResponseDTO.UserDetailDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .gender(user.getGender())
                .genderDisplayName(getGenderDisplayName(user.getGender()))
                .birthday(user.getBirthday())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    public static UserLoginResponseDTO entityToLoginResponse(String token,UserLoginResponseDTO.UserDetailDTO userInfo) {
        return UserLoginResponseDTO.builder()
                .token(token)
                .roleType(userInfo.getUserType().toString())
                .userInfo(userInfo)
                .build();
    }
    public static User RegisterCommandToEntity(UserRegisterCommandDTO command,String encodedPassword) {
        return User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .phone(command.getPhone())
                .password(encodedPassword)
                .nickname(command.getNickname())
                .gender(command.getGender())
                .birthday(command.getBirthday())
                .userType(command.getUserType())
                .status(UserStatus.NORMAL.getCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    /**
     * 获取性别显示名称
     * @param gender 性别代码
     * @return 性别显示名称
     */
    private static String getGenderDisplayName(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        switch (gender) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "未知";
        }
    }

}
