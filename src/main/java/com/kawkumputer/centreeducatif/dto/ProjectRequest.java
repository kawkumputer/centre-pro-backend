package com.kawkumputer.centreeducatif.dto;

import com.kawkumputer.centreeducatif.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectRequest {
    @NotBlank(message = "Le nom du projet est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String name;

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    private LocalDate expectedEndDate;

    @NotNull(message = "Le statut est obligatoire")
    private ProjectStatus status;

    private BigDecimal initialBudget;
}
