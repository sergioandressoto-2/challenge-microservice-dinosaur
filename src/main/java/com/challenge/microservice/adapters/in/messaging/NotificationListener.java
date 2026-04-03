package com.challenge.microservice.adapters.in.messaging;

import com.challenge.microservice.adapters.out.messaging.RabbitMQConfig;
import com.challenge.microservice.application.dto.StatusNotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = "#{T(com.challenge.microservice.adapters.out.messaging.RabbitMQConfig).QUEUE}")
    public void onStatusNotification(StatusNotificationMessage message) {
        log.info("Notification received: dinosaurId={} newStatus={} timestamp={}",
                message.getDinosaurId(), message.getNewStatus(), message.getTimestamp());
    }
}
