package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;

public interface CreateDinosaurUseCase {
    DinosaurResponse createDinosaur(DinosaurRequest request);
}
