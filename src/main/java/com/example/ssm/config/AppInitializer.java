package com.example.ssm.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Replaces the old web.xml servlet registration style with Java configuration.
 *
 * <p>When Tomcat starts this WAR, Spring discovers this class and creates the
 * DispatcherServlet, which is the front controller for all Spring MVC requests.</p>
 */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        // RootConfig contains application-wide beans: DataSource, MyBatis, transactions, services.
        return new Class<?>[]{RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // WebConfig contains MVC-only beans: controllers, JSON converters, request mapping.
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        // "/" means Spring MVC receives requests under this web application's context path.
        return new String[]{"/"};
    }
}
