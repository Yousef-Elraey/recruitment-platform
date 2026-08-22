package com.application_service.application.controller;

import com.application_service.application.dto.request.AddFeedbackScoreRequest;
import com.application_service.application.dto.request.AssignRecruiterIdRequest;
import com.application_service.application.dto.request.CreateApplicationRequest;
import com.application_service.application.dto.request.NextFaceRequest;
import com.application_service.application.dto.response.AddFeedbackScoreResponse;
import com.application_service.application.dto.response.AssignRecruiterIdResponse;
import com.application_service.application.dto.response.CreateApplicationResponse;
import com.application_service.application.service.ApplicationService;
import com.application_service.candidate_face.dto.response.GetCandidateFaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applications")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Application", description = "manage application operations")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "create an application")
    @PreAuthorize("hasAnyRole('ADMIN','CANDIDATE')")
    ResponseEntity<CreateApplicationResponse> createApplication(@RequestBody CreateApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(request));

    }

    @PatchMapping("/assign-recruiter")
    @Operation(summary = "assign recruiter")
    @PreAuthorize("hasAnyRole('ADMIN','HR','INTERVIEWER')")
    public ResponseEntity<AssignRecruiterIdResponse> assignRecruiterId(@RequestBody AssignRecruiterIdRequest request) {
        return new ResponseEntity<>(applicationService.assignRecruiterId(request), HttpStatus.OK);
    }

    @PatchMapping("/final-feedback")
    @Operation(summary = "add feedback and score")
    @PreAuthorize("hasAnyRole('ADMIN','HR','INTERVIEWER')")
    public ResponseEntity<AddFeedbackScoreResponse> addFinalFeedbackScore(@RequestBody AddFeedbackScoreRequest request) {
        return new ResponseEntity<>(applicationService.addFinalFeedbackScore(request), HttpStatus.OK);

    }

    @PatchMapping("/next-face")
    @Operation(summary = "move to next face")
    @PreAuthorize("hasAnyRole('ADMIN','HR','INTERVIEWER')")
    public ResponseEntity<GetCandidateFaceResponse> nextFace(@RequestBody NextFaceRequest request) {
        return new ResponseEntity<>(applicationService.nextFace(request), HttpStatus.OK);

    }
    @PatchMapping("/reject/{applicationId}")
    @Operation(summary = "reject an application")
    @PreAuthorize("hasAnyRole('ADMIN','HR','INTERVIEWER')")
    public ResponseEntity<String> rejectApplication(@PathVariable Long applicationId){
        applicationService.rejectApplication(applicationId);
        return new ResponseEntity<>("sorry, your application rejected",HttpStatus.OK);
    }
}
