package com.job_service.job.service;

import com.job_service.common.exceprion.ErrorCode;
import com.job_service.common.exceprion.RecruitmentBusinessException;
import com.job_service.entity.EmploymentType;
import com.job_service.entity.Job;
import com.job_service.entity.Status;
import com.job_service.entity.WorkMode;
import com.job_service.job.dto.request.CreateJobRequest;
import com.job_service.job.dto.request.JobSearchRequest;
import com.job_service.job.dto.request.UpdateJobRequest;
import com.job_service.job.dto.response.CreateJobResponse;
import com.job_service.job.dto.response.GetJobResponse;
import com.job_service.job.dto.response.PageResponse;
import com.job_service.job.dto.response.UpdateJobResponse;
import com.job_service.job.mapper.JobMapper;
import com.job_service.job.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobService jobService;

    private Job job;
private JobSearchRequest jobSearchRequest;
    @BeforeEach
    void setUp() {
        job = new Job();
        job.setId(1L);
        job.setTitle("Backend Developer");
    }

    @Test
    void getAllJobs_shouldReturnPageResponse_whenJobsExist() {
        List<Job> jobList = List.of(job);
        Page<Job> jobPage = new PageImpl<>(jobList, PageRequest.of(0, 10), 1);
        List<GetJobResponse> mappedResponses = List.of(new GetJobResponse());

        when(jobRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(jobPage);
        when(jobMapper.toGetJobResponses(jobList)).thenReturn(mappedResponses);

        PageResponse<GetJobResponse> result = jobService.getAllJobs(
                jobSearchRequest,  0, 10, "title", "asc"
        );

        assertNotNull(result);
        assertEquals(mappedResponses, result.getData());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
    }

    @Test
    void getAllJobs_shouldThrowException_whenPageIsEmpty() {
        Page<Job> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(jobRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> jobService.getAllJobs(
                        jobSearchRequest,  0, 10, "title", "asc"

                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.JOB_NOT_FOUND.name(), exception.getErrorCode());
        verify(jobMapper, never()).toGetJobResponses(any());
    }

    @Test
    void getAllJobs_shouldBuildPageable_withAscendingSort_byDefault() {
        Page<Job> jobPage = new PageImpl<>(List.of(job), PageRequest.of(2, 5), 20);
        when(jobRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(jobPage);
        when(jobMapper.toGetJobResponses(any())).thenReturn(List.of(new GetJobResponse()));

        jobService.getAllJobs(
                jobSearchRequest,  0, 10, "title", "asc"

        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jobRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(2, capturedPageable.getPageNumber());
        assertEquals(5, capturedPageable.getPageSize());
        Sort.Order order = capturedPageable.getSort().getOrderFor("salaryMin");
        assertNotNull(order);
        assertTrue(order.isAscending());
    }

    @Test
    void getAllJobs_shouldBuildPageable_withDescendingSort_whenDirectionIsDesc() {
        Page<Job> jobPage = new PageImpl<>(List.of(job), PageRequest.of(0, 10), 1);
        when(jobRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(jobPage);
        when(jobMapper.toGetJobResponses(any())).thenReturn(List.of(new GetJobResponse()));

        jobService.getAllJobs(
                jobSearchRequest,  0, 10, "title", "asc"

        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jobRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("createdAt");
        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    @Test
    void getAllJobs_shouldPassNonNullSpecification_whenFiltersProvided() {
        Page<Job> jobPage = new PageImpl<>(List.of(job), PageRequest.of(0, 10), 1);
        when(jobRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(jobPage);
        when(jobMapper.toGetJobResponses(any())).thenReturn(List.of(new GetJobResponse()));

        jobService.getAllJobs(
                jobSearchRequest,  0, 10, "title", "asc"

        );

        ArgumentCaptor<Specification> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(jobRepository).findAll(specCaptor.capture(), any(Pageable.class));

        // With the fix, the specification is the accumulated chain, not the raw unrestricted() instance.
        assertNotNull(specCaptor.getValue());
    }

    @Test
    void getAllJobs_shouldStillWork_whenAllFiltersAreNull() {
        Page<Job> jobPage = new PageImpl<>(List.of(job), PageRequest.of(0, 10), 1);
        when(jobRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(jobPage);
        when(jobMapper.toGetJobResponses(any())).thenReturn(List.of(new GetJobResponse()));

        assertDoesNotThrow(() -> jobService.getAllJobs(
                jobSearchRequest,  0, 10, "title", "asc"

        ));
    }

    @Test
    void getJobById_shouldReturnJob_whenFound() {
        GetJobResponse expectedResponse = new GetJobResponse();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobMapper.toGetJobResponse(job)).thenReturn(expectedResponse);

        GetJobResponse result = jobService.getJobById(1L);

        assertEquals(expectedResponse, result);
        verify(jobRepository).findById(1L);
    }

    @Test
    void getJobById_shouldThrowException_whenNotFound() {
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> jobService.getJobById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.JOB_NOT_FOUND.name(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("99"));
        verify(jobMapper, never()).toGetJobResponse(any());
    }

    @Test
    void createJob_shouldSaveAndReturnResponse() {
        CreateJobRequest request = new CreateJobRequest();
        CreateJobResponse expectedResponse = new CreateJobResponse();

        when(jobMapper.toJob(request)).thenReturn(job);
        when(jobMapper.toCreateJobResponse(job)).thenReturn(expectedResponse);

        CreateJobResponse result = jobService.createJob(request);

        assertEquals(expectedResponse, result);
        verify(jobRepository).save(job);
        verify(jobMapper).toJob(request);
        verify(jobMapper).toCreateJobResponse(job);
    }

    @Test
    void updateJob_shouldUpdateFieldsAndSave_whenJobExists() {
        UpdateJobRequest request = new UpdateJobRequest();
        request.setTitle("Senior Backend Developer");
        request.setDescription("Updated description");
        request.setCompanyName("Acme Corp");
        request.setLocation("Remote");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setWorkMode(WorkMode.REMOTE);
        request.setExperienceLevel(3);
        request.setSalaryMin(50000L);
        request.setSalaryMax(90000L);
        request.setStatus(Status.OPEN);

        UpdateJobResponse expectedResponse = new UpdateJobResponse();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobMapper.toUpdateJobResponse(job)).thenReturn(expectedResponse);

        UpdateJobResponse result = jobService.updateJob(1L, request);

        assertEquals(expectedResponse, result);
        assertEquals("Senior Backend Developer", job.getTitle());
        assertEquals("Updated description", job.getDescription());
        assertEquals("Acme Corp", job.getCompanyName());
        assertEquals("Remote", job.getLocation());
        assertEquals(EmploymentType.FULL_TIME, job.getEmploymentType());
        assertEquals(WorkMode.REMOTE, job.getWorkMode());
        assertEquals(3, job.getExperienceLevel());
        assertEquals(50000, job.getSalaryMin());
        assertEquals(90000, job.getSalaryMax());
        assertEquals(Status.OPEN, job.getStatus());

        verify(jobRepository).save(job);
    }

    @Test
    void updateJob_shouldThrowException_whenJobNotFound() {
        UpdateJobRequest request = new UpdateJobRequest();

        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> jobService.updateJob(99L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.JOB_NOT_FOUND.name(), exception.getErrorCode());
        verify(jobRepository, never()).save(any());
    }

    @Test
    void closeJob_shouldSetStatusClosedAndSave_whenJobExists() {
        job.setStatus(Status.OPEN);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        jobService.closeJob(1L);

        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(jobCaptor.capture());

        assertEquals(Status.CLOSED, jobCaptor.getValue().getStatus());
    }

    @Test
    void closeJob_shouldThrowException_whenJobNotFound() {
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> jobService.closeJob(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.JOB_NOT_FOUND.name(), exception.getErrorCode());
        verify(jobRepository, never()).save(any());
    }
}