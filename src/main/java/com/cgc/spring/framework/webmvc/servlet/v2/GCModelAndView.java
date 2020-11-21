package com.cgc.spring.framework.webmvc.servlet.v2;

import java.util.Map;

/**
 * Description: <br/>
 * date: 2020-11-20 22:15<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCModelAndView {
    private String viewName;
    private Map<String,?> model;
    public GCModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public GCModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
