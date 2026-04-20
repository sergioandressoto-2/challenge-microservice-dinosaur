package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurResponse;

import java.util.List;

public interface ReadDinosaurUseCase {
    List<DinosaurResponse> getDinosaurs();
    DinosaurResponse getDinosaur(Long id);
}
