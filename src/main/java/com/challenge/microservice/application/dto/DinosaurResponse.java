package com.challenge.microservice.application.dto;

import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
public class DinosaurResponse {
    Long id;
    String name;
    String species;
    Date discoveryDate;
    Date extinctionDate;
    String status;
}
