package com.challenge.microservice.adapters.in;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.port.in.CreateDinosaurUseCase;
import com.challenge.microservice.application.port.in.GetDinosaurUseCase;
import com.challenge.microservice.application.port.in.GetDinosaursUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HttpAdapter {

    private final CreateDinosaurUseCase createDinosaurUseCase;
    private final GetDinosaursUseCase getDinosaursUseCase;
    private final GetDinosaurUseCase getDinosaurUseCase;

    public HttpAdapter(CreateDinosaurUseCase createDinosaurUseCase,
                       GetDinosaursUseCase getDinosaursUseCase,
                       GetDinosaurUseCase getDinosaurUseCase) {
        this.createDinosaurUseCase = createDinosaurUseCase;
        this.getDinosaursUseCase = getDinosaursUseCase;
        this.getDinosaurUseCase = getDinosaurUseCase;
    }

    @PostMapping("/dinosaur")
    public ResponseEntity<String> createDinosaur(@RequestBody DinosaurRequest dinosaurRequest) {
        createDinosaurUseCase.createDinosaur(dinosaurRequest);
        return new ResponseEntity<>("Dinosaur Saved Successfully", HttpStatus.CREATED);
    }

    @GetMapping("/dinosaur")
    public ResponseEntity<List<DinosaurResponse>> returnDinosaurs() {
        return ResponseEntity.ok(getDinosaursUseCase.getDinosaurs());
    }

    @GetMapping("/dinosaur/{id}")
    public ResponseEntity<DinosaurResponse> returnDinosaur(@PathVariable Long id) {
        return getDinosaurUseCase.getDinosaur(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/dinosaur/{id}")
    public String updateDinosaur(@PathVariable Long id, @RequestBody DinosaurRequest dinosaurRequest) {
        return "";
    }

    @DeleteMapping("/dinosaur/{id}")
    public String deleteDinosaur(@PathVariable Long id) {
        return "";
    }
}
