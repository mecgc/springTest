package com.cgc.mvcframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GCRequestParam {
    String value() default "";
}
