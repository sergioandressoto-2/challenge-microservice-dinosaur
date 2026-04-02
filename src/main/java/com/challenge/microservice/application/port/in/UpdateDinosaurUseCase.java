package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurRequest;

public interface UpdateDinosaurUseCase {
    void updateDinosaur(Long id, DinosaurRequest request);
}
