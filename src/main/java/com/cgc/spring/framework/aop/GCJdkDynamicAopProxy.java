package com.cgc.spring.framework.aop;

import com.cgc.spring.framework.aop.aspect.GCAdvice;
import com.cgc.spring.framework.aop.support.GCAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Description: <br/>
 * date: 2020-11-22 18:12<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCJdkDynamicAopProxy implements InvocationHandler {
    private GCAdvisedSupport config;
    public GCJdkDynamicAopProxy(GCAdvisedSupport config) {
        this.config=config;
    }
    /*
    newProxyInstance，方法有三个参数：

    loader: 用哪个类加载器去加载代理对象

    interfaces:动态代理类需要实现的接口

    h:动态代理方法在执行时，会调用h里面的invoke方法去执行
     */
    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),this.config.getTargetClass().getInterfaces(),this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, GCAdvice> advices = config.getAdvices(method,null);

        Object returnValue;
        try {
            invokeAdivce(advices.get("before"));

            returnValue = method.invoke(this.config.getTarget(),args);

            invokeAdivce(advices.get("after"));
        }catch (Exception e){
            invokeAdivce(advices.get("afterThrow"));
            throw e;
        }

        return returnValue;
    }

    private void invokeAdivce(GCAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
