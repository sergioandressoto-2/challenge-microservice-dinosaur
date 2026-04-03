package com.challenge.microservice.adapters.in;

import com.challenge.microservice.application.port.in.StatusTransitionUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SchedulerAdapter.class);

    private final StatusTransitionUseCase statusTransitionUseCase;

    public SchedulerAdapter(StatusTransitionUseCase statusTransitionUseCase) {
        this.statusTransitionUseCase = statusTransitionUseCase;
    }

    @Scheduled(cron = "${dinosaur.scheduler.cron}")
    public void run() {
        log.info("Dinosaur status scheduler triggered");
        statusTransitionUseCase.processStatusTransitions();
    }
}
