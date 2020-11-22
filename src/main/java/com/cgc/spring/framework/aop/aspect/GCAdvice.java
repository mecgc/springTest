package com.cgc.spring.framework.aop.aspect;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * Description: <br/>
 * date: 2020-11-22 18:47<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
@Data
public class GCAdvice {
    private Object aspect;
    private Method adviceMethod;
    private String throwName;
    public GCAdvice(Object newInstance, Method method) {
        this.aspect = newInstance;
        this.adviceMethod = method;
    }

}
