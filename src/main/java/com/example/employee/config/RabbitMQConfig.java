package com.example.employee.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  private final CachingConnectionFactory cachingConnectionFactory;

  public RabbitMQConfig(CachingConnectionFactory cachingConnectionFactory) {
    this.cachingConnectionFactory = cachingConnectionFactory;
  }
  @Bean
  public Queue createEmployeeUpdatesQueue() {
    return new Queue("q.employee-updates");
  }

  @Bean
  public Queue createEmployeeEventsQueue() {
    return new Queue("q.employee-events");
  }

  @Bean
  public Jackson2JsonMessageConverter converter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter) {
    RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
    template.setMessageConverter(converter);
    return template;
  }
}
