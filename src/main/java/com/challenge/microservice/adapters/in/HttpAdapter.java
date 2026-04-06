package com.challenge.microservice.adapters.in;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.dto.ErrorResponse;
import com.challenge.microservice.application.port.in.CreateDinosaurUseCase;
import com.challenge.microservice.application.port.in.DeleteDinosaurUseCase;
import com.challenge.microservice.application.port.in.ReadDinosaurUseCase;
import com.challenge.microservice.application.port.in.UpdateDinosaurUseCase;
import com.challenge.microservice.domain.DinosaurNotFoundException;
import com.challenge.microservice.domain.DomainException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Gestión de Dinosaurios", description = "API para crear, leer, actualizar y borrar Dinosaurios")
public class HttpAdapter {

    private final CreateDinosaurUseCase createDinosaurUseCase;
    private final ReadDinosaurUseCase readDinosaurUseCase;
    private final UpdateDinosaurUseCase updateDinosaurUseCase;
    private final DeleteDinosaurUseCase deleteDinosaurUseCase;

    public HttpAdapter(CreateDinosaurUseCase createDinosaurUseCase,
                       ReadDinosaurUseCase readDinosaurUseCase,
                       UpdateDinosaurUseCase updateDinosaurUseCase,
                       DeleteDinosaurUseCase deleteDinosaurUseCase) {
        this.createDinosaurUseCase = createDinosaurUseCase;
        this.readDinosaurUseCase = readDinosaurUseCase;
        this.updateDinosaurUseCase = updateDinosaurUseCase;
        this.deleteDinosaurUseCase = deleteDinosaurUseCase;
    }

    @PostMapping("/dinosaur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "422", description = "Not Created, Unproccesable entity.")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucessfull")
    })
    public ResponseEntity<List<DinosaurResponse>> returnDinosaurs() {
        return ResponseEntity.ok(readDinosaurUseCase.getDinosaurs());
    }

    @GetMapping("/dinosaur/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucessfull"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<?> returnDinosaur(@PathVariable Long id) {
        return readDinosaurUseCase.getDinosaur(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Dinosaur not found with id: " + id)));
    }

    @PutMapping("/dinosaur/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucessfull"),
            @ApiResponse(responseCode = "422", description = "Not Created, Unproccesable entity."),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucessfull")
    })
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
