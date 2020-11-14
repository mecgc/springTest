package com.cgc.demo.mvc.service.impl;

import com.cgc.demo.mvc.service.IDemoService;
import com.cgc.spring.framework.annotation.GCService;

/**
 * 核心业务逻辑
 */
@GCService
public class DemoServiceImpl implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}
