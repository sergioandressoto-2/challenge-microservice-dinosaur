package com.challenge.microservice.application;

import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.application.port.out.DinosaurRepositoryPort;
import com.challenge.microservice.application.port.out.NotificationPort;
import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.domain.DinosaurNotFoundException;
import com.challenge.microservice.domain.DomainException;
import com.challenge.microservice.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DinosaurServiceTest {

    @Mock
    private DinosaurRepositoryPort repositoryPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private DinosaurService service;

    private Date past;
    private Date future;

    @BeforeEach
    void setUp() {
        past   = new Date(System.currentTimeMillis() - 86_400_000L);
        future = new Date(System.currentTimeMillis() + 86_400_000L);
    }

    private DinosaurRequest buildRequest(String name, String species, Date discovery, Date extinction, String status) {
        DinosaurRequest req = new DinosaurRequest();
        req.setName(name);
        req.setSpecies(species);
        req.setDiscoveryDate(discovery);
        req.setExtinctionDate(extinction);
        req.setStatus(status);
        return req;
    }

    private Dinosaur buildDinosaur(Long id, Status status) {
        return new Dinosaur(id, "T-Rex", "Tyrannosaurus", past, future, status);
    }

    // -------------------------------------------------------------------------
    // createDinosaur
    // -------------------------------------------------------------------------

    @Test
    void createDinosaur_savesWhenNameIsUnique() {
        Dinosaur saved = new Dinosaur(1L, "T-Rex", "Tyrannosaurus", past, future, Status.ALIVE);
        when(repositoryPort.existsByName("T-Rex")).thenReturn(false);
        when(repositoryPort.save(any(Dinosaur.class))).thenReturn(saved);
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", past, future, "ALIVE");

        DinosaurResponse response = service.createDinosaur(req);

        verify(repositoryPort).save(any(Dinosaur.class));
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("T-Rex");
        assertThat(response.getStatus()).isEqualTo("ALIVE");
    }

    @Test
    void createDinosaur_throwsDomainException_whenNameAlreadyExists() {
        when(repositoryPort.existsByName("T-Rex")).thenReturn(true);
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", past, future, "ALIVE");

        assertThatThrownBy(() -> service.createDinosaur(req))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("T-Rex");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void createDinosaur_throwsDomainException_whenDatesAreInvalid() {
        when(repositoryPort.existsByName(any())).thenReturn(false);
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", future, past, "ALIVE");

        assertThatThrownBy(() -> service.createDinosaur(req))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("discoveryDate must be before extinction date");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void createDinosaur_checksNameBeforeSaving() {
        when(repositoryPort.existsByName("T-Rex")).thenReturn(true);
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", past, future, "ALIVE");

        assertThatThrownBy(() -> service.createDinosaur(req))
                .isInstanceOf(DomainException.class);

        verify(repositoryPort).existsByName("T-Rex");
        verify(repositoryPort, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // getDinosaurs
    // -------------------------------------------------------------------------

    @Test
    void getDinosaurs_returnsEmptyList_whenNoDinosaurs() {
        when(repositoryPort.findAll()).thenReturn(List.of());

        assertThat(service.getDinosaurs()).isEmpty();
    }

    @Test
    void getDinosaurs_returnsMappedResponse_forEachDinosaur() {
        Dinosaur d1 = buildDinosaur(1L, Status.ALIVE);
        Dinosaur d2 = buildDinosaur(2L, Status.ENDANGERED);
        when(repositoryPort.findAll()).thenReturn(List.of(d1, d2));

        List<DinosaurResponse> result = service.getDinosaurs();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo("ALIVE");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getStatus()).isEqualTo("ENDANGERED");
    }

    @Test
    void getDinosaurs_mapsAllFieldsCorrectly() {
        Dinosaur d = new Dinosaur(5L, "Raptor", "Velociraptor", past, future, Status.ALIVE);
        when(repositoryPort.findAll()).thenReturn(List.of(d));

        DinosaurResponse response = service.getDinosaurs().get(0);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getName()).isEqualTo("Raptor");
        assertThat(response.getSpecies()).isEqualTo("Velociraptor");
        assertThat(response.getDiscoveryDate()).isEqualTo(past);
        assertThat(response.getExtinctionDate()).isEqualTo(future);
        assertThat(response.getStatus()).isEqualTo("ALIVE");
    }

    // -------------------------------------------------------------------------
    // getDinosaur
    // -------------------------------------------------------------------------

    @Test
    void getDinosaur_returnsResponse_whenFound() {
        Dinosaur d = buildDinosaur(1L, Status.ALIVE);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(d));

        Optional<DinosaurResponse> result = service.getDinosaur(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("T-Rex");
    }

    @Test
    void getDinosaur_returnsEmptyOptional_whenNotFound() {
        when(repositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.getDinosaur(99L)).isEmpty();
    }

    @Test
    void getDinosaur_mapsAllFieldsCorrectly() {
        Dinosaur d = new Dinosaur(3L, "Stego", "Stegosaurus", past, future, Status.ENDANGERED);
        when(repositoryPort.findById(3L)).thenReturn(Optional.of(d));

        DinosaurResponse response = service.getDinosaur(3L).orElseThrow();

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("Stego");
        assertThat(response.getSpecies()).isEqualTo("Stegosaurus");
        assertThat(response.getDiscoveryDate()).isEqualTo(past);
        assertThat(response.getExtinctionDate()).isEqualTo(future);
        assertThat(response.getStatus()).isEqualTo("ENDANGERED");
    }

    // -------------------------------------------------------------------------
    // updateDinosaur
    // -------------------------------------------------------------------------

    @Test
    void updateDinosaur_updatesAndSaves_whenDinosaurExists() {
        Dinosaur existing = buildDinosaur(1L, Status.ALIVE);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(existing));
        DinosaurRequest req = buildRequest("New Name", "New Species", past, future, "ENDANGERED");

        service.updateDinosaur(1L, req);

        verify(repositoryPort).save(existing);
        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getSpecies()).isEqualTo("New Species");
        assertThat(existing.getStatus()).isEqualTo(Status.ENDANGERED);
    }

    @Test
    void updateDinosaur_throwsDinosaurNotFoundException_whenNotFound() {
        when(repositoryPort.findById(99L)).thenReturn(Optional.empty());
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", past, future, "ALIVE");

        assertThatThrownBy(() -> service.updateDinosaur(99L, req))
                .isInstanceOf(DinosaurNotFoundException.class)
                .hasMessageContaining("99");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void updateDinosaur_throwsDomainException_whenDinosaurIsExtinct() {
        Dinosaur extinct = buildDinosaur(1L, Status.EXTINCT);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(extinct));
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", past, future, "ALIVE");

        assertThatThrownBy(() -> service.updateDinosaur(1L, req))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Cannot update an extinct dinosaur");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void updateDinosaur_throwsDomainException_whenInvalidDates() {
        Dinosaur existing = buildDinosaur(1L, Status.ALIVE);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(existing));
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", future, past, "ALIVE");

        assertThatThrownBy(() -> service.updateDinosaur(1L, req))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("discoveryDate must be before extinction date");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void updateDinosaur_canTransitionToExtinct() {
        Dinosaur existing = buildDinosaur(1L, Status.ENDANGERED);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(existing));
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", past, future, "EXTINCT");

        service.updateDinosaur(1L, req);

        assertThat(existing.getStatus()).isEqualTo(Status.EXTINCT);
        verify(repositoryPort).save(existing);
    }

    // -------------------------------------------------------------------------
    // deleteDinosaur
    // -------------------------------------------------------------------------

    @Test
    void deleteDinosaur_deletesWhenFound() {
        Dinosaur d = buildDinosaur(1L, Status.ALIVE);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(d));

        service.deleteDinosaur(1L);

        verify(repositoryPort).deleteById(1L);
    }

    @Test
    void deleteDinosaur_throwsDinosaurNotFoundException_whenNotFound() {
        when(repositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteDinosaur(99L))
                .isInstanceOf(DinosaurNotFoundException.class)
                .hasMessageContaining("99");

        verify(repositoryPort, never()).deleteById(any());
    }

    @Test
    void deleteDinosaur_canDeleteExtinctDinosaur() {
        Dinosaur d = buildDinosaur(1L, Status.EXTINCT);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(d));

        assertThatCode(() -> service.deleteDinosaur(1L))
                .doesNotThrowAnyException();

        verify(repositoryPort).deleteById(1L);
    }
}
