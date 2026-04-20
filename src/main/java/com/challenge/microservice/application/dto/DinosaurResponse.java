package com.challenge.microservice.application.dto;

import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
public class DinosaurResponse {
    private Long id;
    private String name;
    private String species;
    private Date discoveryDate;
    private Date extinctionDate;
    private String status;
}
