package com.rocket.myshop.dto.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 针对增删改带有事务的操作统一返回数据格式。
* 校验失败信息（violations）由全局异常处理器统一输出，此处仅承载业务结果。
 */
public class ShopTxResult implements Serializable {

	private static final long serialVersionUID = 713192640075470698L;

	private String message = "操作失败！";

	private Map<String, Object> reasons = new HashMap<>();

	private String version;

	private boolean success;

	public ShopTxResult() {
	}

	public ShopTxResult(String message, boolean success) {
		this.message = message;
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getReasons() {
		return reasons;
	}

	public void setReasons(Map<String, Object> reasons) {
		this.reasons = reasons;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
