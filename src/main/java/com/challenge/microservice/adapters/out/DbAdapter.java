package com.challenge.microservice.adapters.out;

import com.challenge.microservice.adapters.out.model.DinosaurEntity;
import com.challenge.microservice.adapters.out.repository.DbRepository;
import com.challenge.microservice.application.port.out.DinosaurRepositoryPort;
import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.domain.Status;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DbAdapter implements DinosaurRepositoryPort {

    private final DbRepository dbRepository;

    public DbAdapter(DbRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    @Override
    public void save(Dinosaur dinosaur) {
        dbRepository.save(mapDomainToEntity(dinosaur));
    }

    @Override
    public List<Dinosaur> findAll() {
        return dbRepository.findAll()
                .stream()
                .map(this::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Dinosaur> findById(Long id) {
        return dbRepository.findById(id)
                .map(this::mapEntityToDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return dbRepository.existsByName(name);
    }

    @Override
    public void deleteById(Long id) {
        dbRepository.deleteById(id);
    }

    private DinosaurEntity mapDomainToEntity(Dinosaur dinosaur) {
        DinosaurEntity entity = new DinosaurEntity();
        entity.setId(dinosaur.getId());
        entity.setName(dinosaur.getName());
        entity.setSpecies(dinosaur.getSpecies());
        entity.setDiscoveryDate(dinosaur.getDiscoveryDate());
        entity.setExtinctionDate(dinosaur.getExtinctionDate());
        entity.setStatus(dinosaur.getStatus().toString());
        return entity;
    }

    private Dinosaur mapEntityToDomain(DinosaurEntity entity) {
        return new Dinosaur(
                entity.getId(),
                entity.getName(),
                entity.getSpecies(),
                entity.getDiscoveryDate(),
                entity.getExtinctionDate(),
                Status.valueOf(entity.getStatus())
        );
    }
}
