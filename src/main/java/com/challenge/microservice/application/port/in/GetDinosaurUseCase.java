package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurResponse;

import java.util.Optional;

public interface GetDinosaurUseCase {
    Optional<DinosaurResponse> getDinosaur(Long id);
}
