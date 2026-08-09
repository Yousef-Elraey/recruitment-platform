package com.job_service.job.dto.response;

import com.job_service.entity.EmploymentType;
import com.job_service.entity.Status;
import com.job_service.entity.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateJobResponse {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private Integer experienceLevel;
    private Long salaryMin;
    private Long salaryMax;
    private Status status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
