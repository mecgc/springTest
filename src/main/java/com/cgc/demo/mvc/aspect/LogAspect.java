package com.cgc.demo.mvc.aspect;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogAspect {

    //在调用一个方法之前，执行before方法
    public void before(){
        //这个方法中的逻辑，是由我们自己写的
        System.out.println("Invoker Before Method!!!");
    }
    //在调用一个方法之后，执行after方法
    public void after(){
        System.out.println("Invoker After Method!!!");
    }

    public void afterThrowing(){

        System.out.println("出现异常");
    }
}
