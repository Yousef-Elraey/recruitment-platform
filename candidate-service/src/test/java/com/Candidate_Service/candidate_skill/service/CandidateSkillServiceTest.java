package com.Candidate_Service.candidate_skill.service;

import static org.junit.jupiter.api.Assertions.*;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.candidate_skill.dto.request.CreateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.request.UpdateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.response.CreateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.GetCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.UpdateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.mapper.CandidateSkillMapper;
import com.Candidate_Service.candidate_skill.repository.CandidateSkillRepository;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.CandidateSkill;
import com.Candidate_Service.entity.Skill;
import com.Candidate_Service.skill.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateSkillServiceTest {

    @Mock
    private CandidateSkillRepository candidateSkillRepository;

    @Mock
    private CandidateSkillMapper candidateSkillMapper;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private CandidateSkillService candidateSkillService;

    private Candidate candidate;
    private Skill skill;
    private CandidateSkill candidateSkill;

    @BeforeEach
    void setUp() {
        candidate = new Candidate();
        candidate.setId(1L);

        skill = new Skill();
        skill.setId(2L);
        skill.setSkillName("Java");

        candidateSkill = new CandidateSkill();
        candidateSkill.setId(10L);
        candidateSkill.setCandidate(candidate);
        candidateSkill.setSkill(skill);
        candidateSkill.setYearsOfExperience(3);
    }


    @Test
    void getAllCandidateSkills_shouldReturnEmptyList_whenNoRecordsExist() {
        when(candidateSkillRepository.findAll()).thenReturn(Collections.emptyList());

        List<GetCandidateSkillResponse> result = candidateSkillService.getAllCandidateSkills();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCandidateSkills_shouldMapAllRecords_whenRecordsExist() {
        when(candidateSkillRepository.findAll()).thenReturn(List.of(candidateSkill));

        List<GetCandidateSkillResponse> result = candidateSkillService.getAllCandidateSkills();

        assertEquals(1, result.size());
        GetCandidateSkillResponse response = result.get(0);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals(skill, response.getSkill());
        assertEquals(3, response.getYearsOfExperience());
    }

    // ---------- createCandidateSkill ----------

    @Test
    void createCandidateSkill_shouldThrowException_whenCandidateNotFound() {
        CreateCandidateSkillRequest request = new CreateCandidateSkillRequest();
        request.setCandidateId(99L);
        request.setSkillId(2L);

        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.createCandidateSkill(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(skillRepository, never()).findById(any());
        verify(candidateSkillRepository, never()).save(any());
    }

    @Test
    void createCandidateSkill_shouldThrowException_whenSkillNotFound() {
        CreateCandidateSkillRequest request = new CreateCandidateSkillRequest();
        request.setCandidateId(1L);
        request.setSkillId(99L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.createCandidateSkill(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.SKILL_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateSkillRepository, never()).save(any());
    }

    @Test
    void createCandidateSkill_shouldThrowException_whenRecordAlreadyExists() {
        CreateCandidateSkillRequest request = new CreateCandidateSkillRequest();
        request.setCandidateId(1L);
        request.setSkillId(2L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill));
        when(candidateSkillRepository.findByCandidateIdAndSkillId(1L, 2L))
                .thenReturn(Optional.of(candidateSkill));

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.createCandidateSkill(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals(ErrorCode.ALREADY_EXISTS.name(), exception.getErrorCode());
        verify(candidateSkillRepository, never()).save(any());
    }

    @Test
    void createCandidateSkill_shouldSaveAndReturnResponse_whenValid() {
        CreateCandidateSkillRequest request = new CreateCandidateSkillRequest();
        request.setCandidateId(1L);
        request.setSkillId(2L);
        request.setYearsOfExperience(4);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill));
        when(candidateSkillRepository.findByCandidateIdAndSkillId(1L, 2L))
                .thenReturn(Optional.empty());

        CreateCandidateSkillResponse response = candidateSkillService.createCandidateSkill(request);

        ArgumentCaptor<CandidateSkill> captor = ArgumentCaptor.forClass(CandidateSkill.class);
        verify(candidateSkillRepository).save(captor.capture());
        CandidateSkill saved = captor.getValue();

        assertEquals(candidate, saved.getCandidate());
        assertEquals(skill, saved.getSkill());
        assertEquals(4, saved.getYearsOfExperience());

        assertNotNull(response);
        assertEquals(1L, response.getCandidateId());
        assertEquals(2L, response.getSkillId());
        assertEquals(4, response.getYearsOfExperience());
    }

    @Test
    void getCandidateSkillById_shouldThrowException_whenNotFound() {
        when(candidateSkillRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.getCandidateSkillById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_SKILL_NOT_FOUND.name(), exception.getErrorCode());
    }

    @Test
    void getCandidateSkillById_shouldReturnResponse_whenFound() {
        when(candidateSkillRepository.findById(10L)).thenReturn(Optional.of(candidateSkill));

        GetCandidateSkillResponse response = candidateSkillService.getCandidateSkillById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals(skill, response.getSkill());
        assertEquals(3, response.getYearsOfExperience());
    }

    @Test
    void updateCandidateSkill_shouldThrowException_whenCandidateNotFound() {
        UpdateCandidateSkillRequest request = new UpdateCandidateSkillRequest();
        request.setCandidateId(99L);
        request.setSkillId(2L);

        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.updateCandidateSkill(10L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(skillRepository, never()).findById(any());
        verify(candidateSkillRepository, never()).findById(any());
        verify(candidateSkillRepository, never()).save(any());
    }

    @Test
    void updateCandidateSkill_shouldThrowException_whenSkillNotFound() {
        UpdateCandidateSkillRequest request = new UpdateCandidateSkillRequest();
        request.setCandidateId(1L);
        request.setSkillId(99L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.updateCandidateSkill(10L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.SKILL_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateSkillRepository, never()).findById(any());
        verify(candidateSkillRepository, never()).save(any());
    }

    @Test
    void updateCandidateSkill_shouldThrowException_whenCandidateSkillNotFound() {
        UpdateCandidateSkillRequest request = new UpdateCandidateSkillRequest();
        request.setCandidateId(1L);
        request.setSkillId(2L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill));
        when(candidateSkillRepository.findById(10L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.updateCandidateSkill(10L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_SKILL_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateSkillRepository, never()).save(any());
    }

    @Test
    void updateCandidateSkill_shouldUpdateAndSave_whenValid() {
        UpdateCandidateSkillRequest request = new UpdateCandidateSkillRequest();
        request.setCandidateId(1L);
        request.setSkillId(2L);
        request.setYearsOfExperience(7);

        Candidate newCandidate = new Candidate();
        newCandidate.setId(1L);
        Skill newSkill = new Skill();
        newSkill.setId(2L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(newCandidate));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(newSkill));
        when(candidateSkillRepository.findById(10L)).thenReturn(Optional.of(candidateSkill));

        UpdateCandidateSkillResponse response = candidateSkillService.updateCandidateSkill(10L, request);

        assertEquals(newCandidate, candidateSkill.getCandidate());
        assertEquals(newSkill, candidateSkill.getSkill());
        assertEquals(7, candidateSkill.getYearsOfExperience());

        verify(candidateSkillRepository).save(candidateSkill);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals(2L, response.getSkillId());
        assertEquals(7, response.getYearsOfExperience());
    }

    @Test
    void deleteCandidateSkill_shouldThrowException_whenNotFound() {
        when(candidateSkillRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.deleteCandidateSkill(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_SKILL_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateSkillRepository, never()).delete(any());
    }

    @Test
    void deleteCandidateSkill_shouldDelete_whenFound() {
        when(candidateSkillRepository.findById(10L)).thenReturn(Optional.of(candidateSkill));

        candidateSkillService.deleteCandidateSkill(10L);

        verify(candidateSkillRepository).delete(candidateSkill);
    }


    @Test
    void getCandidateSkillByCandidateId_shouldThrowException_whenCandidateNotFound() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateSkillService.getCandidateSkillByCandidateId(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateSkillRepository, never()).findByCandidateId(any());
    }

    @Test
    void getCandidateSkillByCandidateId_shouldReturnEmptyList_whenNoSkillsExist() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateSkillRepository.findByCandidateId(1L)).thenReturn(Collections.emptyList());

        List<GetCandidateSkillResponse> result = candidateSkillService.getCandidateSkillByCandidateId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getCandidateSkillByCandidateId_shouldReturnMappedList_whenSkillsExist() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateSkillRepository.findByCandidateId(1L)).thenReturn(List.of(candidateSkill));

        List<GetCandidateSkillResponse> result = candidateSkillService.getCandidateSkillByCandidateId(1L);

        assertEquals(1, result.size());
        GetCandidateSkillResponse response = result.get(0);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getCandidateId());
        assertEquals(skill, response.getSkill());
        assertEquals(3, response.getYearsOfExperience());
    }
}