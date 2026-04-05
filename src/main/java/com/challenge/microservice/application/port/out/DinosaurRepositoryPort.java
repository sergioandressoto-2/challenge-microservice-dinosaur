package com.challenge.microservice.application.port.out;

import com.challenge.microservice.domain.Dinosaur;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DinosaurRepositoryPort {
    Dinosaur save(Dinosaur dinosaur);
    List<Dinosaur> findAll();
    Optional<Dinosaur> findById(Long id);
    boolean existsByName(String name);
    void deleteById(Long id);
    List<Dinosaur> findNonExtinctWithExtinctionDateBefore(Date date);
    List<Dinosaur> findAliveWithExtinctionDateBetween(Date from, Date to);
    boolean isConnected();
}
