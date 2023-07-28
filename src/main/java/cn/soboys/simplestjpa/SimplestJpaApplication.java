package cn.soboys.simplestjpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class SimplestJpaApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(SimplestJpaApplication.class, args);
        listBeans(ctx);
    }

    public static void listBeans(ApplicationContext ctx) {

        log.info("bean总数:{}", ctx.getBeanDefinitionCount());
        String[] allBeanNames = ctx.getBeanDefinitionNames();
        for (String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}
