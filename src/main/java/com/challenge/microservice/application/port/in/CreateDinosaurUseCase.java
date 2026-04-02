package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurRequest;

public interface CreateDinosaurUseCase {
    void createDinosaur(DinosaurRequest request);
}
