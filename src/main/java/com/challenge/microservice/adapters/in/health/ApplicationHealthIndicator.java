package com.challenge.microservice.adapters.in.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("application")
public class ApplicationHealthIndicator implements HealthIndicator {

    private final String applicationName;
    private final String schedulerCron;

    public ApplicationHealthIndicator(
            @Value("${spring.application.name}") String applicationName,
            @Value("${dinosaur.scheduler.cron}") String schedulerCron) {
        this.applicationName = applicationName;
        this.schedulerCron = schedulerCron;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("name", applicationName)
                .withDetail("schedulerCron", schedulerCron)
                .build();
    }
}
