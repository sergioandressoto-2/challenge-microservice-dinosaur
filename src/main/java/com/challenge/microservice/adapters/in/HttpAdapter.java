package com.challenge.microservice.adapters.in;

import com.challenge.microservice.application.DinosaurService;
import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.port.http.HttpPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class HttpAdapter implements HttpPort {

    @Autowired
    private DinosaurService service;

    @Override
    public ResponseEntity<String> createDinosaur(DinosaurRequest dinosaurRequest) {

        service.createDinosaur(dinosaurRequest);

        return new ResponseEntity<>("Dinosaur Saved Sucessfull", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<DinosaurResponse>> returnDinosaurs() {
        return ResponseEntity.ok(service.returnDinosaurs());
    }

    @Override
    public ResponseEntity<DinosaurResponse> returnDinosaur(String id) {

        return service.returnDinosaur(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public String updateDinosaur(String id, DinosaurRequest dinosaurRequest) {
        return "";
    }

    @Override
    public String deleteDinosaur(String id) {
        return "";
    }
}
