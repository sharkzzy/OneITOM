package org.iplatform.example.ui;

import org.iplatform.microservices.ui.IPlatformUIApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableOAuth2Client
@EnableDiscoveryClient
@EnableEurekaClient
@EnableResourceServer
@EnableFeignClients
@EnableHystrix
@Configuration
@EnableAspectJAutoProxy
@EnableJms
@EnableCaching
@RestController
@ComponentScan({"org.iplatform.microservices", "org.iplatform.example.ui"})
public class UIApplication extends IPlatformUIApplication {

    private static final Logger LOG = LoggerFactory.getLogger(UIApplication.class);

    public static void main(String[] args) throws Exception {
        try {
            run(UIApplication.class, args);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
}
