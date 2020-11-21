package com.cgc.spring.framework.webmvc.servlet.v2;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Description: <br/>
 * date: 2020-11-20 21:28<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCHandlerMapping {
    private Pattern pattern;
    private Object controller;
    private Method method;
    public GCHandlerMapping(Pattern pattern, Object instance, Method method) {
        this.pattern=pattern;
        this.method=method;
        this.controller=instance;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method mothod) {
        this.method = mothod;
    }
}
