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
public class UpdateEducationRequest extends BaseEntity {
    @NotNull(message = "candidate id is required")
    private Long candidateId;

    @NotNull(message = "institution id is required")
    private String institution;

    @NotNull(message = "degree id is required")
    private String degree;

    @NotNull(message = "field of study id is required")
    private String fieldOfStudy;

    @NotNull(message = "start date id is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "current id is required")
    private Boolean current;

    @NotNull(message = "description id is required")
    private String description;


}
