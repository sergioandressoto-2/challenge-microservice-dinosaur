package com.challenge.microservice.application;

import com.challenge.microservice.application.port.in.StatusTransitionUseCase;
import com.challenge.microservice.application.port.out.DinosaurRepositoryPort;
import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DinosaurSchedulerService implements StatusTransitionUseCase {

    private static final Logger log = LoggerFactory.getLogger(DinosaurSchedulerService.class);

    private final DinosaurRepositoryPort repositoryPort;

    public DinosaurSchedulerService(DinosaurRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    @Transactional
    public void processStatusTransitions() {
        Date now = new Date();
        Date threshold = new Date(now.getTime() + 24L * 60 * 60 * 1000);

        List<Dinosaur> toExtinct = repositoryPort.findNonExtinctWithExtinctionDateBefore(now);
        for (Dinosaur dinosaur : toExtinct) {
            log.info("Scheduler: transitioning id={} name='{}' to EXTINCT (extinctionDate reached)", dinosaur.getId(), dinosaur.getName());
            dinosaur.updateStatus(Status.EXTINCT);
            repositoryPort.save(dinosaur);
        }

        List<Dinosaur> toEndangered = repositoryPort.findAliveWithExtinctionDateBetween(now, threshold);
        for (Dinosaur dinosaur : toEndangered) {
            log.info("Scheduler: transitioning id={} name='{}' to ENDANGERED (extinction within 24h)", dinosaur.getId(), dinosaur.getName());
            dinosaur.updateStatus(Status.ENDANGERED);
            repositoryPort.save(dinosaur);
        }

        log.info("Scheduler: completed — {} to EXTINCT, {} to ENDANGERED", toExtinct.size(), toEndangered.size());
    }
}
