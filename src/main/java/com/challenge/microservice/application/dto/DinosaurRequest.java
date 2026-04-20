package com.challenge.microservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class DinosaurRequest {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "species is required")
    private String species;

    @NotNull(message = "discoveryDate is required")
    private Date discoveryDate;

    @NotNull(message = "extinctionDate is required")
    private Date extinctionDate;

    @NotBlank(message = "status is required")
    private String status;
}
