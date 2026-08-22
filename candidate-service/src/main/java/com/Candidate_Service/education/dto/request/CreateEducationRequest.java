package com.Candidate_Service.education.dto.request;

import com.Candidate_Service.entity.BaseEntity;
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
public class CreateEducationRequest {
    @NotNull(message = "candidate id is required")
    private Long candidateId;

    @NotNull(message = "institution is required")
    private String institution;

    @NotNull(message = "degree is required")
    private String degree;

    private String fieldOfStudy;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;


}
