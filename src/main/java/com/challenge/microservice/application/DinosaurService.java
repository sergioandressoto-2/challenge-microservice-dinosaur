package com.challenge.microservice.application;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.dto.PagedResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    @Transactional
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
    @Transactional(readOnly = true)
    public PagedResponse<DinosaurResponse> getDinosaurs(String status, String species, int page, int size) {
        log.info("Returning dinosaurs — status={}, species={}, page={}, size={}", status, species, page, size);
        if (status != null) {
            try {
                Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new DomainException("Invalid status value: " + status);
            }
        }
        PagedResponse<Dinosaur> result = repositoryPort.findWithFilters(status, species, page, size);
        List<DinosaurResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .toList();
        return new PagedResponse<>(content, result.getPage(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public DinosaurResponse getDinosaur(Long id) {
        log.info("Returning dinosaur from db, id: {}", id);
        return repositoryPort.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new DinosaurNotFoundException(id));
    }

    @Override
    @Transactional
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
    @Transactional
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
