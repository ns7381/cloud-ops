package com.cloud.ops.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@RestController
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Value("classpath:/webapp/index.html")
//    @Value("classpath:META-INF/resources/webjars/ui/index.html")
    private Resource indexHtml;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter("UTF-8", true);
        return filter;
    }

    @Bean
    public CommonsMultipartResolver filterMultipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    @Order(0)
    public MultipartFilter multipartFilter() {
        return new MultipartFilter();
    }
    /*@Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }*/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/app/**").addResourceLocations("classpath:META-INF/resources/webjars/ui/app/");
//        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:META-INF/resources/webjars/ui/assets/");
//        registry.addResourceHandler("/yfjs-lib/**").addResourceLocations("classpath:META-INF/resources/webjars/ui/yfjs-lib/");
    }

    @Bean
    public ServletRegistrationBean apiV1ServletBean(WebApplicationContext wac) {
        DispatcherServlet servlet = new DispatcherServlet(wac);
        ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/v1/*");
        bean.setName("api-v1");
        return bean;
    }

    @RequestMapping("/")
    public Object index() {
        return ResponseEntity.ok().body(indexHtml);
    }
}
