package com.rocket.myshop.dto.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
* @Description: 列表查询统一返回数据格式
* @version V1.0.0
 */
public class ShopQueryResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private int total;

	private List<?> list;

	private int start;

	private int length;

	private Map<String, Object> queryParam;

	private String version;

	private boolean success;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Map<String, Object> getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(Map<String, Object> queryParam) {
		this.queryParam = queryParam;
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
