package com.job_service.job.mapper;

import com.job_service.entity.Job;
import com.job_service.job.dto.request.CreateJobRequest;
import com.job_service.job.dto.request.UpdateJobRequest;
import com.job_service.job.dto.response.CreateJobResponse;
import com.job_service.job.dto.response.GetJobResponse;
import com.job_service.job.dto.response.UpdateJobResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobMapper {
    List<GetJobResponse> toGetJobResponses(List<Job> jobList);

    GetJobResponse toGetJobResponse(Job job);

    Job toJob(CreateJobRequest createJobRequest);

    CreateJobResponse toCreateJobResponse(Job job);

    Job toJob(UpdateJobRequest updateJobRequest);

    UpdateJobResponse toUpdateJobResponse(Job jobDb);
}
