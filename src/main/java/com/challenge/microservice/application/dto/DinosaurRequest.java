package com.challenge.microservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class DinosaurRequest {
    @NotBlank(message = "name is required")
    String name;

    @NotBlank(message = "species is required")
    String species;

    @NotNull(message = "discoveryDate is required")
    Date discoveryDate;

    @NotNull(message = "extinctionDate is required")
    Date extinctionDate;

    @NotBlank(message = "status is required")
    String status;
}
