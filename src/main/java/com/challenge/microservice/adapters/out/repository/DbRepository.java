package com.challenge.microservice.adapters.out.repository;

import com.challenge.microservice.adapters.out.model.DinosaurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DbRepository extends JpaRepository<DinosaurEntity, Long> {

    boolean existsByName(String name);

    List<DinosaurEntity> findByStatusNotAndExtinctionDateLessThanEqual(String status, Date date);

    @Query("SELECT d FROM DinosaurEntity d WHERE d.status = :status AND d.extinctionDate > :now AND d.extinctionDate <= :threshold")
    List<DinosaurEntity> findAliveWithExtinctionDateBetween(
            @Param("status") String status,
            @Param("now") Date now,
            @Param("threshold") Date threshold);
}
