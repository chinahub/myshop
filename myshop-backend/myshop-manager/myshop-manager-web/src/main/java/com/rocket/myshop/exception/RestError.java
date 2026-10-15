package com.rocket.myshop.exception;

/**
* @FullClassName com.rocket.myshop.handler.RestError
* @Description: 定义的异常实体，目前支持捕获Error和Exception
* @author  Liu Jie
* @date 2016年11月20日 下午6:09:52 
* @version V1.0.0
 */
public class RestError {

	//TODO 后期考虑使用builder模式构建
	
	//响应头中的 HTTP 状态码相同
    private String status;
    
    //特定 REST API 的异常编码，考虑到仅有 24 个熟知的通用 HTTP 异常码存在局限性
    private String code;
    
    //userMsg 是一个可读性很好的异常消息，可能会被直接展示给应用程序端的用户
    private String message;
    
    //所有对 REST API 的开发者而言可能有用的技术信息。可以在这里包含异常信息，跟踪堆栈，或者其他，
    //同时后台支持配置不显示该信息
    private String developerMessage;
    
    //moreInfo 属性指定一个 URL， 任何看到该 URL 的人都可在浏览器中点击（或复制粘贴）。
  	//URL 的目标网页应该全面的描述出现的异常情况，以及潜在的解决方案，从而帮助他们解决异常情况。
    private String moreInfoUrl;

    public RestError(String status) {
        if (status == null) {
            throw new NullPointerException("HttpStatus 参数不能为空.");
        }
        this.status = status;
    }

	public String getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public String getMoreInfoUrl() {
		return moreInfoUrl;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

	public void setMoreInfoUrl(String moreInfoUrl) {
		this.moreInfoUrl = moreInfoUrl;
	}
}