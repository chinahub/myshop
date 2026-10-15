package com.rocket.myshop.exception;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.jiebaba.summer.web.annotation.ExceptionHandler;
import cn.jiebaba.summer.web.annotation.RestControllerAdvice;
import cn.jiebaba.summer.web.annotation.ResponseStatus;
import cn.jiebaba.summer.web.http.WebResponse;
import cn.jiebaba.summer.web.validation.ValidationException;

/**
 * 全局异常处理：替代原 RestfulExceptionInterceptor/Resolver/Handler 三件套。
 * - Bean 校验失败（violations 非空）：400 + violations 列表
 * - messageCode 以 Error. 开头的业务异常：状态码取 exception/exceptionStatusMapping.properties，
 *   文案取 messages/exception(_zh_CN).properties
 * - 其他异常：按异常类型映射状态码（深度优先匹配），默认 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String DEFAULT_MESSAGE = "系统出现未知问题，请联系管理员!";
	private static final String DEFAULT_MESSAGE_CODE = "Error.0";
	private static final String MORE_INFO_URL = "mailto:support@rocket.com";

	private static final Map<String, String> STATUS_MAPPING = loadProperties(
			"exception/exceptionStatusMapping.properties");
	private static final Map<String, String> MESSAGES = loadProperties("messages/exception.properties",
			"messages/exception_zh_CN.properties");

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(400)
	public Object handleValidation(ValidationException ex) {
		if (ex.violations() == null || ex.violations().isEmpty()) {
			return businessError(new RuntimeException(String.valueOf(ex.getMessage())));
		}
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", 400);
		body.put("error", "Bad Request");
		List<String> violations = new ArrayList<>();
		ex.violations().forEach(v -> violations.add(v.toString()));
		body.put("violations", violations);
		return body;
	}

	@ExceptionHandler(Exception.class)
	public RestError handleOther(Exception ex, WebResponse response) {
		RestError error = businessError(ex);
		response.status(Integer.parseInt(error.getStatus()));
		return error;
	}

	private RestError businessError(Exception ex) {
		String status = resolveStatus(ex);
		RestError error = new RestError(status);
		String messageCode = ex.getMessage();
		if (messageCode != null && messageCode.startsWith("Error.")) {
			error.setCode(messageCode);
			error.setMessage(MESSAGES.getOrDefault(messageCode, DEFAULT_MESSAGE));
		} else {
			error.setCode(DEFAULT_MESSAGE_CODE);
			error.setMessage(messageCode != null && !messageCode.isBlank() ? messageCode : DEFAULT_MESSAGE);
		}
		error.setMoreInfoUrl(MORE_INFO_URL);
		return error;
	}

	/**
	 * 按异常类继承深度匹配映射键（与原 RestfulExceptionHandler.getDepth 语义一致）。
	 */
	private String resolveStatus(Throwable ex) {
		String dominantKey = null;
		int deepest = Integer.MAX_VALUE;
		for (String key : STATUS_MAPPING.keySet()) {
			int depth = depth(key, ex.getClass(), 0);
			if (depth >= 0 && depth < deepest) {
				deepest = depth;
				dominantKey = key;
			}
		}
		return dominantKey == null ? "500" : STATUS_MAPPING.get(dominantKey);
	}

	private int depth(String mapping, Class<?> exceptionClass, int depth) {
		if (exceptionClass.getName().contains(mapping)) {
			return depth;
		}
		if (exceptionClass.equals(Throwable.class)) {
			return -1;
		}
		return depth(mapping, exceptionClass.getSuperclass(), depth + 1);
	}

	private static Map<String, String> loadProperties(String... paths) {
		Map<String, String> merged = new LinkedHashMap<>();
		ClassLoader cl = GlobalExceptionHandler.class.getClassLoader();
		for (String path : paths) {
			try (InputStream is = cl.getResourceAsStream(path)) {
				if (is != null) {
					Properties props = new Properties();
					props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
					props.forEach((k, v) -> merged.put(String.valueOf(k), String.valueOf(v)));
				}
			} catch (IOException e) {
				// 配置缺失时走默认值
			}
		}
		return merged;
	}
}
