package com.rocket.myshop.service;

import com.rocket.myshop.domain.MallUser;
import com.rocket.myshop.dto.mall.LoginRequest;
import com.rocket.myshop.dto.mall.LoginResult;
import com.rocket.myshop.dto.mall.RegisterRequest;

/**
 * 商城会员认证服务：注册/登录（发 token）/登出/解析当前用户。
 * token 为内存态（token -> userId），服务重启后失效，前端需重新登录。
 */
public interface MallAuthService {

    /** 注册并直接登录，用户名重复时抛 MallAuthException。 */
    LoginResult register(RegisterRequest request);

    /** 登录成功发 token，用户名或密码错误、账号被禁用时抛 MallAuthException。 */
    LoginResult login(LoginRequest request);

    /** 登出：token 无效时静默成功。 */
    void logout(String token);

    /** 根据 token 解析当前登录会员，token 缺失/无效/会员被禁用时抛 MallAuthException。 */
    MallUser currentUser(String token);
}
