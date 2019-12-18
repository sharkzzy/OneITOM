package org.iplatform.example.service.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @author liangruijia
 */
@Configuration
public class ActiveMqConfig {
    @Bean
    public Queue Q_MS_TEST() {
        return new ActiveMQQueue("Q_MS_TEST");
    }

    @Bean
    public Topic T_MS_TEST() {
        return new ActiveMQTopic("T_MS_TEST");
    }
}
