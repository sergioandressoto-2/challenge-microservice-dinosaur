package com.challenge.microservice.application.port.out;

import com.challenge.microservice.application.dto.StatusNotificationMessage;

public interface NotificationPort {
    void notifyStatusChange(StatusNotificationMessage message);
}
