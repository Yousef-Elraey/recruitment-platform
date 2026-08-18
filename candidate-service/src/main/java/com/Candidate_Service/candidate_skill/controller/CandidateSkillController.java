package com.Candidate_Service.candidate_skill.controller;

import com.Candidate_Service.candidate.dto.response.GetCandidateResponse;
import com.Candidate_Service.candidate_skill.dto.request.CreateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.request.UpdateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.response.CreateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.GetCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.UpdateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.service.CandidateSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/candidate/skill")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Candidate-skills", description = "manage skills belongs to a candidate")
public class CandidateSkillController {
    private final CandidateSkillService candidateSkillService;

    @PreAuthorize("hasAnyRole('ADMIN','HR','CANDIDATE')")
    @Operation(summary = "Return all candidate-skills")
    @GetMapping
    public ResponseEntity<List<GetCandidateSkillResponse>> getAllCandidateSkills() {
        return new ResponseEntity<>(candidateSkillService.getAllCandidateSkills(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','CANDIDATE')")
    @Operation(summary = "Return candidate-skill by id")
    @GetMapping("/{id}")
    public ResponseEntity<GetCandidateSkillResponse> getCandidateSkillById(@PathVariable Long id) {
        return new ResponseEntity<>(candidateSkillService.getCandidateSkillById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CANDIDATE')")
    @Operation(summary = "add a candidate-skill")
    @PostMapping
    public ResponseEntity<CreateCandidateSkillResponse> createCandidateSkill(@Valid @RequestBody CreateCandidateSkillRequest createCandidateSkillRequest) {
        return new ResponseEntity<>(candidateSkillService.createCandidateSkill(createCandidateSkillRequest), HttpStatus.CREATED);
    }
    @PreAuthorize("hasAnyRole('ADMIN','CANDIDATE')")
    @Operation(summary = "update candidate skill data")
    @PutMapping("/{id}")
    ResponseEntity<UpdateCandidateSkillResponse> updateCandidateSkill(@PathVariable Long id,
                                                                      @RequestBody UpdateCandidateSkillRequest requset) {
        return new ResponseEntity<>(candidateSkillService.updateCandidateSkill(id, requset), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CANDIDATE')")
    @Operation(summary = "delete candidate-skill")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCandidateSkill(@PathVariable Long id) {
        candidateSkillService.deleteCandidateSkill(id);
        return new ResponseEntity<>("candidate skill deleted", HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','CANDIDATE')")
    @Operation(summary = "Return candidate-skill by candidate_id")
    @GetMapping("/by-candidate/{id}")
    public ResponseEntity<List<GetCandidateSkillResponse>> getCandidateSkillByCandidateId(@PathVariable Long id) {
        return new ResponseEntity<>(candidateSkillService.getCandidateSkillByCandidateId(id), HttpStatus.OK);
    }

}
