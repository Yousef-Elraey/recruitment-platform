package com.Candidate_Service.cv.parsing_data.parsing;

import com.Candidate_Service.cv.repository.CvFileInfoRepository;
import com.Candidate_Service.entity.CvFileInfo;
import com.Candidate_Service.entity.CvStatus;
import com.Candidate_Service.entity.CvVersion;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CvParsingService {
    private final CvFileInfoRepository cvFileInfoRepository;
    private final PdfTextExtractor pdfTextExtractor;
    private final CvAiParser cvAiParser;
    private final CvParsingDataService cvParsingDataService;

    @Value("${app.config.retry-counter}")
    private int maxRetries;

    @Transactional
    public void parseUploadedCvs() {
        Pageable pageable = PageRequest.of(0, 5,
                Sort.by("uploadedAt").ascending());


        Page<CvFileInfo> cvFileInfoPage =
                cvFileInfoRepository.findCvsForParsing(
                        List.of(
                                CvStatus.UPLOADED,
                                CvStatus.PARSING_FAILED
                        ),
                        maxRetries,
                            pageable
                );


        if (cvFileInfoPage.isEmpty()) {
            log.info("No CVs waiting for parsing");
            return;
        }
        log.info(
                "Found {} CV(s) waiting for parsing",
                cvFileInfoPage.getNumberOfElements()
        );
                for (CvFileInfo cvFileInfo : cvFileInfoPage.getContent()) {
                    log.info(
                            "Starting parsing CV with id: {}",
                            cvFileInfo.getId()
                    );
                    parseCv(cvFileInfo);
                }
    }

    private void parseCv(CvFileInfo cvFileInfo) {

        try {
            log.info(
                    "CV {} -> PARSING",
                    cvFileInfo.getId()
            );
            cvFileInfo.setStatus(CvStatus.PARSING);
            cvFileInfoRepository.save(cvFileInfo);

            byte[] cvData = cvFileInfo.getCv().getFileData();

            log.info(
                    "CV {} loaded from database, size={} bytes",
                    cvFileInfo.getId(),
                    cvData.length
            );

            String cvText = pdfTextExtractor.extract(cvData);
            log.info(
                    "CV {} PDF text extracted, length={}",
                    cvFileInfo.getId(),
                    cvText != null ? cvText.length() : 0
            );

            ParsedCvResponse parsedCv =
                    cvAiParser.parse(cvText);
            log.info(
                    "CV {} successfully parsed by AI",
                    cvFileInfo.getId()
            );

            cvParsingDataService.saveParsedData(parsedCv, cvFileInfo);
            log.info(
                    "CV {} parsed data saved",
                    cvFileInfo.getId()
            );
            cvFileInfo.setStatus(CvStatus.PARSED);
            cvFileInfo.setVersion(CvVersion.LATEST);
            cvFileInfoRepository.save(cvFileInfo);
            log.info(
                    "CV {} -> PARSED",
                    cvFileInfo.getId()
            );

        } catch (Exception e) {
            log.error("Failed to parse CV with id: {}",
                    cvFileInfo.getId(),
                    e);
            cvFileInfo.setStatus(CvStatus.PARSING_FAILED);
            cvFileInfo.setRetryCounter(cvFileInfo.getRetryCounter()+1);
            cvFileInfo.setVersion(CvVersion.IGNORE);
            cvFileInfoRepository.save(cvFileInfo);

        }
    }
}
