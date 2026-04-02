package com.challenge.microservice.application.port.in;

import com.challenge.microservice.application.dto.DinosaurResponse;

import java.util.List;

public interface GetDinosaursUseCase {
    List<DinosaurResponse> getDinosaurs();
}
