package com.challenge.microservice.application;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.dto.StatusNotificationMessage;
import com.challenge.microservice.application.port.in.CreateDinosaurUseCase;
import com.challenge.microservice.application.port.in.DeleteDinosaurUseCase;
import com.challenge.microservice.application.port.in.ReadDinosaurUseCase;
import com.challenge.microservice.application.port.in.UpdateDinosaurUseCase;
import com.challenge.microservice.application.port.out.DinosaurRepositoryPort;
import com.challenge.microservice.application.port.out.NotificationPort;
import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.domain.DinosaurNotFoundException;
import com.challenge.microservice.domain.DomainException;
import com.challenge.microservice.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DinosaurService implements CreateDinosaurUseCase, ReadDinosaurUseCase, UpdateDinosaurUseCase, DeleteDinosaurUseCase {

    private static final Logger log = LoggerFactory.getLogger(DinosaurService.class);

    private final DinosaurRepositoryPort repositoryPort;
    private final NotificationPort notificationPort;

    public DinosaurService(DinosaurRepositoryPort repositoryPort, NotificationPort notificationPort) {
        this.repositoryPort = repositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public DinosaurResponse createDinosaur(DinosaurRequest req) {
        log.info("Creating dinosaur: {}, {}", req.getName(), req.getDiscoveryDate());
        if (repositoryPort.existsByName(req.getName())) {
            throw new DomainException("Dinosaur name already exists: " + req.getName());
        }
        Dinosaur dinosaur = new Dinosaur(
                req.getName(),
                req.getSpecies(),
                req.getDiscoveryDate(),
                req.getExtinctionDate(),
                req.getStatus()
        );
        Dinosaur saved = repositoryPort.save(dinosaur);
        return toResponse(saved);
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
    public DinosaurResponse getDinosaur(Long id) {
        log.info("Returning dinosaur from db, id: {}", id);
        return repositoryPort.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new DinosaurNotFoundException(id));
    }

    @Override
    public void updateDinosaur(Long id, DinosaurRequest req) {
        log.info("Updating dinosaur, id: {}", id);
        Dinosaur dinosaur = repositoryPort.findById(id)
                .orElseThrow(() -> new DinosaurNotFoundException(id));
        Status previousStatus = dinosaur.getStatus();
        Status newStatus;
        try {
            newStatus = Status.valueOf(req.getStatus());
        } catch  (IllegalArgumentException e) {
            throw new DomainException("Invalid status value: " + req.getStatus());
           }
        dinosaur.updateDetails(req.getName(), req.getSpecies(), req.getDiscoveryDate(),
                req.getExtinctionDate(), newStatus);
        repositoryPort.save(dinosaur);

        if (previousStatus != newStatus) {
            notificationPort.notifyStatusChange(new StatusNotificationMessage(id, newStatus.name(), LocalDateTime.now()));
        }
    }

    @Override
    public void deleteDinosaur(Long id) {
        log.info("Deleting dinosaur, id: {}", id);
        repositoryPort.findById(id)
                .orElseThrow(() -> new DinosaurNotFoundException(id));
        repositoryPort.deleteById(id);
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
