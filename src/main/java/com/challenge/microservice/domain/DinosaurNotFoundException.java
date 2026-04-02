package com.challenge.microservice.domain;

public class DinosaurNotFoundException extends RuntimeException {
    public DinosaurNotFoundException(Long id) {
        super("Dinosaur not found with id: " + id);
    }
}
