package org.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.example.aispringboot.enums.UserStatus;
import org.example.aispringboot.enums.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user")
@Builder
public class User {

    @TableId(type= IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    private String phone;

    private String password;

    private String nickname;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    @TableField("user_type")
    private Integer userType;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
    /**
     * 用户状态判断
     */
    public boolean isActive() {
        return UserStatus.NORMAL.getCode().equals(this.status);
    }
    /**
     * 获取用户类型显示名称
     */
    public String getUserTypeDisplayName() {
        try {
            return UserType.fromCode(userType).getDescription();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }
    /**
     * 获取用户状态名称
     */
    public String getStatusDisplayName() {
        try {
            return UserStatus.fromCode(status).getDescription();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }
    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return nickname != null&&!nickname.trim().isEmpty() ? nickname : username;
    }
}