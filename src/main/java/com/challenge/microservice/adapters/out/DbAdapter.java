package com.challenge.microservice.adapters.out;

import com.challenge.microservice.adapters.out.model.DinosaurEntity;
import com.challenge.microservice.adapters.out.repository.DbRepository;
import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.port.db.DbPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DbAdapter implements DbPort{

    @Autowired
    private DbRepository dbRepository;

    @Override
    public void create(Dinosaur dinosaur) {
        DinosaurEntity entity = mapDinosaurToDinosaurEntity(dinosaur);
        dbRepository.save(entity);
    }

    @Override
    public void updateStatus(String id, String status) {

    }

    @Override
    public List<DinosaurResponse> returnDinosaurs() {
        return dbRepository.findAll()
                .stream()
                .map(this::mapDinosaurEntityToDinosaurResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DinosaurResponse> getDinosaur(String id) {
        return dbRepository.findById(id)
                .map(this::mapDinosaurEntityToDinosaurResponse);

    }

    private DinosaurEntity mapDinosaurToDinosaurEntity(Dinosaur dinosaurReq){
        DinosaurEntity dinosaurEntity = new DinosaurEntity();
        dinosaurEntity.setName(dinosaurReq.getName());
        dinosaurEntity.setSpecies(dinosaurReq.getSpecies());
        dinosaurEntity.setDiscoveryDate(dinosaurReq.getDiscoveryDate());
        dinosaurEntity.setExtinctionDate(dinosaurReq.getExtinctionDate());
        dinosaurEntity.setStatus(dinosaurReq.getStatus());
        return dinosaurEntity;
    }

    private DinosaurResponse mapDinosaurEntityToDinosaurResponse(DinosaurEntity dinosaurEntity){
        DinosaurResponse dinosaurResponse = new DinosaurResponse();
        dinosaurResponse.setName(dinosaurEntity.getName());
        dinosaurResponse.setSpecies(dinosaurEntity.getSpecies());
        dinosaurResponse.setDiscoveryDate(dinosaurEntity.getDiscoveryDate());
        dinosaurResponse.setExtinctionDate(dinosaurEntity.getExtinctionDate());
        dinosaurResponse.setStatus(dinosaurEntity.getStatus());
        dinosaurResponse.setId(dinosaurEntity.getId());
        return dinosaurResponse;
    }
}
