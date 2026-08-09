package com.job_service.job.dto.request;

import com.job_service.entity.EmploymentType;
import com.job_service.entity.Status;
import com.job_service.entity.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateJobRequest {
    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "company name is required")
    private String companyName;

    @NotBlank(message = "location is required")
    private String location;

    @NotNull(message = "employment type should not be null")
    private EmploymentType employmentType;

    @NotNull(message = "work mode should not be null")
    private WorkMode workMode;

    @NotNull(message = "experience level should not be null")
    private Integer experienceLevel;

    @NotNull(message = "minimum salary should not be null")
    private Long salaryMin;

    @NotNull(message = "maximum salary should not be null")
    private Long salaryMax;

    @NotNull(message = "status should not be null")
    private Status status;

}
