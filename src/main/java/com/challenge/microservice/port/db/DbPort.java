package com.challenge.microservice.port.db;

import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.application.dto.DinosaurResponse;

import java.util.List;
import java.util.Optional;

public interface DbPort {

    void create(Dinosaur dinosaur);
    void updateStatus(String id, String status);
    List<DinosaurResponse> returnDinosaurs();
    Optional<DinosaurResponse> getDinosaur(String id);
}

