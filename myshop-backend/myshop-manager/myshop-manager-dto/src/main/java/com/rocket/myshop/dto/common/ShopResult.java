package com.rocket.myshop.dto.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通用查询返回：total/list 分页信息 + queryParam 回显 + version/success 状态。
 */
public class ShopResult implements Serializable {

	private static final long serialVersionUID = 397411232804869934L;

	private int total;

	private List<?> list;

	// 第一条数据的起始位置，比如0代表第一条数据
	private int start;

	/**
	 * 告诉服务器每页显示的条数，这个数字会等于返回的 data集合的记录数，
	 * 可能会大于因为服务器可能没有那么多数据。这个也可能是-1，代表需要返回全部数据
	 */
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
