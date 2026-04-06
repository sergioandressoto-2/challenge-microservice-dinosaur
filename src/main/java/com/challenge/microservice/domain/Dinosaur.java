package com.challenge.microservice.domain;

import lombok.Getter;
import java.util.Date;

@Getter
public class Dinosaur {
     private Long id;
     private String name;
     private String species;
     private Date discoveryDate;
     private Date extinctionDate;
     private Status status;


     public Dinosaur(String name, String species, Date discovery, Date extinction, String status) {
          validateDates(discovery, extinction);
          validateStatusIsValid(status);
          validateStatusIsAlive(status);
          this.name = name;
          this.species = species;
          this.discoveryDate = discovery;
          this.extinctionDate = extinction;
          this.status = Status.ALIVE;
     }

     // Constructor para reconstituir desde persistencia
     public Dinosaur(Long id, String name, String species, Date discovery, Date extinction, Status status) {
          this.id = id;
          this.name = name;
          this.species = species;
          this.discoveryDate = discovery;
          this.extinctionDate = extinction;
          this.status = status;
     }

     public void updateDetails(String name, String species, Date discovery, Date extinction, Status status) {
          if (this.status == Status.EXTINCT) {
               throw new DomainException("Cannot update an extinct dinosaur.");
          }
          validateDates(discovery, extinction);
          validateStatusIsValid(status.toString());
          this.name = name;
          this.species = species;
          this.discoveryDate = discovery;
          this.extinctionDate = extinction;
          this.status = status;
     }

     private void validateDates(Date discovery, Date extinction) {
          if (discovery != null && extinction != null && !discovery.before(extinction)) {
               throw new DomainException("discoveryDate must be before extinction date.");
          }
     }

     private void validateStatusIsValid(String status) {
          try {
               Status.valueOf(status);
          } catch (IllegalArgumentException | NullPointerException e) {
               throw new DomainException("Invalid status value: " + status);
          }
     }

     private void validateStatusIsAlive(String status) {
          if (!Status.ALIVE.name().equals(status)) {
               throw new DomainException("Initial status must be ALIVE.");
          }
     }

     public void updateStatus(Status newStatus) {
          if (this.status == Status.EXTINCT) {
               throw new DomainException("Status cannot be changed once it is EXTINCT.");
          }
          this.status = newStatus;
     }
}

