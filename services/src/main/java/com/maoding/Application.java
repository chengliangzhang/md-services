package com.maoding;

import com.maoding.coreUtils.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@Import({SpringUtils.class})
@EnableAspectJAutoProxy(exposeProxy = true)

@SpringBootApplication
@RestController
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.maoding"})
public class Application extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Runner runner = SpringUtils.getBean(Runner.class);
        runner.run(args);
    }
}
