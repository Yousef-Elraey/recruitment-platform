package com.Candidate_Service.cv.controller;

import com.Candidate_Service.cv.dto.response.UploadCvResponse;
import com.Candidate_Service.cv.service.CvService;
import com.Candidate_Service.cv.parsing_data.parsing.ParsedCvResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("api/v1/candidates/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Cv Management", description = "cv management operations")
public class CvController {
    private final CvService cvService;

    @PostMapping(value = "/cv/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload your cv")
    ResponseEntity<UploadCvResponse> uploadCv(@RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(cvService.uploadCv(file), HttpStatus.CREATED);
    }

    @GetMapping("/cv/load")
    @Operation(summary = "Get parsed data")
    ResponseEntity<ParsedCvResponse> loadParsedDataCv() {
        return new ResponseEntity<>(cvService.loadParsedDataCv(), HttpStatus.OK);
    }


}
