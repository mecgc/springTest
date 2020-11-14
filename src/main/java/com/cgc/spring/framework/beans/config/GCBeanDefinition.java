package com.cgc.spring.framework.beans.config;

/**
 * Description: <br/>
 * date: 2020-11-13 22:23<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCBeanDefinition {
    private String factorBeanyName;
    private String beanClassName;

    public void setFactorBeanyName(String factorBeanyName) {
        this.factorBeanyName = factorBeanyName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String getFactorBeanyName() {
        return factorBeanyName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public GCBeanDefinition(String beanFactoryName, String beanClassName) {
        this.factorBeanyName = beanFactoryName;
        this.beanClassName = beanClassName;
    }

    public GCBeanDefinition() {
    }
}
