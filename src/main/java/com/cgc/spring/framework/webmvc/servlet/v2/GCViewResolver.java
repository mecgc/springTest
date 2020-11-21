package com.cgc.spring.framework.webmvc.servlet.v2;

import java.io.File;

/**
 * Description: <br/>
 * date: 2020-11-20 21:45<br/>
 *
 * @author chenguangchuan<br />
 * @since JDK 1.8
 */
public class GCViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private File tempateRootDir;
    public GCViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        tempateRootDir = new File(templateRootPath);
    }

    public GCView resolveViewName(String viewName) {
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((tempateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new GCView(templateFile);
    }
}
