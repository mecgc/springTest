package com.cgc.demo.mvc.controller;

import com.cgc.demo.mvc.service.IQueryService;
import com.cgc.spring.framework.annotation.GCAutowired;
import com.cgc.spring.framework.annotation.GCController;
import com.cgc.spring.framework.annotation.GCRequestMapping;
import com.cgc.spring.framework.annotation.GCRequestParam;
import com.cgc.spring.framework.webmvc.servlet.v2.GCModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 *
 */
@GCController
@GCRequestMapping("/")
public class PageAction {

    @GCAutowired
    IQueryService queryService;

    @GCRequestMapping("/first.html")
    public GCModelAndView query(@GCRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new GCModelAndView("first.html",model);
    }

}
