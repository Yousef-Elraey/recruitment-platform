package com.Candidate_Service.experience.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateExperienceRequest {
    @NotNull(message = "candidate id is required")
    private Long candidateId;

    @NotBlank(message = "company name is required")
    private String companyName;

    @NotBlank(message = "job title is required")
    private String jobTitle;

    @NotNull(message = "start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "current is required")
    private Boolean current;

    @NotBlank(message = "description is required")
    private String description;

}
