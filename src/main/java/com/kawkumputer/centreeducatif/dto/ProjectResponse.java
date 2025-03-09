package com.kawkumputer.centreeducatif.dto;

import com.kawkumputer.centreeducatif.domain.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private ProjectStatus status;
    private String statusDisplayName;
    private BigDecimal initialBudget;
    private UserResponse owner;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
