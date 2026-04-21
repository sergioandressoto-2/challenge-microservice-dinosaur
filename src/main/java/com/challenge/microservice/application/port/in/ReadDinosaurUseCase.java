package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.dto.PagedResponse;

public interface ReadDinosaurUseCase {
    PagedResponse<DinosaurResponse> getDinosaurs(String status, String species, int page, int size);
    DinosaurResponse getDinosaur(Long id);
}
