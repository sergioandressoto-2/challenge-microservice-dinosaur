package com.challenge.microservice.adapters.out.repository;

import com.challenge.microservice.adapters.out.model.DinosaurEntity;
import org.springframework.data.jpa.domain.Specification;

public class DinosaurSpecification {

    private DinosaurSpecification() {}

    public static Specification<DinosaurEntity> withFilters(String status, String species) {
        return Specification
                .where(hasStatus(status))
                .and(hasSpecies(species));
    }

    private static Specification<DinosaurEntity> hasStatus(String status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<DinosaurEntity> hasSpecies(String species) {
        return (root, query, cb) ->
                species == null ? null : cb.equal(root.get("species"), species);
    }
}
