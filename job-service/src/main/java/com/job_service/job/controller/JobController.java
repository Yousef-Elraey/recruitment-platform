package com.job_service.job.controller;

import com.job_service.entity.EmploymentType;
import com.job_service.entity.Status;
import com.job_service.entity.WorkMode;
import com.job_service.job.dto.request.CreateJobRequest;
import com.job_service.job.dto.request.JobSearchRequest;
import com.job_service.job.dto.request.UpdateJobRequest;
import com.job_service.job.dto.response.CreateJobResponse;
import com.job_service.job.dto.response.GetJobResponse;
import com.job_service.job.dto.response.PageResponse;
import com.job_service.job.dto.response.UpdateJobResponse;
import com.job_service.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job",description = "manage job operations")
@SecurityRequirement(name = "Bearer Authentication")
public class JobController {
 private final JobService jobService;

 @GetMapping
 @PreAuthorize("hasAnyRole('ADMIN','HR','INTERVIEWER','CANDIDATE')")
 @Operation(summary = "get all job records")
 public ResponseEntity<PageResponse<GetJobResponse>> getAllJobs(
                                                                @ParameterObject
                                                                JobSearchRequest searchRequest,

                                                                @RequestParam(defaultValue = "0")
                                                                 int page,

                                                                @RequestParam(defaultValue = "10")
                                                                 int size,

                                                                @RequestParam(defaultValue = "createdAt")
                                                                 String sortBy,

                                                                @RequestParam(defaultValue = "desc")
                                                                 String direction

 ) {
  return new ResponseEntity<>(jobService.getAllJobs(searchRequest,page, size, sortBy, direction), HttpStatus.OK);
 }

 @GetMapping("/{id}")
 @PreAuthorize("hasAnyRole('ADMIN','HR','INTERVIEWER','CANDIDATE')")
 @Operation(summary = "get job record by id")
 public ResponseEntity<GetJobResponse> getJobById(@PathVariable Long id) {
  return new ResponseEntity<>(jobService.getJobById(id), HttpStatus.OK);
 }

 @PostMapping
 @PreAuthorize("hasAnyRole('ADMIN','HR')")
 @Operation(summary = "create job")
 public ResponseEntity<CreateJobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
  return new ResponseEntity<CreateJobResponse>(jobService.createJob(request), HttpStatus.CREATED);
 }

 @PutMapping("/{id}")
 @PreAuthorize("hasAnyRole('ADMIN','HR')")
 @Operation(summary = "update job data")
 public ResponseEntity<UpdateJobResponse> updateJob(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateJobRequest request) {
  return new ResponseEntity<UpdateJobResponse>(jobService.updateJob(id, request), HttpStatus.OK);
 }
@PatchMapping("/{id}")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
@Operation(summary = "close job")
 public ResponseEntity<String> closeJob(@PathVariable Long id){
 jobService.closeJob(id);
  return new ResponseEntity<>("Job closed successfully",HttpStatus.NO_CONTENT);
}
}
