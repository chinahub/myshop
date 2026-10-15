package com.rocket.myshop.dto.mall;

import java.io.Serializable;

/**
 * 登录/注册成功返回：token + 不含密码哈希的用户信息。
 * token 随服务重启失效（内存态），前端存 localStorage 并在请求头 X-Mall-Token 上携带。
 */
public class LoginResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;

    private Integer userId;

    private String username;

    private String nickname;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
