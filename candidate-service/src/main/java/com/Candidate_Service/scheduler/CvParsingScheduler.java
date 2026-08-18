package com.Candidate_Service.scheduler;

import com.Candidate_Service.cv.parsing_data.parsing.CvParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CvParsingScheduler {
    private final CvParsingService cvParsingService;

    @Scheduled(fixedDelay = 30000)
    public void parseUploadedCvs() {
        cvParsingService.parseUploadedCvs();

    }
}
