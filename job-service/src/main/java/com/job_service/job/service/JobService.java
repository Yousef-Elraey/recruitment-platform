package com.job_service.job.service;

import com.job_service.common.exceprion.ErrorCode;
import com.job_service.common.exceprion.RecruitmentBusinessException;
import com.job_service.common.security.JwtService;
import com.job_service.entity.*;
import com.job_service.job.dto.request.CreateJobRequest;
import com.job_service.job.dto.request.JobSearchRequest;
import com.job_service.job.dto.request.UpdateJobRequest;
import com.job_service.job.dto.response.CreateJobResponse;
import com.job_service.job.dto.response.GetJobResponse;
import com.job_service.job.dto.response.PageResponse;
import com.job_service.job.dto.response.UpdateJobResponse;
import com.job_service.job.mapper.JobMapper;
import com.job_service.job.repository.JobRepository;
import com.job_service.job.specification.JobSpecification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final JwtService jwtService;
    public PageResponse<GetJobResponse> getAllJobs(HttpServletRequest request, JobSearchRequest searchRequest, int page, int size, String sortBy, String direction) {
      String authHeader = request.getHeader("Authorization");
      String token = authHeader.substring(7);
      String role = jwtService.extractRole(token);
      String email = jwtService.extractUsername(token);
      Long userId = jwtService.extractUserId(token);


        System.out.println("User ID       : " + userId);
        System.out.println("Email         : " + email);
        System.out.println("Role          : " + role);
        System.out.println("Authority     : " + "ROLE_" + role);

        if (searchRequest.getSalaryMin()> searchRequest.getSalaryMax()){
            throw new RecruitmentBusinessException(HttpStatus.BAD_REQUEST
                    ,ErrorCode.SALARY_NOT_VALID.name(),"the minimum salary is greater than maximum");
        }
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Job> specification = Specification.unrestricted();

       specification = specification
                        .and(JobSpecification.hasTitle(searchRequest.getTitle()))
                        .and(JobSpecification.hasDescription(searchRequest.getDescription()))
                        .and(JobSpecification.hasCompanyName(searchRequest.getCompanyName()))
                        .and(JobSpecification.hasLocation(searchRequest.getLocation()))
                        .and(JobSpecification.hasEmploymentType(searchRequest.getEmploymentType()))
                        .and(JobSpecification.hasSalaryMin(searchRequest.getSalaryMin()))
                        .and(JobSpecification.hasExperienceLevel(searchRequest.getExperienceLevel()))
                        .and(JobSpecification.hasSalaryMax(searchRequest.getSalaryMax()))
                        .and(JobSpecification.hasWorkMode(searchRequest.getWorkMode()))
                        .and(JobSpecification.hasStatus(searchRequest.getStatus()));

        Page<Job> jobPage = jobRepository.findAll(specification,pageable);
        if (jobPage.isEmpty()) {
            return  PageResponse.<GetJobResponse>builder()
                    .data(new ArrayList<>())
                    .page(jobPage.getNumber())
                    .size(jobPage.getSize())
                    .totalElements(jobPage.getTotalElements())
                    .totalPages(jobPage.getTotalPages())
                    .first(jobPage.isFirst())
                    .last(jobPage.isLast())
                    .build();

        }
        List<Job> jobList = jobPage.getContent();
        List<GetJobResponse> getJobResponseList = jobMapper.toGetJobResponses(jobList);
        return  PageResponse.<GetJobResponse>builder()
                .data(getJobResponseList)
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .totalElements(jobPage.getTotalElements())
                .totalPages(jobPage.getTotalPages())
                .first(jobPage.isFirst())
                .last(jobPage.isLast())
                .build();
    }

    public GetJobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(
                () -> new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                        ErrorCode.JOB_NOT_FOUND.name(),
                        "job with id (" + id + ") not found")
        );
        return jobMapper.toGetJobResponse(job);
    }


    public CreateJobResponse createJob(CreateJobRequest createJobRequest) {
        Job job = jobMapper.toJob(createJobRequest);
        jobRepository.save(job);
        return jobMapper.toCreateJobResponse(job);
    }

    public UpdateJobResponse updateJob(Long id, UpdateJobRequest updateJobRequest) {
        Job jobDb = jobRepository.findById(id).orElseThrow(() ->
                new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                        ErrorCode.JOB_NOT_FOUND.name(),
                        "job with id (" + id + ") not found"));
        jobDb.setTitle(updateJobRequest.getTitle())
                .setDescription(updateJobRequest.getDescription())
                .setCompanyName(updateJobRequest.getCompanyName())
                .setLocation(updateJobRequest.getLocation())
                .setEmploymentType(updateJobRequest.getEmploymentType())
                .setWorkMode(updateJobRequest.getWorkMode())
                .setExperienceLevel(updateJobRequest.getExperienceLevel())
                .setSalaryMin(updateJobRequest.getSalaryMin())
                .setSalaryMax(updateJobRequest.getSalaryMax())
                .setStatus(updateJobRequest.getStatus());
        jobRepository.save(jobDb);
        return jobMapper.toUpdateJobResponse(jobDb);

    }

    public void closeJob(Long id) {
       Job jobDb = jobRepository.findById(id).orElseThrow(()->
                new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                ErrorCode.JOB_NOT_FOUND.name(),
                "job with id (" + id + ") not found"));
       jobDb.setStatus(Status.CLOSED);
       jobRepository.save(jobDb);
    }
}
