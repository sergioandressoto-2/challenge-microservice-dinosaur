package com.challenge.microservice.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class DinosaurTest {

    private Date past()   { return new Date(System.currentTimeMillis() - 86_400_000L); }
    private Date future() { return new Date(System.currentTimeMillis() + 86_400_000L); }

    // -------------------------------------------------------------------------
    // Constructor — nuevo dinosaurio
    // -------------------------------------------------------------------------

    @Test
    void constructor_setsAllFields_andStatusIsAlive() {
        Date discovery = past();
        Date extinction = future();

        Dinosaur d = new Dinosaur("T-Rex", "Tyrannosaurus", discovery, extinction);

        assertThat(d.getId()).isNull();
        assertThat(d.getName()).isEqualTo("T-Rex");
        assertThat(d.getSpecies()).isEqualTo("Tyrannosaurus");
        assertThat(d.getDiscoveryDate()).isEqualTo(discovery);
        assertThat(d.getExtinctionDate()).isEqualTo(extinction);
        assertThat(d.getStatus()).isEqualTo(Status.ALIVE);
    }

    @Test
    void constructor_throwsDomainException_whenDiscoveryAfterExtinction() {
        assertThatThrownBy(() -> new Dinosaur("T-Rex", "Tyrannosaurus", future(), past()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("discoveryDate must be before extinction date");
    }

    @Test
    void constructor_throwsDomainException_whenDiscoveryEqualsExtinction() {
        Date same = new Date();
        assertThatThrownBy(() -> new Dinosaur("T-Rex", "Tyrannosaurus", same, same))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void constructor_allowsBothDatesNull() {
        assertThatCode(() -> new Dinosaur("T-Rex", "Tyrannosaurus", null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void constructor_allowsNullDiscoveryDate() {
        assertThatCode(() -> new Dinosaur("T-Rex", "Tyrannosaurus", null, future()))
                .doesNotThrowAnyException();
    }

    @Test
    void constructor_allowsNullExtinctionDate() {
        assertThatCode(() -> new Dinosaur("T-Rex", "Tyrannosaurus", past(), null))
                .doesNotThrowAnyException();
    }

    // -------------------------------------------------------------------------
    // Constructor — reconstitución desde persistencia
    // -------------------------------------------------------------------------

    @Test
    void reconstitutionConstructor_setsAllFields_includingIdAndStatus() {
        Date discovery = past();
        Date extinction = future();

        Dinosaur d = new Dinosaur(1L, "T-Rex", "Tyrannosaurus", discovery, extinction, Status.ENDANGERED);

        assertThat(d.getId()).isEqualTo(1L);
        assertThat(d.getName()).isEqualTo("T-Rex");
        assertThat(d.getSpecies()).isEqualTo("Tyrannosaurus");
        assertThat(d.getDiscoveryDate()).isEqualTo(discovery);
        assertThat(d.getExtinctionDate()).isEqualTo(extinction);
        assertThat(d.getStatus()).isEqualTo(Status.ENDANGERED);
    }

    @Test
    void reconstitutionConstructor_preservesExtinctStatus() {
        Dinosaur d = new Dinosaur(2L, "Raptor", "Velociraptor", past(), future(), Status.EXTINCT);
        assertThat(d.getStatus()).isEqualTo(Status.EXTINCT);
    }

    // -------------------------------------------------------------------------
    // updateDetails
    // -------------------------------------------------------------------------

    @Test
    void updateDetails_updatesAllFields() {
        Dinosaur d = new Dinosaur("T-Rex", "Old Species", past(), future());
        Date newDiscovery = new Date(System.currentTimeMillis() - 172_800_000L);
        Date newExtinction = new Date(System.currentTimeMillis() + 172_800_000L);

        d.updateDetails("New Name", "New Species", newDiscovery, newExtinction, Status.ENDANGERED);

        assertThat(d.getName()).isEqualTo("New Name");
        assertThat(d.getSpecies()).isEqualTo("New Species");
        assertThat(d.getDiscoveryDate()).isEqualTo(newDiscovery);
        assertThat(d.getExtinctionDate()).isEqualTo(newExtinction);
        assertThat(d.getStatus()).isEqualTo(Status.ENDANGERED);
    }

    @Test
    void updateDetails_updatesStatusToExtinct() {
        Dinosaur d = new Dinosaur("T-Rex", "Tyrannosaurus", past(), future());

        d.updateDetails("T-Rex", "Tyrannosaurus", past(), future(), Status.EXTINCT);

        assertThat(d.getStatus()).isEqualTo(Status.EXTINCT);
    }

    @Test
    void updateDetails_throwsDomainException_whenDinosaurIsExtinct() {
        Dinosaur d = new Dinosaur(1L, "T-Rex", "Tyrannosaurus", past(), future(), Status.EXTINCT);

        assertThatThrownBy(() -> d.updateDetails("New", "New", null, null, Status.ALIVE))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Cannot update an extinct dinosaur");
    }

    @Test
    void updateDetails_throwsDomainException_whenInvalidDates() {
        Dinosaur d = new Dinosaur("T-Rex", "Tyrannosaurus", null, null);

        assertThatThrownBy(() -> d.updateDetails("T-Rex", "Tyrannosaurus", future(), past(), Status.ALIVE))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("discoveryDate must be before extinction date");
    }

    @Test
    void updateDetails_allowsNullDates() {
        Dinosaur d = new Dinosaur("T-Rex", "Tyrannosaurus", past(), future());

        assertThatCode(() -> d.updateDetails("T-Rex", "Tyrannosaurus", null, null, Status.ALIVE))
                .doesNotThrowAnyException();
    }

    // -------------------------------------------------------------------------
    // updateStatus
    // -------------------------------------------------------------------------

    @Test
    void updateStatus_fromAlive_toEndangered() {
        Dinosaur d = new Dinosaur("T-Rex", "Tyrannosaurus", null, null);

        d.updateStatus(Status.ENDANGERED);

        assertThat(d.getStatus()).isEqualTo(Status.ENDANGERED);
    }

    @Test
    void updateStatus_fromAlive_toExtinct() {
        Dinosaur d = new Dinosaur("T-Rex", "Tyrannosaurus", null, null);

        d.updateStatus(Status.EXTINCT);

        assertThat(d.getStatus()).isEqualTo(Status.EXTINCT);
    }

    @Test
    void updateStatus_fromEndangered_toExtinct() {
        Dinosaur d = new Dinosaur(1L, "T-Rex", "Tyrannosaurus", null, null, Status.ENDANGERED);

        d.updateStatus(Status.EXTINCT);

        assertThat(d.getStatus()).isEqualTo(Status.EXTINCT);
    }

    @Test
    void updateStatus_throwsDomainException_whenAlreadyExtinct() {
        Dinosaur d = new Dinosaur(1L, "T-Rex", "Tyrannosaurus", null, null, Status.EXTINCT);

        assertThatThrownBy(() -> d.updateStatus(Status.ALIVE))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Status cannot be changed once it is EXTINCT");
    }

    @Test
    void updateStatus_throwsDomainException_whenExtinctToEndangered() {
        Dinosaur d = new Dinosaur(1L, "T-Rex", "Tyrannosaurus", null, null, Status.EXTINCT);

        assertThatThrownBy(() -> d.updateStatus(Status.ENDANGERED))
                .isInstanceOf(DomainException.class);
    }

    // -------------------------------------------------------------------------
    // DinosaurNotFoundException
    // -------------------------------------------------------------------------

    @Test
    void dinosaurNotFoundException_messageContainsId() {
        DinosaurNotFoundException ex = new DinosaurNotFoundException(42L);
        assertThat(ex.getMessage()).contains("42");
    }
}
