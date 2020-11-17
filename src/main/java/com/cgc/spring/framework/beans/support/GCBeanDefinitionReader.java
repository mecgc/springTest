package com.cgc.spring.framework.beans.support;

import com.cgc.spring.framework.annotation.GCController;
import com.cgc.spring.framework.annotation.GCService;
import com.cgc.spring.framework.beans.config.GCBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Description: <br/>
 * date: 2020-11-13 22:00<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCBeanDefinitionReader {
    //保存扫描的结果
    private List<String> registryBeanClass =new ArrayList<String>();
    private Properties contextConfig=new Properties();

    public GCBeanDefinitionReader(String... configLocations) {
        doLoadConfig(configLocations[0]);
        //扫描配置文件中的配置的相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    private void doScanner(String scanPackage) {
        {
            //包传过来包下面的所有的类全部扫描进来的
            URL url = this.getClass().getClassLoader()
                    .getResource("/" + scanPackage.replaceAll("\\.","/"));
            File classPath = new File(url.getFile());

            for (File file : classPath.listFiles()) {
                if(file.isDirectory()){
                    doScanner(scanPackage + "." + file.getName());
                }else {
                    if(!file.getName().endsWith(".class")){ continue; }
                    String className = (scanPackage + "." + file.getName()).replace(".class","");
                    registryBeanClass.add(className);
                }
            }

        }
    }

    private void doLoadConfig(String configLocation) {
        InputStream fis = null;
        try {
            fis = this.getClass().getClassLoader().getResourceAsStream(configLocation);
            //1、读取配置文件
            contextConfig.load(fis);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(null != fis){fis.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<GCBeanDefinition> loadBaenDefinitions() {
        List<GCBeanDefinition> result = new ArrayList<GCBeanDefinition>();
        try {
            for (String className : registryBeanClass) {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){continue;}
                if(beanClass.isAnnotationPresent(GCService.class)||beanClass.isAnnotationPresent(GCController.class)) {
                    //保存类对应的ClassName（全类名）
                    //还有beanName
                    //1、默认是类名首字母小写
                    result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                    //2、自定义
               /* GCService service = beanClass.getAnnotation(GCService.class);
                if(!"".equals(service.value())){
                    beanName = service.value();
                }*/
                    //3、接口注入
                    for (Class<?> i : beanClass.getInterfaces()) {
                        result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private GCBeanDefinition doCreateBeanDefinition(String beanFactoryName, String beanClassName) {
        GCBeanDefinition beanDefinition = new GCBeanDefinition();
        beanDefinition.setFactorBeanyName(beanFactoryName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;
        return  String.valueOf(chars);
    }
}
