package com.cgc.demo.mvc.controller;


import com.cgc.demo.mvc.service.IDemoService;
import com.cgc.spring.framework.annotation.GCAutowired;
import com.cgc.spring.framework.annotation.GCController;
import com.cgc.spring.framework.annotation.GCRequestMapping;
import com.cgc.spring.framework.annotation.GCRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@GCController
@GCRequestMapping("/demo")
public class DemoAction {

  	@GCAutowired
	private IDemoService demoService;

	@GCRequestMapping("/query")
	public void query(HttpServletRequest req, HttpServletResponse resp,
					  @GCRequestParam("name") String name){
		String result = demoService.get(name);
//		String result = "My name is " + name;
		try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GCRequestMapping("/add")
	public void add(HttpServletRequest req, HttpServletResponse resp,
					@GCRequestParam("a") Integer a, @GCRequestParam("b") Integer b){
		try {
			resp.getWriter().write(a + "+" + b + "=" + (a + b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GCRequestMapping("/remove")
	public void remove(HttpServletRequest req,HttpServletResponse resp,
					   @GCRequestParam("id") Integer id){
	}

}
