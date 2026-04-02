package com.challenge.microservice.port.http;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface HttpPort {

    @PostMapping("/dinosaur")
    ResponseEntity<String> createDinosaur(@RequestBody DinosaurRequest dinosaurRequest);

    @GetMapping("/dinosaur")
    ResponseEntity<List<DinosaurResponse>> returnDinosaurs();

    @GetMapping("dinosaur/{id}")
    ResponseEntity<DinosaurResponse> returnDinosaur(@PathVariable String id);

    @PutMapping("dinosaur/{id}")
    String updateDinosaur(@PathVariable String id, @RequestBody DinosaurRequest dinosaurRequest);

    @DeleteMapping("dinosaur/{id}")
    String deleteDinosaur(@PathVariable String id);
}
