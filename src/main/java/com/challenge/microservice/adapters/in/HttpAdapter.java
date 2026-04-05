package com.challenge.microservice.adapters.in;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.dto.ErrorResponse;
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
    public ResponseEntity<?> createDinosaur(@RequestBody DinosaurRequest dinosaurRequest) {
        try {
            DinosaurResponse created = createDinosaurUseCase.createDinosaur(dinosaurRequest);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage()));
        }
    }

    @GetMapping("/dinosaur")
    public ResponseEntity<List<DinosaurResponse>> returnDinosaurs() {
        return ResponseEntity.ok(getDinosaursUseCase.getDinosaurs());
    }

    @GetMapping("/dinosaur/{id}")
    public ResponseEntity<?> returnDinosaur(@PathVariable Long id) {
        return getDinosaurUseCase.getDinosaur(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Dinosaur not found with id: " + id)));
    }

    @PutMapping("/dinosaur/{id}")
    public ResponseEntity<?> updateDinosaur(@PathVariable Long id, @RequestBody DinosaurRequest dinosaurRequest) {
        try {
            updateDinosaurUseCase.updateDinosaur(id, dinosaurRequest);
            return ResponseEntity.ok().build();
        } catch (DinosaurNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/dinosaur/{id}")
    public ResponseEntity<?> deleteDinosaur(@PathVariable Long id) {
        try {
            deleteDinosaurUseCase.deleteDinosaur(id);
            return ResponseEntity.noContent().build();
        } catch (DinosaurNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }
}
