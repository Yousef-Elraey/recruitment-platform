package com.Candidate_Service.candidate_skill.controller;

import com.Candidate_Service.candidate.dto.response.GetCandidateResponse;
import com.Candidate_Service.candidate_skill.dto.request.CreateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.request.UpdateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.response.CreateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.GetCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.UpdateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.service.CandidateSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/candidate/skill")
@RequiredArgsConstructor
public class CandidateSkillController {
    private final CandidateSkillService candidateSkillService;

    @GetMapping
    public ResponseEntity<List<GetCandidateSkillResponse>> getAllCandidateSkills() {
        return new ResponseEntity<>(candidateSkillService.getAllCandidateSkills(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCandidateSkillResponse> getCandidateSkillById(@PathVariable Long id) {
        return new ResponseEntity<>(candidateSkillService.getCandidateSkillById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CreateCandidateSkillResponse> createCandidateSkill(@Valid @RequestBody CreateCandidateSkillRequest createCandidateSkillRequest) {
        return new ResponseEntity<>(candidateSkillService.createCandidateSkill(createCandidateSkillRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<UpdateCandidateSkillResponse> updateCandidateSkill(@PathVariable Long id,
                                                                      @RequestBody UpdateCandidateSkillRequest requset) {
        return new ResponseEntity<>(candidateSkillService.updateCandidateSkill(id, requset), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCandidateSkill(@PathVariable Long id) {
        candidateSkillService.deleteCandidateSkill(id);
        return new ResponseEntity<>("candidate skill deleted", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-candidate/{id}")
    public ResponseEntity<List<GetCandidateSkillResponse>> getCandidateSkillByCandidateId(@PathVariable Long id) {
        return new ResponseEntity<>(candidateSkillService.getCandidateSkillByCandidateId(id), HttpStatus.OK);
    }

}
