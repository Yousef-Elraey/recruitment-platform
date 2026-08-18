package com.Candidate_Service.skill.controller;

import com.Candidate_Service.candidate.dto.request.CandidateSearchRequest;
import com.Candidate_Service.candidate.dto.response.PageResponse;
import com.Candidate_Service.skill.dto.request.CreateSkillRequest;
import com.Candidate_Service.skill.dto.request.SkillSearchRequest;
import com.Candidate_Service.skill.dto.request.UpdateSkillRequest;
import com.Candidate_Service.skill.dto.response.CreateSkillResponse;
import com.Candidate_Service.skill.dto.response.GetSkillResoponse;
import com.Candidate_Service.skill.dto.response.UpdateSkillResponse;
import com.Candidate_Service.skill.service.SkillService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")

public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<PageResponse<GetSkillResoponse>> getAllSkills(@ParameterObject
                                                                        SkillSearchRequest searchRequest,

                                                                        @RequestParam(defaultValue = "0")
                                                                        int page,

                                                                        @RequestParam(defaultValue = "10")
                                                                        int size,

                                                                        @RequestParam(defaultValue = "createdAt")
                                                                        String sortBy,

                                                                        @RequestParam(defaultValue = "desc")
                                                                        String direction) {
        return new ResponseEntity<>(skillService.getAllSkills(searchRequest, page, size, sortBy, direction), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetSkillResoponse> getSkillById(@PathVariable Long id) {
        return new ResponseEntity<>(skillService.getSkillById(id), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<CreateSkillResponse> createSkill(@RequestBody CreateSkillRequest request) {
        return new ResponseEntity<>(skillService.createSkill(request), HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateSkillResponse> updateSkill(@RequestBody UpdateSkillRequest request,
                                                           @PathVariable Long id) {
        return new ResponseEntity<>(skillService.updateSkill(request, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return new ResponseEntity<>("skill deleted", HttpStatus.NO_CONTENT);
    }
}
