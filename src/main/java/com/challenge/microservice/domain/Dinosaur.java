package com.challenge.microservice.domain;

import lombok.Getter;
import java.time.LocalDate;
import java.util.Date;

@Getter
public class Dinosaur {
     String name;
     String species;
     Date discoveryDate;
     Date extinctionDate;
     String status;

     public Dinosaur(String name, String species, Date discovery, Date extinction) {
          validateDates(discovery, extinction);
          this.name = name;
          this.species = species;
          this.discoveryDate = discovery;
          this.extinctionDate = extinction;
          this.status = Status.ALIVE.toString();
     }

     public void updateDetails(String species, Date discovery, Date extinction) {
          if (this.status.equals(Status.EXTINCT.toString())) {
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

     public void updateStatus(String newStatus) {
          if (this.status.equals(Status.EXTINCT.toString())) {
               throw new DomainException("Status cannot be changed once it is EXTINCT.");
          }
          this.status = newStatus;
     }
}

