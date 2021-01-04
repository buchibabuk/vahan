/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kartikey Singh
 */
@Service
public class SpringContext implements ApplicationContextAware {

    @Autowired
    static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getAppContext() {
        return context;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

//    @Bean
//    public WebClient.Builder getWebClientBuilder() {
//        System.out.println("Inside getWebClientBuilder()");
//        return WebClient.builder();
//    }
}
