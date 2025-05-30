/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.zqq.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zqq.common.exception.BizCodeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
@Getter
@Setter
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public R setData(Object data){
		put("data",data);
		return this;
	}

	//利用fastJson进行逆转
	public <T> T getData(TypeReference<T> typeReference){
		Object data = get("data");  //默认转换为是map类型
		String jsonString = JSON.toJSONString(data); //需要转为map在转为json再逆转
		T t = JSON.parseObject(jsonString, typeReference);
		return t;
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R error(BizCodeEnum bizCodeEnum){
		R r = new R();
		r.put("code",bizCodeEnum.getCode());
		r.put("msg",bizCodeEnum.getMsg());
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public Integer getCode(){
		return (Integer) this.get("code");
	}
}
