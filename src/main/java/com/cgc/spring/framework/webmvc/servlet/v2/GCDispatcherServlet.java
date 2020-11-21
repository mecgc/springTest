package com.cgc.spring.framework.webmvc.servlet.v2;


import com.cgc.spring.framework.annotation.GCController;
import com.cgc.spring.framework.annotation.GCRequestMapping;
import com.cgc.spring.framework.annotation.GCRequestParam;
import com.cgc.spring.framework.context.GCApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GCDispatcherServlet extends HttpServlet {


    GCApplicationContext applicationContext;

    private List<GCHandlerMapping> handlerMappings = new ArrayList<GCHandlerMapping>();
    private Map<GCHandlerMapping,GCHandlerAdapter> handlerAdapters=new HashMap<GCHandlerMapping, GCHandlerAdapter>();
    private List<GCViewResolver> viewResolvers=new ArrayList<GCViewResolver>();

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
            try {
                processDispatchResult(req,resp,new GCModelAndView("500"));
            } catch (Exception e1) {
                e1.printStackTrace();
                resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、通过URL获得一个HandlerMapping
        GCHandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req,resp,new GCModelAndView("404"));
            return;
        }
        //2、根据一个HandlerMaping获得一个HandlerAdapter
        GCHandlerAdapter ha = getHandlerAdapter(handler);
        //3、解析某一个方法的形参和返回值之后，统一封装为ModelAndView对象
        GCModelAndView mv = ha.handler(req,resp,handler);
        // 就把ModelAndView变成一个ViewResolver
        processDispatchResult(req,resp,mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GCModelAndView mv) throws Exception {
        if(null == mv){return;}
        if(this.viewResolvers.isEmpty()){return;}

        for (GCViewResolver viewResolver : this.viewResolvers) {
            GCView view = viewResolver.resolveViewName(mv.getViewName());
            //直接往浏览器输出
            view.render(mv.getModel(),req,resp);
            return;
        }
    }




    private GCHandlerAdapter getHandlerAdapter(GCHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        return this.handlerAdapters.get(handler);
    }

    private GCHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){return  null;}
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        for (GCHandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if(!matcher.matches()){continue;}
            return mapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) {

        //初始化Spring核心IoC容器
        applicationContext = new GCApplicationContext(config.getInitParameter("contextConfigLocation"));

        //初始化九大组件
        initStrategies(applicationContext);
        System.out.println("GC Spring framework is init.");
    }

    private void initStrategies(GCApplicationContext context) {
//        //多文件上传的组件
//        initMultipartResolver(context);
//        //初始化本地语言环境
//        initLocaleResolver(context);
//        //初始化模板处理器
//        initThemeResolver(context);
        //handlerMapping
        initHandlerMappings(context);
        //初始化参数适配器
        initHandlerAdapters(context);
//        //初始化异常拦截器
//        initHandlerExceptionResolvers(context);
//        //初始化视图预处理器
//        initRequestToViewNameTranslator(context);
        //初始化视图转换器
        initViewResolvers(context);
//        //FlashMap管理器
//        initFlashMapManager(context);
    }
    //viewResolver保存了view的路径File
    private void initViewResolvers(GCApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new GCViewResolver(templateRoot));
        }

    }

    private void initHandlerAdapters(GCApplicationContext context) {
        for (GCHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new GCHandlerAdapter());
        }
    }

    /**
     * @description:  为handlerMapping对象赋值
     * @date: 2020-11-20
     * @params: [context]
     * @return: void
     */
    private void initHandlerMappings(GCApplicationContext context) {
        if(context.getBeanDefinitionCount() == 0){ return;}

        for (String beanName : context.getBeanDefinitionNames()) {
            Object instance = context.getBean(beanName);
            Class<?> clazz = instance.getClass();

            if(!clazz.isAnnotationPresent(GCController.class)){ continue; }

            //相当于提取 class上配置的url
            String baseUrl = "";
            if(clazz.isAnnotationPresent(GCRequestMapping.class)){
                GCRequestMapping requestMapping = clazz.getAnnotation(GCRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //只获取public的方法
            for (Method method : clazz.getMethods()) {
                if(!method.isAnnotationPresent(GCRequestMapping.class)){continue;}
                //提取每个方法上面配置的url
                GCRequestMapping requestMapping = method.getAnnotation(GCRequestMapping.class);

                // //demo//query
                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                //handlerMapping.put(url,method);
                handlerMappings.add(new GCHandlerMapping(pattern,instance,method));
                System.out.println("Mapped : " + regex + "," + method);
            }

        }
    }


}
