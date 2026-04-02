package com.challenge.microservice.adapters.in;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.port.in.CreateDinosaurUseCase;
import com.challenge.microservice.application.port.in.DeleteDinosaurUseCase;
import com.challenge.microservice.application.port.in.GetDinosaurUseCase;
import com.challenge.microservice.application.port.in.GetDinosaursUseCase;
import com.challenge.microservice.application.port.in.UpdateDinosaurUseCase;
import com.challenge.microservice.domain.DinosaurNotFoundException;
import com.challenge.microservice.domain.DomainException;
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
    private final UpdateDinosaurUseCase updateDinosaurUseCase;
    private final DeleteDinosaurUseCase deleteDinosaurUseCase;

    public HttpAdapter(CreateDinosaurUseCase createDinosaurUseCase,
                       GetDinosaursUseCase getDinosaursUseCase,
                       GetDinosaurUseCase getDinosaurUseCase,
                       UpdateDinosaurUseCase updateDinosaurUseCase,
                       DeleteDinosaurUseCase deleteDinosaurUseCase) {
        this.createDinosaurUseCase = createDinosaurUseCase;
        this.getDinosaursUseCase = getDinosaursUseCase;
        this.getDinosaurUseCase = getDinosaurUseCase;
        this.updateDinosaurUseCase = updateDinosaurUseCase;
        this.deleteDinosaurUseCase = deleteDinosaurUseCase;
    }

    @PostMapping("/dinosaur")
    public ResponseEntity<String> createDinosaur(@RequestBody DinosaurRequest dinosaurRequest) {
        try {
            createDinosaurUseCase.createDinosaur(dinosaurRequest);
            return new ResponseEntity<>("Dinosaur Saved Successfully", HttpStatus.CREATED);
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
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
    public ResponseEntity<String> updateDinosaur(@PathVariable Long id, @RequestBody DinosaurRequest dinosaurRequest) {
        try {
            updateDinosaurUseCase.updateDinosaur(id, dinosaurRequest);
            return ResponseEntity.ok("Dinosaur Updated Successfully");
        } catch (DinosaurNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @DeleteMapping("/dinosaur/{id}")
    public ResponseEntity<String> deleteDinosaur(@PathVariable Long id) {
        try {
            deleteDinosaurUseCase.deleteDinosaur(id);
            return ResponseEntity.ok("Dinosaur Deleted Successfully");
        } catch (DinosaurNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
