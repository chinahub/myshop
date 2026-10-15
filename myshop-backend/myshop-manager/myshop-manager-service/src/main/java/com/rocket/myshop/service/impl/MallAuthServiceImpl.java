package com.rocket.myshop.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.myshop.domain.MallUser;
import com.rocket.myshop.dto.mall.LoginRequest;
import com.rocket.myshop.dto.mall.LoginResult;
import com.rocket.myshop.dto.mall.RegisterRequest;
import com.rocket.myshop.exception.MallAuthException;
import com.rocket.myshop.mapper.MallUserMapper;
import com.rocket.myshop.service.MallAuthService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.LambdaQueryWrapper;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("MallAuthService")
@Transactional(rollbackFor = Exception.class)
public class MallAuthServiceImpl implements MallAuthService {

	/**
	 * token -> userId。内存态：重启后全部失效（玩具级会话，见接口注释）。
	 */
	private final Map<String, Integer> tokens = new ConcurrentHashMap<>();

	@Autowired
	MallUserMapper mallUserMapper;

	@Override
	public LoginResult register(RegisterRequest request) {
		String username = request.getUsername().trim();
		MallUser exist = mallUserMapper.selectOne(
				new LambdaQueryWrapper<MallUser>().eq(MallUser::getUsername, username));
		if (exist != null) {
			throw new MallAuthException("用户名已被注册");
		}
		MallUser user = new MallUser();
		user.setUsername(username);
		user.setNickname(request.getNickname() == null || request.getNickname().isBlank()
				? username : request.getNickname().trim());
		String salt = UUID.randomUUID().toString().replace("-", "");
		user.setSalt(salt);
		user.setPassword(sha256Hex(salt + request.getPassword()));
		user.setStatus(Boolean.TRUE);
		Date now = new Date();
		user.setCreateTime(now);
		user.setUpdateTime(now);
		mallUserMapper.insert(user);
		return issueToken(user);
	}

	@Override
	public LoginResult login(LoginRequest request) {
		String username = request.getUsername().trim();
		MallUser user = mallUserMapper.selectOne(
				new LambdaQueryWrapper<MallUser>().eq(MallUser::getUsername, username));
		if (user == null) {
			throw new MallAuthException("用户名或密码不正确");
		}
		if (!Boolean.TRUE.equals(user.getStatus())) {
			throw new MallAuthException("账号已被禁用，请联系管理员");
		}
		if (!sha256Hex(user.getSalt() + request.getPassword()).equals(user.getPassword())) {
			throw new MallAuthException("用户名或密码不正确");
		}
		return issueToken(user);
	}

	@Override
	public void logout(String token) {
		if (token != null && !token.isBlank()) {
			tokens.remove(token);
		}
	}

	@Override
	public MallUser currentUser(String token) {
		if (token == null || token.isBlank()) {
			throw new MallAuthException("请先登录");
		}
		Integer userId = tokens.get(token);
		if (userId == null) {
			throw new MallAuthException("登录已过期，请重新登录");
		}
		MallUser user = mallUserMapper.selectById(userId);
		if (user == null || !Boolean.TRUE.equals(user.getStatus())) {
			tokens.remove(token);
			throw new MallAuthException("登录已过期，请重新登录");
		}
		return user;
	}

	private LoginResult issueToken(MallUser user) {
		String token = UUID.randomUUID().toString().replace("-", "");
		tokens.put(token, user.getId());
		LoginResult result = new LoginResult();
		result.setToken(token);
		result.setUserId(user.getId());
		result.setUsername(user.getUsername());
		result.setNickname(user.getNickname());
		return result;
	}

	private static String sha256Hex(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder(bytes.length * 2);
			for (byte b : bytes) {
				sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// JDK 必带 SHA-256，理论不可达
			throw new IllegalStateException(e);
		}
	}
}
