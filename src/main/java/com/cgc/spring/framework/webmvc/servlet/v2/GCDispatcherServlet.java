package com.cgc.spring.framework.webmvc.servlet.v2;


import com.cgc.spring.framework.annotation.*;
import com.cgc.spring.framework.context.GCApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;


public class GCDispatcherServlet extends HttpServlet {

    //存储aplication.properties的配置内容
    private Properties contextConfig = new Properties();

    GCApplicationContext applicationContext;
    //保存Contrller中所有Mapping的对应关系

    private Map<String, Method> handlerMapping = new HashMap<String, Method>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //派遣，分发任务
        try {
            //委派模式
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Excetion Detail:" + Arrays.toString(e.getStackTrace()));
        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!!");
            return;
        }

        Method method = this.handlerMapping.get(url);

        //获取方法的形参列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //保存请求的url参数列表
        //第一个参数：方法所在的实例
        //第二个参数：调用时所需要的实参
        Map<String, String[]> parameterMap = req.getParameterMap();
        //保存赋值参数的位置
        Object[] paramValues = new Object[parameterTypes.length];
        //按根据参数位置动态赋值
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                paramValues[i] = req;
                continue;
            } else if (parameterType == HttpServletResponse.class) {
                paramValues[i] = resp;
                continue;
            } else if (parameterType == String.class) {

                //提取方法中加了注解的参数
                Annotation[][] pa = method.getParameterAnnotations();
                for (int j = 0; j < pa.length; j++) {
                    for (Annotation a : pa[i]) {
                        if (a instanceof GCRequestParam) {
                            String paramName = ((GCRequestParam) a).value();
                            if (!"".equals(paramName.trim())) {
                                String value = Arrays.toString(parameterMap.get(paramName))
                                        .replaceAll("\\[|\\]", "")
                                        .replaceAll("\\s", ",");
                                paramValues[i] = value;
                            }
                        }
                    }
                }

            }
        }
        //投机取巧的方式
        //通过反射拿到method所在class，拿到class之后还是拿到class的名称
        //再调用toLowerFirstCase获得beanName
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(applicationContext.getBean(beanName), paramValues);
    }

    @Override
    public void init(ServletConfig config) {

        //初始化Spring核心IoC容器
        applicationContext = new GCApplicationContext(config.getInitParameter("contextConfigLocation"));
        //5、初始化HandlerMapping
        initHandlerMapping();

        System.out.println("GC Spring framework is init.");
    }

    private void initHandlerMapping() {
        if (applicationContext.getBeanDefinitionCount()==0) {
            return;
        }

        for (String beanName : this.applicationContext.getBeanDefinitionNames()) {
            if (applicationContext.getBean(beanName)==null){
                continue;
            }
            Class<?> clazz = applicationContext.getBean(beanName).getClass();

            if (!clazz.isAnnotationPresent(GCController.class)) {
                continue;
            }

            String baseUrl = "";
            //获取Controller的url配置
            if (clazz.isAnnotationPresent(GCRequestMapping.class)) {
                GCRequestMapping requestMapping = clazz.getAnnotation(GCRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //获取Method的url配置
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {

                //没有加RequestMapping注解的直接忽略
                if (!method.isAnnotationPresent(GCRequestMapping.class)) {
                    continue;
                }

                //映射URL
                GCRequestMapping requestMapping = method.getAnnotation(GCRequestMapping.class);
                //  /demo/query

                //  (//demo//query)

                String url = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("Mapped " + url + "," + method);
            }
        }


    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
