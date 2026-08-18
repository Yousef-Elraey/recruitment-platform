package com.Candidate_Service.cv.service;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.common.security.AuthenticatedUser;
import com.Candidate_Service.common.security.CurrentUser;
import com.Candidate_Service.cv.dto.response.UploadCvResponse;
import com.Candidate_Service.cv.repository.CvFileInfoRepository;
import com.Candidate_Service.cv.repository.CvRepository;
import com.Candidate_Service.cv.parsing_data.parsing.CvParsingDataRepository;
import com.Candidate_Service.cv.parsing_data.parsing.CvParsingDataService;
import com.Candidate_Service.cv.parsing_data.parsing.ParsedCvResponse;
import com.Candidate_Service.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CvService {
    private final CvRepository cvRepository;
    private final CandidateRepository candidateRepository;
    private final CvFileInfoRepository cvFileInfoRepository;
    private final CvParsingDataRepository cvParsingDataRepository;
    private final ObjectMapper objectMapper;
    private final CvParsingDataService cvParsingDataService;


    public UploadCvResponse uploadCv(MultipartFile file) {
        validateCv(file);

        AuthenticatedUser currentUser = CurrentUser.getCurrentUser();
        Long candidateId = currentUser.getUserId();
        Optional<Candidate> candidateOp = candidateRepository.findById(candidateId);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                    ErrorCode.CANDIDATE_NOT_FOUND.name(), "candidate with id (" + candidateId + ") not found");
        }
        Candidate candidate = candidateOp.get();
        Cv cv = new Cv();
        CvFileInfo cvFileInfo = new CvFileInfo();
        try {
            cv.setFileData(file.getBytes());
            cvRepository.save(cv);

            cvFileInfo
                    .setCandidate(candidate)
                    .setCv(cv)
                    .setFileName(file.getOriginalFilename())
                    .setFileType(file.getContentType())
                    .setFileSize(file.getSize())
                    .setUploadedAt(LocalDateTime.now())
                    .setStatus(CvStatus.UPLOADED)
                    .setRetryCounter(0L)
                    .setVersion(CvVersion.LATEST);

            List<CvFileInfo> cvFileInfoList = cvFileInfoRepository.findAllByCandidateId(candidateId);

            if (!cvFileInfoList.isEmpty()) {
                for (CvFileInfo cvfi : cvFileInfoList) {
                if (cvfi.getVersion().equals(CvVersion.LATEST)) {
                        cvfi.setVersion(CvVersion.PREVIOUS);
                    }
                }

            }

            cvFileInfoRepository.save(cvFileInfo);

            UploadCvResponse response = new UploadCvResponse();
            response.setCvId(cv.getId())
                    .setCvInfoId(cvFileInfo.getId())
                    .setFileName(cvFileInfo.getFileName())
                    .setFileType(cvFileInfo.getFileType())
                    .setFileSize(cvFileInfo.getFileSize())
                    .setUploadedAt(cvFileInfo.getUploadedAt())
                    .setStatus(cvFileInfo.getStatus())
                    .setVersion(cvFileInfo.getVersion());

            return response;
        } catch (IOException e) {
            throw new RecruitmentBusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.CV_UPLOADED_FAILED.name(),
                    "failed to upload cv");
        }


    }

    private void validateCv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RecruitmentBusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.EMPTY_CV.name(),
                    "cv file is required");
        }

        final long maxFileSize = 5 * 1024 * 1024; // 5 MB

        if (file.getSize() > maxFileSize) {
            throw new RecruitmentBusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.CV_TOO_LARGE.name(),
                    "CV file size must not exceed 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (!"application/pdf".equalsIgnoreCase(contentType)) {
            throw new RecruitmentBusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.INVALID_CV_TYPE.name(),
                    "Only PDF files are allowed"
            );
        }
    }

    public ParsedCvResponse loadParsedDataCv() {
        AuthenticatedUser authenticatedUser = CurrentUser.getCurrentUser();
        Long candidateId = authenticatedUser.getUserId();
        Optional<CvFileInfo> cvFileInfoOp = cvFileInfoRepository.findByCandidateIdAndVersion(candidateId, CvVersion.LATEST);
        if (cvFileInfoOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                    ErrorCode.CV_NOT_FOUND.name(),
                    "candidate with id (" + candidateId + ") not found");
        }

        Cv cv = cvFileInfoOp.get().getCv();
        Optional<CvParsingData> cvParsingDataOp = cvParsingDataRepository.findByCvId(cv.getId());
        if (cvParsingDataOp.isEmpty()){
            throw new RecruitmentBusinessException(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.CV_PARSING_DATA_NOT_FOUND.name(),
                    "No parsed data found for CV with id: " + cv.getId()
            );
        }
       CvParsingData cvParsingData = cvParsingDataOp.get();


       ParsedCvResponse response = cvParsingDataService.loadParsedCv(cvParsingData);
        return response;
    }
}
