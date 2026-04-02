package com.challenge.microservice.adapters.out.repository;

import com.challenge.microservice.adapters.out.model.DinosaurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbRepository extends JpaRepository<DinosaurEntity, Long> {
    boolean existsByName(String name);
}

