package com.cgc.spring.framework.context;

import com.cgc.spring.framework.annotation.GCAutowired;
import com.cgc.spring.framework.annotation.GCController;
import com.cgc.spring.framework.annotation.GCService;
import com.cgc.spring.framework.beans.GCBeanWrapper;
import com.cgc.spring.framework.beans.config.GCBeanDefinition;
import com.cgc.spring.framework.beans.support.GCBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Description: <br/>
 * date: 2020-11-13 21:57<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCApplicationContext {
    private GCBeanDefinitionReader reader;
    private Map<String,GCBeanDefinition> beanDefinitionMap=new HashMap<String, GCBeanDefinition>();
    //保存beanName和实例
    private Map<String,Object> factoryBeanObjectCache = new HashMap<String, Object>();
    private Map<String,GCBeanWrapper> factoryBeanInstanceCache = new HashMap<String, GCBeanWrapper>();
    public GCApplicationContext(String... configLocations) {
        
        try {
            //1.加载配置文件
            reader=new GCBeanDefinitionReader(configLocations);
            //2、解析配置文件，并生成beanDefinition对象
            List<GCBeanDefinition>  beanDefinitions=reader.loadBaenDefinitions();
            //3.将beanDefinition缓存起来
            doRegistBeanDefinition(beanDefinitions);
            doAutowrited();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowrited() {
        //调用getBean()
        //这一步，所有的Bean并没有真正的实例化，还只是配置阶段
        for (Map.Entry<String,GCBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    private void doRegistBeanDefinition(List<GCBeanDefinition> beanDefinitions) throws Exception {
        for (GCBeanDefinition beanDefinition : beanDefinitions) {
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactorBeanyName())){
                throw new Exception("The " + beanDefinition.getFactorBeanyName() + "is exists");
            }
            beanDefinitionMap.put(beanDefinition.getFactorBeanyName(),beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
        }
    }

    //Bean的实例化，DI是从而这个方法开始的
    public Object getBean(String beanName){
        //1、先拿到BeanDefinition配置信息
        GCBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        //2、反射实例化newInstance();
        Object instance = instantiateBean(beanName,beanDefinition);
        if (instance==null){
            System.out.println(beanDefinition);
            return null;
        }
        //3、封装成一个叫做BeanWrapper
        GCBeanWrapper beanWrapper = new GCBeanWrapper(instance);

        //4、保存到IoC容器
        factoryBeanInstanceCache.put(beanName,beanWrapper);
        //5、执行依赖注入
        populateBean(beanName,beanDefinition,beanWrapper);

        return beanWrapper.getWrapperInstance();
    }

    private void populateBean(String beanName, GCBeanDefinition beanDefinition, GCBeanWrapper beanWrapper) {
        //可能涉及到循环依赖？
        //A{ B b}
        //B{ A b}
        //用两个缓存，循环两次
        //1、把第一次读取结果为空的BeanDefinition存到第一个缓存
        //2、等第一次循环之后，第二次循环再检查第一次的缓存，再进行赋值

        Object instance = beanWrapper.getWrapperInstance();

        Class<?> clazz = beanWrapper.getWrapperdClass();

        //在Spring中@Component
        if(!(clazz.isAnnotationPresent(GCController.class) || clazz.isAnnotationPresent(GCService.class))){
            return;
        }

        //把所有的包括private/protected/default/public 修饰字段都取出来
        for (Field field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(GCAutowired.class)){ continue; }

            GCAutowired autowired = field.getAnnotation(GCAutowired.class);

            //如果用户没有自定义的beanName，就默认根据类型注入
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                //field.getType().getName() 获取字段的类型
                autowiredBeanName = field.getType().getName();
            }

            //暴力访问
            field.setAccessible(true);

            try {
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){
                    continue;
                }
                //ioc.get(beanName) 相当于通过接口的全名拿到接口的实现的实例
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    private Object instantiateBean(String beanName, GCBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        if(this.factoryBeanObjectCache.containsKey(beanName)){
            instance=factoryBeanObjectCache.get(beanName);
        }else {
            try {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(GCController.class)) {
                    //2、默认的类名首字母小写
                    instance = clazz.newInstance();
                    this.factoryBeanObjectCache.put(beanName, instance);
                }else if(clazz.isAnnotationPresent(GCService.class)){
                    GCService service = clazz.getAnnotation(GCService.class);
                    if(!"".equals(service.value())){
                        beanName = service.value();
                    }
                    instance = clazz.newInstance();
                    this.factoryBeanObjectCache.put(beanName, instance);
                    //3、根据类型注入实现类，投机取巧的方式
                    //getInterfaces()方法和Java的反射机制有关。它能够获得这个对象所实现的接口
                    for (Class<?> i : clazz.getInterfaces()) {
                        this.factoryBeanObjectCache.put(i.getName(),instance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
