package com.cgc.demo.mvc.service.impl;


import com.cgc.demo.mvc.service.IQueryService;
import com.cgc.spring.framework.annotation.GCService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 * @author Tom
 *
 */
@GCService
public class QueryServiceImpl implements IQueryService {

	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		int i=8/0;
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		return json;
	}

}
