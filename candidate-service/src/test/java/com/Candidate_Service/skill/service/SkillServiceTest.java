package com.Candidate_Service.skill.service;

import static org.junit.jupiter.api.Assertions.*;

import com.Candidate_Service.candidate.dto.response.PageResponse;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.entity.Skill;
import com.Candidate_Service.skill.dto.request.CreateSkillRequest;
import com.Candidate_Service.skill.dto.request.SkillSearchRequest;
import com.Candidate_Service.skill.dto.request.UpdateSkillRequest;
import com.Candidate_Service.skill.dto.response.CreateSkillResponse;
import com.Candidate_Service.skill.dto.response.GetSkillResoponse;
import com.Candidate_Service.skill.dto.response.UpdateSkillResponse;
import com.Candidate_Service.skill.mapper.SkillMapper;
import com.Candidate_Service.skill.repository.SkillRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private SkillSearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(1L);
        skill.setSkillName("Java");

        searchRequest = new SkillSearchRequest();
    }

    @Test
    void getAllSkills_shouldReturnEmptyData_whenPageIsEmpty() {
        Page<Skill> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(skillRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        PageResponse<GetSkillResoponse> result =
                skillService.getAllSkills(searchRequest, 0, 10, "skillName", "asc");

        assertNotNull(result);
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(skillMapper, never()).toGetSkillResponses(any());
    }

    @Test
    void getAllSkills_shouldReturnMappedData_whenSkillsExist() {
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), PageRequest.of(0, 10), 1);
        List<GetSkillResoponse> mappedResponses = List.of(new GetSkillResoponse());

        when(skillRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(skillPage);
        when(skillMapper.toGetSkillResponses(List.of(skill))).thenReturn(mappedResponses);

        PageResponse<GetSkillResoponse> result =
                skillService.getAllSkills(searchRequest, 0, 10, "skillName", "asc");

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
    void getAllSkills_shouldBuildAscendingSort_whenDirectionIsAsc() {
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), PageRequest.of(1, 5), 10);
        when(skillRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(skillPage);
        when(skillMapper.toGetSkillResponses(any())).thenReturn(List.of(new GetSkillResoponse()));

        skillService.getAllSkills(searchRequest, 1, 5, "skillName", "asc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(skillRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(1, capturedPageable.getPageNumber());
        assertEquals(5, capturedPageable.getPageSize());
        Sort.Order order = capturedPageable.getSort().getOrderFor("skillName");
        assertNotNull(order);
        assertTrue(order.isAscending());
    }

    @Test
    void getAllSkills_shouldBuildDescendingSort_whenDirectionIsDesc() {
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), PageRequest.of(0, 10), 1);
        when(skillRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(skillPage);
        when(skillMapper.toGetSkillResponses(any())).thenReturn(List.of(new GetSkillResoponse()));

        skillService.getAllSkills(searchRequest, 0, 10, "skillName", "DESC");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(skillRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("skillName");
        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    @Test
    void getAllSkills_shouldPassBuiltSpecification_toRepository() {
        searchRequest.setSkillName("Java");

        Page<Skill> skillPage = new PageImpl<>(List.of(skill), PageRequest.of(0, 10), 1);
        when(skillRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(skillPage);
        when(skillMapper.toGetSkillResponses(any())).thenReturn(List.of(new GetSkillResoponse()));

        skillService.getAllSkills(searchRequest, 0, 10, "skillName", "asc");

        ArgumentCaptor<Specification> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(skillRepository).findAll(specCaptor.capture(), any(Pageable.class));

        assertNotNull(specCaptor.getValue());
    }

    @Test
    void getSkillById_shouldThrowException_whenNotFound() {
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> skillService.getSkillById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.SKILL_NOT_FOUND.name(), exception.getErrorCode());
        verify(skillMapper, never()).toGetSkillResponse(any());
    }

    @Test
    void getSkillById_shouldReturnResponse_whenFound() {
        GetSkillResoponse expectedResponse = new GetSkillResoponse();

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillMapper.toGetSkillResponse(skill)).thenReturn(expectedResponse);

        GetSkillResoponse result = skillService.getSkillById(1L);

        assertEquals(expectedResponse, result);
    }


    @Test
    void createSkill_shouldSaveAndReturnResponse() {
        CreateSkillRequest request = new CreateSkillRequest();
        request.setSkillName("Python");

        CreateSkillResponse expectedResponse = new CreateSkillResponse();

        when(skillMapper.toCreateSkillResponse(any(Skill.class))).thenReturn(expectedResponse);

        CreateSkillResponse result = skillService.createSkill(request);

        ArgumentCaptor<Skill> captor = ArgumentCaptor.forClass(Skill.class);
        verify(skillRepository).save(captor.capture());
        assertEquals("Python", captor.getValue().getSkillName());

        assertEquals(expectedResponse, result);
    }

    @Test
    void updateSkill_shouldThrowException_whenNotFound() {
        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setSkillName("Go");

        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> skillService.updateSkill(request, 99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.SKILL_NOT_FOUND.name(), exception.getErrorCode());
        verify(skillRepository, never()).save(any());
        verify(skillMapper, never()).toUpdateSkillResponse(any());
    }

    @Test
    void updateSkill_shouldUpdateNameAndSave_whenFound() {
        UpdateSkillRequest request = new UpdateSkillRequest();
        request.setSkillName("Kotlin");

        UpdateSkillResponse expectedResponse = new UpdateSkillResponse();

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillMapper.toUpdateSkillResponse(skill)).thenReturn(expectedResponse);

        UpdateSkillResponse result = skillService.updateSkill(request, 1L);

        assertEquals("Kotlin", skill.getSkillName());
        verify(skillRepository).save(skill);
        assertEquals(expectedResponse, result);
    }

    @Test
    void deleteSkill_shouldThrowException_whenNotFound() {
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> skillService.deleteSkill(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.SKILL_NOT_FOUND.name(), exception.getErrorCode());
        verify(skillRepository, never()).delete((Skill) any());
    }

    @Test
    void deleteSkill_shouldDelete_whenFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        skillService.deleteSkill(1L);

        verify(skillRepository).delete(skill);
    }
}