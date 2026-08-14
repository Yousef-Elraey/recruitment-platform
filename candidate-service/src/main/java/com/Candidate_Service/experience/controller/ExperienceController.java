package com.Candidate_Service.experience.controller;

import com.Candidate_Service.education.dto.request.CreateEducationRequest;
import com.Candidate_Service.education.dto.request.UpdateEducationRequest;
import com.Candidate_Service.education.dto.response.CreateEducationResponse;
import com.Candidate_Service.education.dto.response.GetEducationResponse;
import com.Candidate_Service.education.dto.response.UpdateEducationResponse;
import com.Candidate_Service.education.service.EducationService;
import com.Candidate_Service.experience.dto.request.CreateExperienceRequest;
import com.Candidate_Service.experience.dto.request.UpdateExperienceRequest;
import com.Candidate_Service.experience.dto.response.CreateExperienceResponse;
import com.Candidate_Service.experience.dto.response.GetExperienceResponse;
import com.Candidate_Service.experience.dto.response.UpdateExperienceResponse;
import com.Candidate_Service.experience.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experiences")
@RequiredArgsConstructor
public class ExperienceController {
    private final ExperienceService experienceService;

    @GetMapping
    public ResponseEntity<List<GetExperienceResponse>> getAllExperiences() {
        return new ResponseEntity<>(experienceService.getAllExperiences(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<GetExperienceResponse> getExperienceById(@PathVariable Long id) {
        return new ResponseEntity<>(experienceService.getExperienceById(id), HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<CreateExperienceResponse> createExperience(@RequestBody CreateExperienceRequest request) {
        return new ResponseEntity<>(experienceService.createExperience(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateExperienceResponse> updateExperience(@PathVariable Long id,
                                                                     @RequestBody UpdateExperienceRequest request) {
        return new ResponseEntity<>(experienceService.updateExperience(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long id) {
        experienceService.deleteExperience(id);
        return new ResponseEntity<>("experience deleted", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-candidate/{id}")
    public ResponseEntity<List<GetExperienceResponse>> getAllExperiencesByCandidateId(@PathVariable Long id) {
        return new ResponseEntity<>(experienceService.getAllExperiencesByCandidateId(id), HttpStatus.OK);
    }
}
