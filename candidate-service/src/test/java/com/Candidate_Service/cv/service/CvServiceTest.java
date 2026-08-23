package com.Candidate_Service.cv.service;

import static org.junit.jupiter.api.Assertions.*;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.common.security.AuthenticatedUser;
import com.Candidate_Service.common.security.CurrentUser;
import com.Candidate_Service.cv.dto.response.UploadCvResponse;
import com.Candidate_Service.cv.parsing_data.parsing.CvParsingDataRepository;
import com.Candidate_Service.cv.parsing_data.parsing.CvParsingDataService;
import com.Candidate_Service.cv.parsing_data.parsing.ParsedCvResponse;
import com.Candidate_Service.cv.repository.CvFileInfoRepository;
import com.Candidate_Service.cv.repository.CvRepository;
import com.Candidate_Service.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CvServiceTest {

    @Mock
    private CvRepository cvRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CvFileInfoRepository cvFileInfoRepository;

    @Mock
    private CvParsingDataRepository cvParsingDataRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CvParsingDataService cvParsingDataService;

    @InjectMocks
    private CvService cvService;

    private Candidate candidate;
    private AuthenticatedUser currentUser;

    @BeforeEach
    void setUp() {
        candidate = new Candidate();
        candidate.setId(1L);

        currentUser = new AuthenticatedUser();
        currentUser.setUserId(1L);
    }

    @Test
    void uploadCv_shouldThrowException_whenFileIsNull() {
        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> cvService.uploadCv(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ErrorCode.EMPTY_CV.name(), exception.getErrorCode());
        verifyNoInteractions(candidateRepository, cvRepository, cvFileInfoRepository);
    }

    @Test
    void uploadCv_shouldThrowException_whenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[0]);

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> cvService.uploadCv(emptyFile)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ErrorCode.EMPTY_CV.name(), exception.getErrorCode());
    }

    @Test
    void uploadCv_shouldThrowException_whenFileExceedsMaxSize() {
        byte[] bigContent = new byte[6 * 1024 * 1024]; // 6 MB
        MultipartFile bigFile = new MockMultipartFile("file", "cv.pdf", "application/pdf", bigContent);

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> cvService.uploadCv(bigFile)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ErrorCode.CV_TOO_LARGE.name(), exception.getErrorCode());
    }

    @Test
    void uploadCv_shouldThrowException_whenContentTypeIsNotPdf() {
        MultipartFile wrongTypeFile = new MockMultipartFile(
                "file", "cv.docx", "application/msword", "some content".getBytes());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> cvService.uploadCv(wrongTypeFile)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ErrorCode.INVALID_CV_TYPE.name(), exception.getErrorCode());
    }

    @Test
    void uploadCv_shouldThrowException_whenCandidateNotFound() {
        MultipartFile validFile = new MockMultipartFile(
                "file", "cv.pdf", "application/pdf", "pdf content".getBytes());

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(candidateRepository.findById(1L)).thenReturn(Optional.empty());

            RecruitmentBusinessException exception = assertThrows(
                    RecruitmentBusinessException.class,
                    () -> cvService.uploadCv(validFile)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.name(), exception.getErrorCode());
            verify(cvRepository, never()).save(any());
        }
    }


    @Test
    void uploadCv_shouldSaveCvAndFileInfo_whenNoPreviousCvExists() {
        MultipartFile validFile = new MockMultipartFile(
                "file", "cv.pdf", "application/pdf", "pdf content".getBytes());

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
            when(cvFileInfoRepository.findAllByCandidateId(1L)).thenReturn(List.of());

            UploadCvResponse response = cvService.uploadCv(validFile);

            verify(cvRepository).save(any(Cv.class));

            ArgumentCaptor<CvFileInfo> captor = ArgumentCaptor.forClass(CvFileInfo.class);
            verify(cvFileInfoRepository).save(captor.capture());
            CvFileInfo savedInfo = captor.getValue();

            assertEquals(candidate, savedInfo.getCandidate());
            assertEquals("cv.pdf", savedInfo.getFileName());
            assertEquals("application/pdf", savedInfo.getFileType());
            assertEquals(CvStatus.UPLOADED, savedInfo.getStatus());
            assertEquals(CvVersion.LATEST, savedInfo.getVersion());
            assertEquals(0L, savedInfo.getRetryCounter());

            assertNotNull(response);
            assertEquals(savedInfo.getFileName(), response.getFileName());
            assertEquals(savedInfo.getFileType(), response.getFileType());
            assertEquals(CvStatus.UPLOADED, response.getStatus());
            assertEquals(CvVersion.LATEST, response.getVersion());
        }
    }

    @Test
    void uploadCv_shouldDemotePreviousLatestVersions_whenOlderCvFileInfoExists() {
        MultipartFile validFile = new MockMultipartFile(
                "file", "cv.pdf", "application/pdf", "pdf content".getBytes());

        CvFileInfo oldLatest = new CvFileInfo();
        oldLatest.setVersion(CvVersion.LATEST);

        CvFileInfo alreadyPrevious = new CvFileInfo();
        alreadyPrevious.setVersion(CvVersion.PREVIOUS);

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
            when(cvFileInfoRepository.findAllByCandidateId(1L))
                    .thenReturn(List.of(oldLatest, alreadyPrevious));

            cvService.uploadCv(validFile);

            assertEquals(CvVersion.PREVIOUS, oldLatest.getVersion());
            assertEquals(CvVersion.PREVIOUS, alreadyPrevious.getVersion());

            verify(cvFileInfoRepository).save(any(CvFileInfo.class));
        }
    }

    @Test
    void uploadCv_shouldThrowInternalServerError_whenIOExceptionOccurs() throws IOException {
        MultipartFile brokenFile = mock(MultipartFile.class);
        when(brokenFile.isEmpty()).thenReturn(false);
        when(brokenFile.getSize()).thenReturn(1000L);
        when(brokenFile.getContentType()).thenReturn("application/pdf");
        when(brokenFile.getBytes()).thenThrow(new IOException("disk error"));

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            RecruitmentBusinessException exception = assertThrows(
                    RecruitmentBusinessException.class,
                    () -> cvService.uploadCv(brokenFile)
            );

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
            assertEquals(ErrorCode.CV_UPLOADED_FAILED.name(), exception.getErrorCode());
            verify(cvRepository, never()).save(any());
        }
    }

    @Test
    void loadParsedDataCv_shouldThrowException_whenNoCvFileInfoFound() {
        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvFileInfoRepository.findByCandidateIdAndVersion(1L, CvVersion.LATEST))
                    .thenReturn(Optional.empty());

            RecruitmentBusinessException exception = assertThrows(
                    RecruitmentBusinessException.class,
                    () -> cvService.loadParsedDataCv()
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals(ErrorCode.CV_NOT_FOUND.name(), exception.getErrorCode());
            verify(cvParsingDataRepository, never()).findByCvId(any());
        }
    }

    @Test
    void loadParsedDataCv_shouldThrowException_whenNoParsingDataFound() {
        Cv cv = new Cv();
        cv.setId(5L);

        CvFileInfo cvFileInfo = new CvFileInfo();
        cvFileInfo.setCv(cv);

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvFileInfoRepository.findByCandidateIdAndVersion(1L, CvVersion.LATEST))
                    .thenReturn(Optional.of(cvFileInfo));
            when(cvParsingDataRepository.findByCvId(5L)).thenReturn(Optional.empty());

            RecruitmentBusinessException exception = assertThrows(
                    RecruitmentBusinessException.class,
                    () -> cvService.loadParsedDataCv()
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals(ErrorCode.CV_PARSING_DATA_NOT_FOUND.name(), exception.getErrorCode());
            verify(cvParsingDataService, never()).loadParsedCv(any());
        }
    }

    @Test
    void loadParsedDataCv_shouldReturnParsedResponse_whenDataFound() {
        Cv cv = new Cv();
        cv.setId(5L);

        CvFileInfo cvFileInfo = new CvFileInfo();
        cvFileInfo.setCv(cv);

        CvParsingData cvParsingData = new CvParsingData();
        ParsedCvResponse expectedResponse = new ParsedCvResponse();

        try (MockedStatic<CurrentUser> mockedStatic = mockStatic(CurrentUser.class)) {
            mockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
            when(cvFileInfoRepository.findByCandidateIdAndVersion(1L, CvVersion.LATEST))
                    .thenReturn(Optional.of(cvFileInfo));
            when(cvParsingDataRepository.findByCvId(5L)).thenReturn(Optional.of(cvParsingData));
            when(cvParsingDataService.loadParsedCv(cvParsingData)).thenReturn(expectedResponse);

            ParsedCvResponse result = cvService.loadParsedDataCv();

            assertEquals(expectedResponse, result);
        }
    }
}