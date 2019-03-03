package com.suarezlin.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 自定义响应数据结构
 * 				这个类是提供给门户，ios，安卓，微信商城用的
 * 				门户接受此类数据后需要使用本类的方法转换成对于的数据类型格式（类，或者list）
 * 				其他自行处理
 * 				200：表示成功
 * 				500：表示错误，错误信息在msg字段中
 * 				501：bean验证错误，不管多少个错误都以map形式返回
 * 				502：拦截器拦截到用户token出错
 * 				555：异常抛出信息
 */
@ApiModel(value = "标准返回类型", description = "web app 所返回 json 的标准类型")
public class CommonReturnType {

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    @ApiModelProperty(hidden = true)
    private String ok;	// 不使用

    public static CommonReturnType build(Integer status, String msg, Object data) {
        return new CommonReturnType(status, msg, data);
    }

    public static CommonReturnType ok(Object data) {
        return new CommonReturnType(data);
    }

    public static CommonReturnType ok() {
        return new CommonReturnType(null);
    }
    
    public static CommonReturnType errorMsg(String msg) {
        return new CommonReturnType(500, msg, null);
    }
    
    public static CommonReturnType errorMap(Object data) {
        return new CommonReturnType(501, "error", data);
    }
    
    public static CommonReturnType errorTokenMsg(String msg) {
        return new CommonReturnType(502, msg, null);
    }
    
    public static CommonReturnType errorException(String msg) {
        return new CommonReturnType(555, msg, null);
    }

    public CommonReturnType() {

    }

    public CommonReturnType(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public CommonReturnType(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

	public String getOk() {
		return ok;
	}

	public void setOk(String ok) {
		this.ok = ok;
	}

}
