package com.challenge.microservice.application.dto;

import java.time.LocalDateTime;

public class StatusNotificationMessage {

    private Long dinosaurId;
    private String newStatus;
    private LocalDateTime timestamp;

    public StatusNotificationMessage() {}

    public StatusNotificationMessage(Long dinosaurId, String newStatus, LocalDateTime timestamp) {
        this.dinosaurId = dinosaurId;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
    }

    public Long getDinosaurId() { return dinosaurId; }
    public String getNewStatus() { return newStatus; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
