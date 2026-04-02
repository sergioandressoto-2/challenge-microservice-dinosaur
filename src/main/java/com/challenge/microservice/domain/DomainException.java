package com.challenge.microservice.domain;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
