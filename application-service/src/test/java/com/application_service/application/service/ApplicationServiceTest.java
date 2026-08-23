package com.application_service.application.service;

import com.application_service.application.dto.request.AddFeedbackScoreRequest;
import com.application_service.application.dto.request.AssignRecruiterIdRequest;
import com.application_service.application.dto.request.CreateApplicationRequest;
import com.application_service.application.dto.request.NextFaceRequest;
import com.application_service.application.dto.response.AddFeedbackScoreResponse;
import com.application_service.application.dto.response.AssignRecruiterIdResponse;
import com.application_service.application.dto.response.CreateApplicationResponse;
import com.application_service.application.repository.ApplicationRepository;
import com.application_service.candidate_face.dto.response.GetCandidateFaceResponse;
import com.application_service.candidate_face.repository.CandidateFaceRepository;
import com.application_service.client.*;
import com.application_service.common.exceprion.ErrorCode;
import com.application_service.common.exceprion.RecruitmentBusinessException;
import com.application_service.entity.Application;
import com.application_service.entity.CandidateFace;
import com.application_service.entity.Face;
import com.application_service.face.repository.FaceRepository;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CandidateFaceRepository candidateFaceRepository;

    @Mock
    private FaceRepository faceRepository;

    @Mock
    private CandidateClient candidateClient;

    @Mock
    private JobClient jobClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ApplicationService applicationService;

    private Application application;
    private Face applyFace;

    @BeforeEach
    void setUp() {
        applyFace = new Face("APPLY", 1);

        application = new Application();
        application.setId(10L);
        application.setCandidateId(1L);
        application.setJobId(2L);
        application.setFace(applyFace);
    }

    @Test
    void createApplication_shouldThrowException_whenApplicationAlreadyExists() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setCandidateId(1L);
        request.setJobId(2L);

        when(candidateClient.getCandidate(1L)).thenReturn(new CandidateResponseDto());
        when(jobClient.getJob(2L)).thenReturn(new JobResponseDto());
        when(applicationRepository.findByCandidateIdAndJobId(1L, 2L))
                .thenReturn(Optional.of(application));

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.createApplication(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals(ErrorCode.ALREADY_EXISTS.name(), exception.getErrorCode());
        verify(applicationRepository, never()).save(any());
        verify(candidateFaceRepository, never()).save(any());
    }

    @Test
    void createApplication_shouldCreateApplicationAndCandidateFace_whenNotExists() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setCandidateId(1L);
        request.setJobId(2L);

        when(candidateClient.getCandidate(1L)).thenReturn(new CandidateResponseDto());
        when(jobClient.getJob(2L)).thenReturn(new JobResponseDto());
        when(applicationRepository.findByCandidateIdAndJobId(1L, 2L))
                .thenReturn(Optional.empty());

        CreateApplicationResponse response = applicationService.createApplication(request);

        ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository).save(appCaptor.capture());
        Application savedApp = appCaptor.getValue();

        assertEquals(1L, savedApp.getCandidateId());
        assertEquals(2L, savedApp.getJobId());
        assertEquals("APPLY", savedApp.getFace().getFaceCode());

        ArgumentCaptor<CandidateFace> cfCaptor = ArgumentCaptor.forClass(CandidateFace.class);
        verify(candidateFaceRepository).save(cfCaptor.capture());
        assertEquals(savedApp, cfCaptor.getValue().getApplication());
        assertEquals(savedApp.getFace(), cfCaptor.getValue().getFace());

        assertNotNull(response);
        assertEquals(savedApp.getId(), response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals(2L, response.getJobId());
        assertEquals("APPLY", response.getFaceCode());
    }

    @Test
    void createApplication_shouldPropagateException_whenCandidateClientFails() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setCandidateId(99L);
        request.setJobId(2L);

        when(candidateClient.getCandidate(99L))
                .thenThrow(new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                        ErrorCode.CANDIDATE_NOT_FOUND.name(), "candidate not found"));

        assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.createApplication(request)
        );

        verify(jobClient, never()).getJob(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void createApplication_shouldPropagateException_whenJobClientFails() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setCandidateId(1L);
        request.setJobId(99L);

        when(candidateClient.getCandidate(1L)).thenReturn(new CandidateResponseDto());
        when(jobClient.getJob(99L))
                .thenThrow(new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                        ErrorCode.JOB_NOT_FOUND.name(), "job not found"));

        assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.createApplication(request)
        );

        verify(applicationRepository, never()).findByCandidateIdAndJobId(any(), any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void assignRecruiterId_shouldThrowException_whenUserIsCandidate() {
        AssignRecruiterIdRequest request = new AssignRecruiterIdRequest();
        request.setApplicationId(10L);
        request.setRecruiterId(5L);

        UserResponseDto candidateUser = new UserResponseDto();
        candidateUser.setRole("CANDIDATE");

        when(userClient.getUser(5L)).thenReturn(candidateUser);

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.assignRecruiterId(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals(ErrorCode.NOT_VALID_OPERATION.name(), exception.getErrorCode());
        verify(applicationRepository, never()).findById(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void assignRecruiterId_shouldThrowException_whenApplicationNotFound() {
        AssignRecruiterIdRequest request = new AssignRecruiterIdRequest();
        request.setApplicationId(99L);
        request.setRecruiterId(5L);

        UserResponseDto recruiterUser = new UserResponseDto();
        recruiterUser.setRole("RECRUITER");

        when(userClient.getUser(5L)).thenReturn(recruiterUser);
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.assignRecruiterId(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.APPLICATION_NOT_FOUND.name(), exception.getErrorCode());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void assignRecruiterId_shouldThrowException_whenRecruiterAlreadyAssigned() {
        AssignRecruiterIdRequest request = new AssignRecruiterIdRequest();
        request.setApplicationId(10L);
        request.setRecruiterId(5L);

        application.setRecruiterId(3L);

        UserResponseDto recruiterUser = new UserResponseDto();
        recruiterUser.setRole("RECRUITER");

        when(userClient.getUser(5L)).thenReturn(recruiterUser);
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.assignRecruiterId(request)
        );

        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatus());
        assertEquals(ErrorCode.ALREADY_EXISTS.name(), exception.getErrorCode());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void assignRecruiterId_shouldAssignAndSave_whenValid() {
        AssignRecruiterIdRequest request = new AssignRecruiterIdRequest();
        request.setApplicationId(10L);
        request.setRecruiterId(5L);

        application.setRecruiterId(null);

        UserResponseDto recruiterUser = new UserResponseDto();
        recruiterUser.setRole("RECRUITER");

        when(userClient.getUser(5L)).thenReturn(recruiterUser);
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));

        AssignRecruiterIdResponse response = applicationService.assignRecruiterId(request);

        assertEquals(5L, application.getRecruiterId());
        assertEquals(5L, response.getRecruiterId());
        verify(applicationRepository).save(application);
    }

    @Test
    void addFinalFeedbackScore_shouldThrowException_whenApplicationNotFound() {
        AddFeedbackScoreRequest request = new AddFeedbackScoreRequest();
        request.setApplicationId(99L);

        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.addFinalFeedbackScore(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.APPLICATION_NOT_FOUND.name(), exception.getErrorCode());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void addFinalFeedbackScore_shouldSetFeedbackAndScore_whenApplicationFound() {
        AddFeedbackScoreRequest request = new AddFeedbackScoreRequest();
        request.setApplicationId(10L);
        request.setFeedback("Great candidate");
        request.setScore(9f);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));

        AddFeedbackScoreResponse response = applicationService.addFinalFeedbackScore(request);

        assertEquals("Great candidate", application.getFeedback());
        assertEquals(9, application.getScore());
        assertEquals("Great candidate", response.getFeedback());
        assertEquals(9, response.getScore());
        verify(applicationRepository).save(application);
    }

    @Test
    void nextFace_shouldThrowException_whenApplicationNotFound() {
        NextFaceRequest request = new NextFaceRequest();
        request.setApplicationId(99L);

        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.nextFace(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.APPLICATION_NOT_FOUND.name(), exception.getErrorCode());
    }

    @Test
    void nextFace_shouldThrowException_whenApplicationAlreadyRejected() {
        NextFaceRequest request = new NextFaceRequest();
        request.setApplicationId(10L);

        application.setFace(new Face("REJECTED", 99));

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.nextFace(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals(ErrorCode.NOT_VALID_OPERATION.name(), exception.getErrorCode());
        verify(faceRepository, never()).findByFaceOrder(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void nextFace_shouldThrowException_whenNoNextFaceExists() {
        NextFaceRequest request = new NextFaceRequest();
        request.setApplicationId(10L);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));
        when(faceRepository.findByFaceOrder(2)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.nextFace(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.NO_FACE_FOUND.name(), exception.getErrorCode());
        verify(applicationRepository, never()).save(any());
        verify(candidateFaceRepository, never()).save(any());
    }

    @Test
    void nextFace_shouldAdvanceFaceAndSave_whenNextFaceIsNotInterview() {
        NextFaceRequest request = new NextFaceRequest();
        request.setApplicationId(10L);
        request.setFeedback("ok");
        request.setScore(7f);

        Face nextFace = new Face("SCREENING", 2);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));
        when(faceRepository.findByFaceOrder(2)).thenReturn(Optional.of(nextFace));

        GetCandidateFaceResponse response = applicationService.nextFace(request);

        assertEquals(nextFace, application.getFace());
        verify(applicationRepository).save(application);

        ArgumentCaptor<CandidateFace> cfCaptor = ArgumentCaptor.forClass(CandidateFace.class);
        verify(candidateFaceRepository).save(cfCaptor.capture());
        assertEquals(application, cfCaptor.getValue().getApplication());
        assertEquals(nextFace, cfCaptor.getValue().getFace());

        assertEquals("SCREENING", response.getFaceCode());
        assertEquals(application.getId(), response.getApplicationId());

        verify(applicationRepository, times(1)).findById(10L);
    }

    @Test
    void nextFace_shouldTriggerFinalFeedback_whenNextFaceIsInterview() {
        NextFaceRequest request = new NextFaceRequest();
        request.setApplicationId(10L);
        request.setFeedback("Strong technical round");
        request.setScore(8f);

        Face interviewFace = new Face("INTERVIEW", 2);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));
        when(faceRepository.findByFaceOrder(2)).thenReturn(Optional.of(interviewFace));

        GetCandidateFaceResponse response = applicationService.nextFace(request);

        verify(applicationRepository, times(2)).findById(10L);

        assertEquals("Strong technical round", application.getFeedback());
        assertEquals(8, application.getScore());
        assertEquals(interviewFace, application.getFace());

        verify(applicationRepository, times(2)).save(application);

        assertEquals("INTERVIEW", response.getFaceCode());
    }

    @Test
    void rejectApplication_shouldThrowException_whenApplicationNotFound() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.rejectApplication(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.APPLICATION_NOT_FOUND.name(), exception.getErrorCode());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void rejectApplication_shouldThrowException_whenAlreadyApproved() {
        application.setFace(new Face("APPROVE", 5));

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> applicationService.rejectApplication(10L)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals(ErrorCode.NOT_VALID_OPERATION.name(), exception.getErrorCode());
        verify(faceRepository, never()).findById(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void rejectApplication_shouldSetRejectedFaceAndSave_whenApplicationFound() {
        Face rejectedFace = new Face("REJECTED", 99);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));
        when(faceRepository.findById("REJECTED")).thenReturn(Optional.of(rejectedFace));

        applicationService.rejectApplication(10L);

        assertEquals(rejectedFace, application.getFace());
        verify(applicationRepository).save(application);
    }
}