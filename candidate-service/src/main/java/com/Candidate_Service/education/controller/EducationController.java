package com.Candidate_Service.education.controller;

import com.Candidate_Service.education.dto.request.CreateEducationRequest;
import com.Candidate_Service.education.dto.request.UpdateEducationRequest;
import com.Candidate_Service.education.dto.response.CreateEducationResponse;
import com.Candidate_Service.education.dto.response.GetEducationResponse;
import com.Candidate_Service.education.dto.response.UpdateEducationResponse;
import com.Candidate_Service.education.service.EducationService;
import com.Candidate_Service.entity.Education;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/educations")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @GetMapping
    public ResponseEntity<List<GetEducationResponse>> getAllEducations() {
        return new ResponseEntity<>(educationService.getAllEducations(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<GetEducationResponse> getEducationById(@PathVariable Long id) {
        return new ResponseEntity<>(educationService.getEducationById(id), HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<CreateEducationResponse> createEducation(@RequestBody CreateEducationRequest request) {
        return new ResponseEntity<>(educationService.createEducation(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateEducationResponse> updateEducation(@PathVariable Long id,
                                                                   @RequestBody UpdateEducationRequest request) {
        return new ResponseEntity<>(educationService.updateEducation(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return new ResponseEntity<>("education deleted", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-candidate/{id}")
    public ResponseEntity<List<GetEducationResponse>> getAllEducationsByCandidateId(@PathVariable Long id) {
        return new ResponseEntity<>(educationService.getAllEducationsByCandidateId(id), HttpStatus.OK);
    }
}
