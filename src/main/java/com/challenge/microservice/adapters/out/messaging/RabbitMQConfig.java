package com.challenge.microservice.adapters.out.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "NotificationStatus";
    public static final String QUEUE = "notification.status.queue";
    public static final String ROUTING_KEY = "notification.#";

    @Bean
    public Queue notificationStatusQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange notificationStatusExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Binding notificationStatusBinding(Queue notificationStatusQueue,
                                             TopicExchange notificationStatusExchange) {
        return BindingBuilder
                .bind(notificationStatusQueue)
                .to(notificationStatusExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
