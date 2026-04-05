package com.challenge.microservice.adapters.in.health;

import com.challenge.microservice.application.port.out.DinosaurRepositoryPort;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DinosaurRepositoryPort repositoryPort;

    public DatabaseHealthIndicator(DinosaurRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Health health() {
        try {
            repositoryPort.isConnected();
            return Health.up()
                    .withDetail("validation", "JPA count() OK")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
