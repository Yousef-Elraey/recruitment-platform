package com.job_service.job.dto.request;

import com.job_service.entity.EmploymentType;
import com.job_service.entity.Status;
import com.job_service.entity.WorkMode;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestParam;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class JobSearchRequest {
    private String title;
    private String description;
    private String companyName;
    private String location;
    private EmploymentType employmentType;
    private WorkMode workMode;

    @PositiveOrZero(message = "Experience level must be greater than or equal to 0")
    private Integer experienceLevel;

    @PositiveOrZero(message = "Minimum salary must be greater than or equal to 0")
    private Long salaryMin;

    @PositiveOrZero(message = "Maximum salary must be greater than or equal to 0")
    private Long salaryMax;

    private Status status;
}
