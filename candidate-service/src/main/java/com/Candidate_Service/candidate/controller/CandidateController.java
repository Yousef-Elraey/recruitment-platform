package com.Candidate_Service.candidate.controller;

import com.Candidate_Service.candidate.dto.request.CandidateSearchRequest;
import com.Candidate_Service.candidate.dto.request.CreateCandidateRequest;
import com.Candidate_Service.candidate.dto.request.UpdateCandidateRequest;
import com.Candidate_Service.candidate.dto.response.CreateCandidateResponse;
import com.Candidate_Service.candidate.dto.response.GetCandidateResponse;
import com.Candidate_Service.candidate.dto.response.PageResponse;
import com.Candidate_Service.candidate.dto.response.UpdateCandidateResponse;
import com.Candidate_Service.candidate.service.CandidateService;
import com.Candidate_Service.entity.Candidate;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Candidate",description = "manage candidate operation")
@RequestMapping("api/v1/candidates")
@RequiredArgsConstructor
public class CandidateController {
private final CandidateService candidateService;
    @GetMapping
    public ResponseEntity<PageResponse<GetCandidateResponse>> getAllCandidates(@ParameterObject
                                                                                   CandidateSearchRequest searchRequest,

                                                                               @RequestParam(defaultValue = "0")
                                                                                    int page,

                                                                               @RequestParam(defaultValue = "10")
                                                                                    int size,

                                                                               @RequestParam(defaultValue = "createdAt")
                                                                                    String sortBy,

                                                                               @RequestParam(defaultValue = "desc")
                                                                                    String direction){
        return new ResponseEntity(candidateService.getAllCandidates(searchRequest,page,size,sortBy,direction), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<GetCandidateResponse> getCandidateById(@PathVariable Long id){
        return new ResponseEntity<>(candidateService.getCandidateById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CreateCandidateResponse> createCandidate(@Valid @RequestBody CreateCandidateRequest candidateRequest){
        return new ResponseEntity(candidateService.createCandidate(candidateRequest),HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCandidateResponse> updateCandidateData(@Valid @RequestBody UpdateCandidateRequest candidateRequest,
                                                                       @PathVariable Long id){
        return new ResponseEntity(candidateService.updateCandidateDate(candidateRequest,id),HttpStatus.CREATED);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id){
        candidateService.deleteCandidate(id);
        return new ResponseEntity("candidate deleted",HttpStatus.CREATED);
    }
}
