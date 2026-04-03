package com.challenge.microservice.adapters.out.messaging;

import com.challenge.microservice.application.dto.StatusNotificationMessage;
import com.challenge.microservice.application.port.out.NotificationPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQNotificationAdapter implements NotificationPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQNotificationAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notifyStatusChange(StatusNotificationMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                "notification.status",
                message
        );
    }
}
