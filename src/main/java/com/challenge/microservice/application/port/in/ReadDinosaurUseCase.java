package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurResponse;

import java.util.List;
import java.util.Optional;

public interface ReadDinosaurUseCase {
    List<DinosaurResponse> getDinosaurs();
    Optional<DinosaurResponse> getDinosaur(Long id);
}
