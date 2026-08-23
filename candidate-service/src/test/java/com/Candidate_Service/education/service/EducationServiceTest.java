package com.Candidate_Service.education.service;

import static org.junit.jupiter.api.Assertions.*;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.education.dto.request.CreateEducationRequest;
import com.Candidate_Service.education.dto.request.UpdateEducationRequest;
import com.Candidate_Service.education.dto.response.CreateEducationResponse;
import com.Candidate_Service.education.dto.response.GetEducationResponse;
import com.Candidate_Service.education.dto.response.UpdateEducationResponse;
import com.Candidate_Service.education.maper.EducationMapper;
import com.Candidate_Service.education.repository.EducationRepository;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Education;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationService educationService;

    private Candidate candidate;
    private Education education;

    @BeforeEach
    void setUp() {
        candidate = new Candidate();
        candidate.setId(1L);

        education = new Education();
        education.setId(10L);
        education.setCandidate(candidate);
        education.setInstitution("MIT");
        education.setDegree("BSc");
        education.setFieldOfStudy("Computer Science");
        education.setStartDate(LocalDate.of(2018, 9, 1));
        education.setEndDate(LocalDate.of(2022, 6, 1));
        education.setDescription("Graduated with honors");
    }


    @Test
    void getAllEducations_shouldReturnEmptyList_whenNoRecordsExist() {
        when(educationRepository.findAll()).thenReturn(Collections.emptyList());

        List<GetEducationResponse> result = educationService.getAllEducations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllEducations_shouldMapAllRecords_whenRecordsExist() {
        when(educationRepository.findAll()).thenReturn(List.of(education));

        List<GetEducationResponse> result = educationService.getAllEducations();

        assertEquals(1, result.size());
        GetEducationResponse response = result.get(0);
        assertEquals(1L, response.getCandidateId());
        assertEquals("MIT", response.getInstitution());
        assertEquals("BSc", response.getDegree());
        assertEquals("Computer Science", response.getFieldOfStudy());
        assertEquals(education.getStartDate(), response.getStartDate());
        assertEquals(education.getEndDate(), response.getEndDate());
        assertEquals("Graduated with honors", response.getDescription());
    }

    @Test
    void createEducation_shouldThrowException_whenCandidateNotFound() {
        CreateEducationRequest request = new CreateEducationRequest();
        request.setCandidateId(99L);

        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> educationService.createEducation(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducation(any());
    }

    @Test
    void createEducation_shouldMapAndSave_whenCandidateFound() {
        CreateEducationRequest request = new CreateEducationRequest();
        request.setCandidateId(1L);
        request.setInstitution("Stanford");
        request.setDegree("MSc");
        request.setFieldOfStudy("AI");
        request.setStartDate(LocalDate.of(2022, 9, 1));
        request.setEndDate(LocalDate.of(2024, 6, 1));
        request.setDescription("Focused on machine learning");

        Education mappedEducation = new Education();

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(educationMapper.toEducation(request)).thenReturn(mappedEducation);

        CreateEducationResponse response = educationService.createEducation(request);

        ArgumentCaptor<Education> captor = ArgumentCaptor.forClass(Education.class);
        verify(educationRepository).save(captor.capture());
        Education saved = captor.getValue();

        assertEquals(candidate, saved.getCandidate());
        assertEquals("Stanford", saved.getInstitution());
        assertEquals("MSc", saved.getDegree());
        assertEquals("AI", saved.getFieldOfStudy());
        assertEquals(request.getStartDate(), saved.getStartDate());
        assertEquals(request.getEndDate(), saved.getEndDate());
        assertEquals("Focused on machine learning", saved.getDescription());

        assertNotNull(response);
        assertEquals(1L, response.getCandidateId());
        assertEquals("Stanford", response.getInstitution());
        assertEquals("MSc", response.getDegree());
    }

    @Test
    void getEducationById_shouldThrowException_whenNotFound() {
        when(educationRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> educationService.getEducationById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.EDUCATION_NOT_FOUND.name(), exception.getErrorCode());
    }

    @Test
    void getEducationById_shouldReturnResponse_whenFound() {
        when(educationRepository.findById(10L)).thenReturn(Optional.of(education));

        GetEducationResponse response = educationService.getEducationById(10L);

        assertNotNull(response);
        assertEquals(1L, response.getCandidateId());
        assertEquals("MIT", response.getInstitution());
        assertEquals("BSc", response.getDegree());
        assertEquals("Computer Science", response.getFieldOfStudy());
    }

    @Test
    void updateEducation_shouldThrowException_whenCandidateNotFound() {
        UpdateEducationRequest request = new UpdateEducationRequest();
        request.setCandidateId(99L);

        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> educationService.updateEducation(10L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(educationRepository, never()).findById(any());
        verify(educationRepository, never()).save(any());
    }

    @Test
    void updateEducation_shouldThrowException_whenEducationNotFound() {
        UpdateEducationRequest request = new UpdateEducationRequest();
        request.setCandidateId(1L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(educationRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> educationService.updateEducation(99L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.EDUCATION_NOT_FOUND.name(), exception.getErrorCode());
        verify(educationRepository, never()).save(any());
    }

    @Test
    void updateEducation_shouldUpdateFieldsAndSave_whenValid() {
        UpdateEducationRequest request = new UpdateEducationRequest();
        request.setCandidateId(1L);
        request.setInstitution("Harvard");
        request.setDegree("PhD");
        request.setFieldOfStudy("Physics");
        request.setStartDate(LocalDate.of(2020, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 1));
        request.setDescription("Updated description");

        Candidate newCandidate = new Candidate();
        newCandidate.setId(1L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(newCandidate));
        when(educationRepository.findById(10L)).thenReturn(Optional.of(education));

        UpdateEducationResponse response = educationService.updateEducation(10L, request);

        assertEquals(newCandidate, education.getCandidate());
        assertEquals("Harvard", education.getInstitution());
        assertEquals("PhD", education.getDegree());
        assertEquals("Physics", education.getFieldOfStudy());
        assertEquals(request.getStartDate(), education.getStartDate());
        assertEquals(request.getEndDate(), education.getEndDate());
        assertEquals("Updated description", education.getDescription());

        verify(educationRepository).save(education);

        assertNotNull(response);
        assertEquals(1L, response.getCandidateId());
        assertEquals("Harvard", response.getInstitution());
        assertEquals("PhD", response.getDegree());
    }

    @Test
    void deleteEducation_shouldThrowException_whenNotFound() {
        when(educationRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> educationService.deleteEducation(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.EDUCATION_NOT_FOUND.name(), exception.getErrorCode());
        verify(educationRepository, never()).delete(any());
    }

    @Test
    void deleteEducation_shouldDelete_whenFound() {
        when(educationRepository.findById(10L)).thenReturn(Optional.of(education));

        educationService.deleteEducation(10L);

        verify(educationRepository).delete(education);
    }

    @Test
    void getAllEducationsByCandidateId_shouldThrowException_whenCandidateNotFound() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> educationService.getAllEducationsByCandidateId(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(educationRepository, never()).findByCandidateId(any());
    }

    @Test
    void getAllEducationsByCandidateId_shouldReturnEmptyList_whenNoEducationsExist() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(educationRepository.findByCandidateId(1L)).thenReturn(Collections.emptyList());

        List<GetEducationResponse> result = educationService.getAllEducationsByCandidateId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllEducationsByCandidateId_shouldReturnMappedList_whenEducationsExist() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(educationRepository.findByCandidateId(1L)).thenReturn(List.of(education));

        List<GetEducationResponse> result = educationService.getAllEducationsByCandidateId(1L);

        assertEquals(1, result.size());
        GetEducationResponse response = result.get(0);
        assertEquals(1L, response.getCandidateId());
        assertEquals("MIT", response.getInstitution());
        assertEquals("BSc", response.getDegree());
        assertEquals("Computer Science", response.getFieldOfStudy());
    }
}