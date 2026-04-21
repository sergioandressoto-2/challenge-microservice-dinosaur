package com.challenge.microservice.adapters.in;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.dto.PagedResponse;
import com.challenge.microservice.application.port.in.CreateDinosaurUseCase;
import com.challenge.microservice.application.port.in.DeleteDinosaurUseCase;
import com.challenge.microservice.application.port.in.ReadDinosaurUseCase;
import com.challenge.microservice.application.port.in.UpdateDinosaurUseCase;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @ApiResponse(responseCode = "422", description = "Unprocessable entity")
    })
    public ResponseEntity<DinosaurResponse> createDinosaur(@Valid @RequestBody DinosaurRequest dinosaurRequest) {
        DinosaurResponse created = createDinosaurUseCase.createDinosaur(dinosaurRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/dinosaur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "422", description = "Invalid status value")
    })
    public ResponseEntity<PagedResponse<DinosaurResponse>> returnDinosaurs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String species,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(readDinosaurUseCase.getDinosaurs(status, species, page, size));
    }

    @GetMapping("/dinosaur/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<DinosaurResponse> returnDinosaur(@PathVariable Long id) {
        return ResponseEntity.ok(readDinosaurUseCase.getDinosaur(id));
    }

    @PutMapping("/dinosaur/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Void> updateDinosaur(@PathVariable Long id, @Valid @RequestBody DinosaurRequest dinosaurRequest) {
        updateDinosaurUseCase.updateDinosaur(id, dinosaurRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/dinosaur/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Void> deleteDinosaur(@PathVariable Long id) {
        deleteDinosaurUseCase.deleteDinosaur(id);
        return ResponseEntity.noContent().build();
    }
}
