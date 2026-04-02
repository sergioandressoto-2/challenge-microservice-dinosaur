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

     // Constructor para crear un nuevo dinosaurio (sin id, lo genera la DB)
     public Dinosaur(String name, String species, Date discovery, Date extinction) {
          validateDates(discovery, extinction);
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

     public void updateDetails(String species, Date discovery, Date extinction) {
          if (this.status == Status.EXTINCT) {
               throw new DomainException("Cannot update an extinct dinosaur.");
          }
          validateDates(discovery, extinction);
          this.species = species;
          this.discoveryDate = discovery;
          this.extinctionDate = extinction;
     }

     private void validateDates(Date discovery, Date extinction) {
          if (discovery != null && extinction != null && !discovery.before(extinction)) {
               throw new DomainException("discoveryDate must be before extinction date.");
          }
     }

     public void updateStatus(Status newStatus) {
          if (this.status == Status.EXTINCT) {
               throw new DomainException("Status cannot be changed once it is EXTINCT.");
          }
          this.status = newStatus;
     }
}

