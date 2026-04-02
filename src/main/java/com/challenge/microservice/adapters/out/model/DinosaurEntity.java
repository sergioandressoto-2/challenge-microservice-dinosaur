package com.challenge.microservice.adapters.out.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;


@Entity
@Table(name = "dinosaurs")
@Setter
@Getter
@Data
public class DinosaurEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true, nullable = false)
    String name;
    String species;
    Date discoveryDate;
    Date extinctionDate;
    String status;
}
