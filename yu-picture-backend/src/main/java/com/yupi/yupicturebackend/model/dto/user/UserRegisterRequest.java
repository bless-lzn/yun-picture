package com.yupi.yupicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = 8735650154179439661L;
    /**
     * 用户账号，用于唯一标识一个用户
     */
    private String userAccount;

    /**
     * 用户密码，用于用户登录验证
     */
    private String userPassword;

    /**
     * 确认密码，用于在用户注册或修改密码时确保密码输入的正确性
     */
    private String checkPassword;

}
