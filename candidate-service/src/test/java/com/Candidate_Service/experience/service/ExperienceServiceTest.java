package com.Candidate_Service.experience.service;

import static org.junit.jupiter.api.Assertions.*;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Experience;
import com.Candidate_Service.experience.dto.request.CreateExperienceRequest;
import com.Candidate_Service.experience.dto.request.UpdateExperienceRequest;
import com.Candidate_Service.experience.dto.response.CreateExperienceResponse;
import com.Candidate_Service.experience.dto.response.GetExperienceResponse;
import com.Candidate_Service.experience.dto.response.UpdateExperienceResponse;
import com.Candidate_Service.experience.repository.ExperienceRepository;
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
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private ExperienceService experienceService;

    private Candidate candidate;
    private Experience experience;

    @BeforeEach
    void setUp() {
        candidate = new Candidate();
        candidate.setId(1L);

        experience = new Experience();
        experience.setId(10L);
        experience.setCandidate(candidate);
        experience.setCompanyName("Google");
        experience.setJobTitle("Software Engineer");
        experience.setStartDate(LocalDate.of(2019, 1, 1));
        experience.setEndDate(LocalDate.of(2022, 1, 1));
        experience.setDescription("Worked on search infra");
    }

    @Test
    void getAllExperiences_shouldReturnEmptyList_whenNoRecordsExist() {
        when(experienceRepository.findAll()).thenReturn(Collections.emptyList());

        List<GetExperienceResponse> result = experienceService.getAllExperiences();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllExperiences_shouldMapAllRecords_whenRecordsExist() {
        when(experienceRepository.findAll()).thenReturn(List.of(experience));

        List<GetExperienceResponse> result = experienceService.getAllExperiences();

        assertEquals(1, result.size());
        GetExperienceResponse response = result.get(0);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals("Google", response.getCompanyName());
        assertEquals("Software Engineer", response.getJobTitle());
        assertEquals(experience.getStartDate(), response.getStartDate());
        assertEquals(experience.getEndDate(), response.getEndDate());
        assertEquals("Worked on search infra", response.getDescription());
    }
    @Test
    void getExperienceById_shouldThrowException_whenNotFound() {
        when(experienceRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> experienceService.getExperienceById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.EXPERIENCE_NOT_FOUND.name(), exception.getErrorCode());
    }

    @Test
    void getExperienceById_shouldReturnResponse_whenFound() {
        when(experienceRepository.findById(10L)).thenReturn(Optional.of(experience));

        GetExperienceResponse response = experienceService.getExperienceById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals("Google", response.getCompanyName());
        assertEquals("Software Engineer", response.getJobTitle());
    }

    @Test
    void createExperience_shouldThrowException_whenCandidateNotFound() {
        CreateExperienceRequest request = new CreateExperienceRequest();
        request.setCandidateId(99L);

        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> experienceService.createExperience(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(experienceRepository, never()).save(any());
    }

    @Test
    void createExperience_shouldSaveAndReturnResponse_whenCandidateFound() {
        CreateExperienceRequest request = new CreateExperienceRequest();
        request.setCandidateId(1L);
        request.setCompanyName("Meta");
        request.setJobTitle("Backend Engineer");
        request.setStartDate(LocalDate.of(2023, 1, 1));
        request.setEndDate(LocalDate.of(2024, 1, 1));
        request.setDescription("Worked on messaging systems");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        CreateExperienceResponse response = experienceService.createExperience(request);

        ArgumentCaptor<Experience> captor = ArgumentCaptor.forClass(Experience.class);
        verify(experienceRepository).save(captor.capture());
        Experience saved = captor.getValue();

        assertEquals(candidate, saved.getCandidate());
        assertEquals("Meta", saved.getCompanyName());
        assertEquals("Backend Engineer", saved.getJobTitle());
        assertEquals(request.getStartDate(), saved.getStartDate());
        assertEquals(request.getEndDate(), saved.getEndDate());
        assertEquals("Worked on messaging systems", saved.getDescription());

        assertNotNull(response);
        assertEquals(1L, response.getCandidateId());
        assertEquals("Meta", response.getCompanyName());
        assertEquals("Backend Engineer", response.getJobTitle());
    }

    @Test
    void updateExperience_shouldThrowException_whenExperienceNotFound() {
        UpdateExperienceRequest request = new UpdateExperienceRequest();
        request.setCandidateId(1L);

        when(experienceRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> experienceService.updateExperience(99L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.EXPERIENCE_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateRepository, never()).findById(any());
        verify(experienceRepository, never()).save(any());
    }

    @Test
    void updateExperience_shouldThrowException_whenCandidateNotFound() {
        UpdateExperienceRequest request = new UpdateExperienceRequest();
        request.setCandidateId(99L);

        when(experienceRepository.findById(10L)).thenReturn(Optional.of(experience));
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> experienceService.updateExperience(10L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(experienceRepository, never()).save(any());
    }

    @Test
    void updateExperience_shouldUpdateFieldsAndSave_whenValid() {
        UpdateExperienceRequest request = new UpdateExperienceRequest();
        request.setCandidateId(1L);
        request.setCompanyName("Amazon");
        request.setJobTitle("SDE II");
        request.setStartDate(LocalDate.of(2021, 3, 1));
        request.setEndDate(LocalDate.of(2023, 3, 1));
        request.setDescription("Worked on AWS services");

        Candidate newCandidate = new Candidate();
        newCandidate.setId(1L);

        when(experienceRepository.findById(10L)).thenReturn(Optional.of(experience));
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(newCandidate));

        UpdateExperienceResponse response = experienceService.updateExperience(10L, request);

        assertEquals(newCandidate, experience.getCandidate());
        assertEquals("Amazon", experience.getCompanyName());
        assertEquals("SDE II", experience.getJobTitle());
        assertEquals(request.getStartDate(), experience.getStartDate());
        assertEquals(request.getEndDate(), experience.getEndDate());
        assertEquals("Worked on AWS services", experience.getDescription());

        verify(experienceRepository).save(experience);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals("Amazon", response.getCompanyName());
        assertEquals("SDE II", response.getJobTitle());
    }

    @Test
    void deleteExperience_shouldThrowException_whenNotFound() {
        when(experienceRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> experienceService.deleteExperience(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.EXPERIENCE_NOT_FOUND.name(), exception.getErrorCode());
        verify(experienceRepository, never()).delete(any());
    }

    @Test
    void deleteExperience_shouldDelete_whenFound() {
        when(experienceRepository.findById(10L)).thenReturn(Optional.of(experience));

        experienceService.deleteExperience(10L);

        verify(experienceRepository).delete(experience);
    }
    @Test
    void getAllExperiencesByCandidateId_shouldThrowException_whenCandidateNotFound() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> experienceService.getAllExperiencesByCandidateId(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(experienceRepository, never()).findByCandidateId(any());
    }

    @Test
    void getAllExperiencesByCandidateId_shouldReturnEmptyList_whenNoExperiencesExist() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(experienceRepository.findByCandidateId(1L)).thenReturn(Collections.emptyList());

        List<GetExperienceResponse> result = experienceService.getAllExperiencesByCandidateId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllExperiencesByCandidateId_shouldReturnMappedList_whenExperiencesExist() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(experienceRepository.findByCandidateId(1L)).thenReturn(List.of(experience));

        List<GetExperienceResponse> result = experienceService.getAllExperiencesByCandidateId(1L);

        assertEquals(1, result.size());
        GetExperienceResponse response = result.get(0);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals("Google", response.getCompanyName());
        assertEquals("Software Engineer", response.getJobTitle());
    }
}