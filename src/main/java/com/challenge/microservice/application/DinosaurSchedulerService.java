package com.challenge.microservice.application;

import com.challenge.microservice.application.dto.StatusNotificationMessage;
import com.challenge.microservice.application.port.in.StatusTransitionUseCase;
import com.challenge.microservice.application.port.out.DinosaurRepositoryPort;
import com.challenge.microservice.application.port.out.NotificationPort;
import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class DinosaurSchedulerService implements StatusTransitionUseCase {

    private static final Logger log = LoggerFactory.getLogger(DinosaurSchedulerService.class);

    private final DinosaurRepositoryPort repositoryPort;
    private final NotificationPort notificationPort;

    public DinosaurSchedulerService(DinosaurRepositoryPort repositoryPort, NotificationPort notificationPort) {
        this.repositoryPort = repositoryPort;
        this.notificationPort = notificationPort;
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
            notificationPort.notifyStatusChange(new StatusNotificationMessage(dinosaur.getId(), Status.EXTINCT.name(), LocalDateTime.now()));
        }

        List<Dinosaur> toEndangered = repositoryPort.findAliveWithExtinctionDateBetween(now, threshold);
        for (Dinosaur dinosaur : toEndangered) {
            log.info("Scheduler: transitioning id={} name='{}' to ENDANGERED (extinction within 24h)", dinosaur.getId(), dinosaur.getName());
            dinosaur.updateStatus(Status.ENDANGERED);
            repositoryPort.save(dinosaur);
            notificationPort.notifyStatusChange(new StatusNotificationMessage(dinosaur.getId(), Status.ENDANGERED.name(), LocalDateTime.now()));
        }

        log.info("Scheduler: completed — {} to EXTINCT, {} to ENDANGERED", toExtinct.size(), toEndangered.size());
    }
}
