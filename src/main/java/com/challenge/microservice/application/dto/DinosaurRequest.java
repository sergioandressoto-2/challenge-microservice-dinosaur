package com.challenge.microservice.application.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
public class DinosaurRequest {
        String name;
        String species;
        Date discoveryDate;
        Date extinctionDate;
        String status;
}
