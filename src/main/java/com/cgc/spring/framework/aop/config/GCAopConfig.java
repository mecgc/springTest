package com.cgc.spring.framework.aop.config;

import lombok.Data;

@Data
public class GCAopConfig {
    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
