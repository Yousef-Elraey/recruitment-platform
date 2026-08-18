package com.Candidate_Service.cv.parsing_data.parsing;

import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfTextExtractor {

    public String extract(byte[] cvData) {
        if (cvData == null || cvData.length == 0) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND,
                    ErrorCode.EMPTY_CV.name(),
                    "cv is empty");
        }

        try (PDDocument document =
                     Loader.loadPDF(cvData)) {

            PDFTextStripper stripper =
                    new PDFTextStripper();

            return stripper.getText(document);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to extract text from CV", e
            );
        }
    }
}
