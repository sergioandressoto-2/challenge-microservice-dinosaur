package com.challenge.microservice.application;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.port.in.CreateDinosaurUseCase;
import com.challenge.microservice.application.port.in.GetDinosaurUseCase;
import com.challenge.microservice.application.port.in.GetDinosaursUseCase;
import com.challenge.microservice.application.port.out.DinosaurRepositoryPort;
import com.challenge.microservice.domain.Dinosaur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DinosaurService implements CreateDinosaurUseCase, GetDinosaursUseCase, GetDinosaurUseCase {

    private static final Logger log = LoggerFactory.getLogger(DinosaurService.class);

    private final DinosaurRepositoryPort repositoryPort;

    public DinosaurService(DinosaurRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void createDinosaur(DinosaurRequest req) {
        log.info("Creating dinosaur: {}, {}", req.getName(), req.getDiscoveryDate());
        // TODO: validar nombre único (req. de negocio pendiente)
        Dinosaur dinosaur = new Dinosaur(
                req.getName(),
                req.getSpecies(),
                req.getDiscoveryDate(),
                req.getExtinctionDate()
        );
        repositoryPort.save(dinosaur);
    }

    @Override
    public List<DinosaurResponse> getDinosaurs() {
        log.info("Returning list of dinosaurs from db");
        return repositoryPort.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DinosaurResponse> getDinosaur(Long id) {
        log.info("Returning dinosaur from db, id: {}", id);
        return repositoryPort.findById(id)
                .map(this::toResponse);
    }

    private DinosaurResponse toResponse(Dinosaur dinosaur) {
        DinosaurResponse response = new DinosaurResponse();
        response.setId(dinosaur.getId());
        response.setName(dinosaur.getName());
        response.setSpecies(dinosaur.getSpecies());
        response.setDiscoveryDate(dinosaur.getDiscoveryDate());
        response.setExtinctionDate(dinosaur.getExtinctionDate());
        response.setStatus(dinosaur.getStatus().toString());
        return response;
    }
}
