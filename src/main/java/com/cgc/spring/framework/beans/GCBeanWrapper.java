package com.cgc.spring.framework.beans;

/**
 * Description: <br/>
 * date: 2020-11-14 18:19<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCBeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrapperdClass;

    public GCBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
        this.wrapperdClass=wrapperInstance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }


    public Class<?> getWrapperdClass() {
        return wrapperdClass;
    }

}
