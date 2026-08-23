package com.Candidate_Service.candidate.service;

import com.Candidate_Service.candidate.dto.request.CandidateSearchRequest;
import com.Candidate_Service.candidate.dto.request.CreateCandidateRequest;
import com.Candidate_Service.candidate.dto.request.UpdateCandidateRequest;
import com.Candidate_Service.candidate.dto.response.CreateCandidateResponse;
import com.Candidate_Service.candidate.dto.response.GetCandidateResponse;
import com.Candidate_Service.candidate.dto.response.PageResponse;
import com.Candidate_Service.candidate.dto.response.UpdateCandidateResponse;
import com.Candidate_Service.candidate.mapper.CandidateMapper;
import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.candidate_skill.dto.request.CreateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.response.GetCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.repository.CandidateSkillRepository;
import com.Candidate_Service.candidate_skill.service.CandidateSkillService;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.common.security.AuthenticatedUser;
import com.Candidate_Service.common.security.CurrentUser;
import com.Candidate_Service.cv.parsing_data.dto.ParsedEducation;
import com.Candidate_Service.cv.parsing_data.dto.ParsedExperience;
import com.Candidate_Service.cv.parsing_data.dto.ParsedSkill;
import com.Candidate_Service.cv.parsing_data.parsing.ParsedCvResponse;
import com.Candidate_Service.cv.service.CvService;
import com.Candidate_Service.education.dto.request.CreateEducationRequest;
import com.Candidate_Service.education.dto.response.GetEducationResponse;
import com.Candidate_Service.education.repository.EducationRepository;
import com.Candidate_Service.education.service.EducationService;
import com.Candidate_Service.entity.*;
import com.Candidate_Service.experience.dto.request.CreateExperienceRequest;
import com.Candidate_Service.experience.dto.response.GetExperienceResponse;
import com.Candidate_Service.experience.repository.ExperienceRepository;
import com.Candidate_Service.experience.service.ExperienceService;
import com.Candidate_Service.skill.repository.SkillRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock private CandidateRepository candidateRepository;
    @Mock private CandidateMapper candidateMapper;
    @Mock private ExperienceService experienceService;
    @Mock private EducationService educationService;
    @Mock private CandidateSkillService candidateSkillService;
    @Mock private CvService cvService;
    @Mock private SkillRepository skillRepository;
    @Mock private CandidateSkillRepository candidateSkillRepository;
    @Mock private EducationRepository educationRepository;
    @Mock private ExperienceRepository experienceRepository;

    @InjectMocks
    private CandidateService candidateService;

    private Candidate candidate;
    private CandidateSearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        candidate = new Candidate();
        candidate.setId(1L);
        candidate.setName("John Doe");
        candidate.setUserId(100L);
        candidate.setStatus(CandidateStatus.ACTIVE);

        searchRequest = new CandidateSearchRequest();
    }

    @Test
    void getAllCandidates_shouldReturnEmptyData_whenPageIsEmpty() {
        Page<Candidate> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(candidateRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        PageResponse<GetCandidateResponse> result =
                candidateService.getAllCandidates(searchRequest, 0, 10, "name", "asc");

        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
        assertEquals(0, result.getTotalElements());
        verifyNoInteractions(experienceService, educationService, candidateSkillService);
    }

    @Test
    void getAllCandidates_shouldBuildResponsesWithSubResources_whenPageHasCandidates() {
        Page<Candidate> candidatePage = new PageImpl<>(List.of(candidate), PageRequest.of(0, 10), 1);
        when(candidateRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(candidatePage);

        List<GetExperienceResponse> experiences = List.of(new GetExperienceResponse());
        List<GetCandidateSkillResponse> skills = List.of(new GetCandidateSkillResponse());
        List<GetEducationResponse> educations = List.of(new GetEducationResponse());

        when(experienceService.getAllExperiencesByCandidateId(1L)).thenReturn(experiences);
        when(candidateSkillService.getCandidateSkillByCandidateId(1L)).thenReturn(skills);
        when(educationService.getAllEducationsByCandidateId(1L)).thenReturn(educations);

        PageResponse<GetCandidateResponse> result =
                candidateService.getAllCandidates(searchRequest, 0, 10, "name", "asc");

        assertEquals(1, result.getData().size());
        GetCandidateResponse response = result.getData().get(0);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals(experiences, response.getExperiences());
        assertEquals(skills, response.getCandidateSkills());
        assertEquals(educations, response.getEducations());

        verify(candidateMapper, never()).toGetCandidateResponse(any());
    }

    @Test
    void getAllCandidates_shouldUseDescendingSort_whenDirectionIsDesc() {
        Page<Candidate> candidatePage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(candidateRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(candidatePage);

        candidateService.getAllCandidates(searchRequest, 0, 10, "name", "DESC");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(candidateRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("name");
        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    @Test
    void getCandidateById_shouldThrowException_whenNotFound() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateService.getCandidateById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
    }

    @Test
    void getCandidateById_shouldThrowException_whenCandidateIsDeleted() {
        candidate.setStatus(CandidateStatus.DELETED);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateService.getCandidateById(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_AVAILABLE.name(), exception.getErrorCode());
        verify(candidateMapper, never()).toGetCandidateResponse(any());
    }

    @Test
    void getCandidateById_shouldReturnResponse_whenCandidateIsActive() {
        GetCandidateResponse expectedResponse = new GetCandidateResponse();
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateMapper.toGetCandidateResponse(candidate)).thenReturn(expectedResponse);

        GetCandidateResponse result = candidateService.getCandidateById(1L);

        assertEquals(expectedResponse, result);
    }

    @Test
    void createCandidate_shouldSaveCandidate_withCurrentUserId() {
        CreateCandidateRequest request = new CreateCandidateRequest();
        request.setId(1L);
        request.setName("Jane Doe");
        request.setPhone("12345");
        request.setAddress("Cairo");
        request.setSummary("Experienced dev");

        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(200L);

        CreateCandidateResponse expectedResponse = new CreateCandidateResponse();

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(candidateMapper.toCreateCandidateResponse(any(Candidate.class)))
                    .thenReturn(expectedResponse);

            CreateCandidateResponse result = candidateService.createCandidate(request);

            ArgumentCaptor<Candidate> candidateCaptor = ArgumentCaptor.forClass(Candidate.class);
            verify(candidateRepository).save(candidateCaptor.capture());
            Candidate saved = candidateCaptor.getValue();

            assertEquals(200L, saved.getUserId());
            assertEquals("Jane Doe", saved.getName());
            assertEquals(CandidateStatus.ACTIVE, saved.getStatus());
            assertEquals(expectedResponse, result);
        }
    }

    @Test
    void updateCandidateDate_shouldThrowException_whenNotFound() {
        UpdateCandidateRequest request = new UpdateCandidateRequest();
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateService.updateCandidateDate(request, 99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateRepository, never()).save(any());
    }

    @Test
    void updateCandidateDate_shouldUpdateFieldsAndSave_whenFound() {
        UpdateCandidateRequest request = new UpdateCandidateRequest();
        request.setId(1L);
        request.setName("Updated Name");
        request.setUserId(150L);
        request.setPhone("999");
        request.setAddress("New Address");
        request.setSummary("New summary");
        request.setStatus(CandidateStatus.APPROVED);

        UpdateCandidateResponse expectedResponse = new UpdateCandidateResponse();

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateMapper.toUpdateCandidateResponse(candidate)).thenReturn(expectedResponse);

        UpdateCandidateResponse result = candidateService.updateCandidateDate(request, 1L);

        assertEquals("Updated Name", candidate.getName());
        assertEquals(150L, candidate.getUserId());
        assertEquals("999", candidate.getPhone());
        assertEquals("New Address", candidate.getAddress());
        assertEquals("New summary", candidate.getSummary());
        assertEquals(CandidateStatus.APPROVED, candidate.getStatus());
        assertEquals(expectedResponse, result);

        verify(candidateRepository).save(candidate);
    }

    @Test
    void deleteCandidate_shouldThrowException_whenNotFound() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateService.deleteCandidate(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateRepository, never()).save(any());
    }

    @Test
    void deleteCandidate_shouldSetStatusDeletedAndSave_whenFound() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        candidateService.deleteCandidate(1L);

        assertEquals(CandidateStatus.DELETED, candidate.getStatus());
        verify(candidateRepository).save(candidate);
    }
    @Test
    void approveCandidate_shouldThrowException_whenNotFound() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> candidateService.approveCandidate(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
        verify(candidateRepository, never()).save(any());
    }

    @Test
    void approveCandidate_shouldSetStatusApprovedAndSave_whenFound() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        candidateService.approveCandidate(1L);

        assertEquals(CandidateStatus.APPROVED, candidate.getStatus());
        verify(candidateRepository).save(candidate);
    }

    @Test
    void fillCurrentCandidateData_shouldThrowException_whenNoParsedCvFound() {
        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(100L);

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvService.loadParsedDataCv()).thenReturn(null);

            RecruitmentBusinessException exception = assertThrows(
                    RecruitmentBusinessException.class,
                    () -> candidateService.fillCurrentCandidateData()
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals(ErrorCode.NO_PARSED_DATA_FOUND.name(), exception.getErrorCode());
        }
    }

    @Test
    void fillCurrentCandidateData_shouldCreateNewSkill_whenSkillDoesNotExistYet() {
        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(100L);

        ParsedSkill parsedSkill = new ParsedSkill();
        parsedSkill.setName("Java");

        Skill savedSkill = new Skill();
        savedSkill.setId(5L);
        savedSkill.setSkillName("Java");

        ParsedCvResponse parsedCvResponse = new ParsedCvResponse();
        parsedCvResponse.setSkills(List.of(parsedSkill));
        parsedCvResponse.setEducation(new ArrayList<>());
        parsedCvResponse.setExperience(new ArrayList<>());

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvService.loadParsedDataCv()).thenReturn(parsedCvResponse);

            when(skillRepository.findBySkillName("Java"))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(savedSkill));

            when(candidateSkillRepository.findByCandidateIdAndSkillId(100L, 5L))
                    .thenReturn(Optional.empty());

            candidateService.fillCurrentCandidateData();

            ArgumentCaptor<Skill> skillCaptor = ArgumentCaptor.forClass(Skill.class);
            verify(skillRepository).save(skillCaptor.capture());
            assertEquals("Java", skillCaptor.getValue().getSkillName());

            ArgumentCaptor<CreateCandidateSkillRequest> reqCaptor =
                    ArgumentCaptor.forClass(CreateCandidateSkillRequest.class);
            verify(candidateSkillService).createCandidateSkill(reqCaptor.capture());
            assertEquals(100L, reqCaptor.getValue().getCandidateId());
            assertEquals(5L, reqCaptor.getValue().getSkillId());
        }
    }

    @Test
    void fillCurrentCandidateData_shouldSkipCandidateSkillCreation_whenAlreadyLinked() {
        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(100L);

        ParsedSkill parsedSkill = new ParsedSkill();
        parsedSkill.setName("Java");

        Skill existingSkill = new Skill();
        existingSkill.setId(5L);
        existingSkill.setSkillName("Java");

        ParsedCvResponse parsedCvResponse = new ParsedCvResponse();
        parsedCvResponse.setSkills(List.of(parsedSkill));
        parsedCvResponse.setEducation(new ArrayList<>());
        parsedCvResponse.setExperience(new ArrayList<>());

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvService.loadParsedDataCv()).thenReturn(parsedCvResponse);

            when(skillRepository.findBySkillName("Java")).thenReturn(Optional.of(existingSkill));
            when(candidateSkillRepository.findByCandidateIdAndSkillId(100L, 5L))
                    .thenReturn(Optional.of(new CandidateSkill()));

            candidateService.fillCurrentCandidateData();

            verify(skillRepository, never()).save(any());
            verify(candidateSkillService, never()).createCandidateSkill(any());
        }
    }

    @Test
    void fillCurrentCandidateData_shouldCreateEducation_whenNotAlreadyPresent() {
        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(100L);

        ParsedEducation parsedEducation = new ParsedEducation();
        parsedEducation.setInstitution("MIT");
        parsedEducation.setDegree("BSc");
        parsedEducation.setFieldOfStudy("CS");

        ParsedCvResponse parsedCvResponse = new ParsedCvResponse();
        parsedCvResponse.setSkills(new ArrayList<>());
        parsedCvResponse.setEducation(List.of(parsedEducation));
        parsedCvResponse.setExperience(new ArrayList<>());

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvService.loadParsedDataCv()).thenReturn(parsedCvResponse);
            when(educationRepository.findByCandidateIdAndInstitution(100L, "MIT"))
                    .thenReturn(Optional.empty());

            candidateService.fillCurrentCandidateData();

            ArgumentCaptor<CreateEducationRequest> captor =
                    ArgumentCaptor.forClass(CreateEducationRequest.class);
            verify(educationService).createEducation(captor.capture());
            assertEquals(100L, captor.getValue().getCandidateId());
            assertEquals("MIT", captor.getValue().getInstitution());
        }
    }

    @Test
    void fillCurrentCandidateData_shouldSkipEducation_whenAlreadyExists() {
        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(100L);

        ParsedEducation parsedEducation = new ParsedEducation();
        parsedEducation.setInstitution("MIT");

        ParsedCvResponse parsedCvResponse = new ParsedCvResponse();
        parsedCvResponse.setSkills(new ArrayList<>());
        parsedCvResponse.setEducation(List.of(parsedEducation));
        parsedCvResponse.setExperience(new ArrayList<>());

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvService.loadParsedDataCv()).thenReturn(parsedCvResponse);
            when(educationRepository.findByCandidateIdAndInstitution(100L, "MIT"))
                    .thenReturn(Optional.of(new Education()));

            candidateService.fillCurrentCandidateData();

            verify(educationService, never()).createEducation(any());
        }
    }

    @Test
    void fillCurrentCandidateData_shouldCreateExperience_whenNotAlreadyPresent() {
        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(100L);

        ParsedExperience parsedExperience = new ParsedExperience();
        parsedExperience.setCompanyName("Google");
        parsedExperience.setJobTitle("Engineer");

        ParsedCvResponse parsedCvResponse = new ParsedCvResponse();
        parsedCvResponse.setSkills(new ArrayList<>());
        parsedCvResponse.setEducation(new ArrayList<>());
        parsedCvResponse.setExperience(List.of(parsedExperience));

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvService.loadParsedDataCv()).thenReturn(parsedCvResponse);
            when(experienceRepository.findByCandidateIdAndCompanyName(100L, "Google"))
                    .thenReturn(Optional.empty());

            candidateService.fillCurrentCandidateData();

            ArgumentCaptor<CreateExperienceRequest> captor =
                    ArgumentCaptor.forClass(CreateExperienceRequest.class);
            verify(experienceService).createExperience(captor.capture());
            assertEquals(100L, captor.getValue().getCandidateId());
            assertEquals("Google", captor.getValue().getCompanyName());
        }
    }

    @Test
    void fillCurrentCandidateData_shouldSkipExperience_whenAlreadyExists() {
        AuthenticatedUser currentUser = new AuthenticatedUser();
        currentUser.setUserId(100L);

        ParsedExperience parsedExperience = new ParsedExperience();
        parsedExperience.setCompanyName("Google");

        ParsedCvResponse parsedCvResponse = new ParsedCvResponse();
        parsedCvResponse.setSkills(new ArrayList<>());
        parsedCvResponse.setEducation(new ArrayList<>());
        parsedCvResponse.setExperience(List.of(parsedExperience));

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvService.loadParsedDataCv()).thenReturn(parsedCvResponse);
            when(experienceRepository.findByCandidateIdAndCompanyName(100L, "Google"))
                    .thenReturn(Optional.of(new Experience()));

            candidateService.fillCurrentCandidateData();

            verify(experienceService, never()).createExperience(any());
        }
    }
}