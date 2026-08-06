package com.job_service.job.service;

import com.job_service.common.exceprion.ErrorCode;
import com.job_service.common.exceprion.RecruitmentBusinessException;
import com.job_service.entity.AuditorAwareImpl;
import com.job_service.entity.Job;
import com.job_service.entity.Status;
import com.job_service.job.dto.request.CreateJobRequest;
import com.job_service.job.dto.request.UpdateJobRequest;
import com.job_service.job.dto.response.CreateJobResponse;
import com.job_service.job.dto.response.GetJobResponse;
import com.job_service.job.dto.response.UpdateJobResponse;
import com.job_service.job.mapper.JobMapper;
import com.job_service.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public List<GetJobResponse> getAllJobs() {
        List<Job> jobList = jobRepository.findAll();
        if (jobList.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                    ErrorCode.JOB_NOT_FOUND.name(),
                    "no users found");
        }
        List<GetJobResponse> getJobResponseList = jobMapper.toGetJobResponses(jobList);
        return getJobResponseList;
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
//        AuditorAwareImpl currentAuditor = new AuditorAwareImpl();
//       Optional<String> currentUser = currentAuditor.getCurrentAuditor();

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
//                .setCreatedBy(currentUser.get());
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
