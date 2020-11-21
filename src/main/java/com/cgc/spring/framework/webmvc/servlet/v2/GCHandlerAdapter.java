package com.cgc.spring.framework.webmvc.servlet.v2;

import com.cgc.spring.framework.annotation.GCRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: <br/>
 * date: 2020-11-20 21:33<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCHandlerAdapter {
    public GCModelAndView handler(HttpServletRequest req, HttpServletResponse resp, GCHandlerMapping handler) throws Exception {
        //保存形参列表
        //将参数名称和参数的位置，这种关系保存起来
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();

        //通过运行时的状态去拿
        //一个形参可能被多个注解修饰 所以这里是个二维数组
        Annotation[][] pa = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof GCRequestParam) {
                    String paramName = ((GCRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }
        //初始化一下
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramterType = paramTypes[i];
            if (paramterType == HttpServletRequest.class || paramterType == HttpServletResponse.class) {
                paramIndexMapping.put(paramterType.getName(), i);
            }
        }
        //去拼接实参列表
        //http://localhost/web/query?name=Tom&Cat
        //同一个实参可能多个值，所以是个数组
        Map<String,String[]> params = req.getParameterMap();

        Object [] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String,String[]> param : params.entrySet()) {
            //取到http://localhost/web/query?name=Tom&Cat 的tom,cat
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]","")//数组转化成字符串会有【】
                    .replaceAll("\\s+",",");//去空格

            if(!paramIndexMapping.containsKey(param.getKey())){continue;}

            int index = paramIndexMapping.get(param.getKey());

            //允许自定义的类型转换器Converter
            paramValues[index] = castStringValue(value,paramTypes[index]);
        }

        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        Object result = handler.getMethod().invoke(handler.getController(),paramValues);
        if(result == null || result instanceof Void){return null;}

        boolean isModelAndView = handler.getMethod().getReturnType() == GCModelAndView.class;
        if(isModelAndView){
            return (GCModelAndView)result;
        }
        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }else if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else if(Double.class == paramType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }

    }
}
