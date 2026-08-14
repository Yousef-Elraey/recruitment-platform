package com.Candidate_Service.skill.service;

import com.Candidate_Service.candidate.dto.response.PageResponse;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.entity.Skill;
import com.Candidate_Service.skill.dto.request.CreateSkillRequest;
import com.Candidate_Service.skill.dto.request.SkillSearchRequest;
import com.Candidate_Service.skill.dto.request.UpdateSkillRequest;
import com.Candidate_Service.skill.dto.response.CreateSkillResponse;
import com.Candidate_Service.skill.dto.response.GetSkillResoponse;
import com.Candidate_Service.skill.dto.response.UpdateSkillResponse;
import com.Candidate_Service.skill.mapper.SkillMapper;
import com.Candidate_Service.skill.repository.SkillRepository;
import com.Candidate_Service.skill.specification.SkillSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public PageResponse<GetSkillResoponse> getAllSkills(SkillSearchRequest searchRequest,
                                                        int page, int size, String sortBy, String direction) {


        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Skill> specification = Specification.unrestricted();

        specification = specification
                .and(SkillSpecification.hasSkillName(searchRequest.getSkillName()));


        Page<Skill> skillPage = skillRepository.findAll(specification, pageable);

        if (skillPage.isEmpty()) {
            return PageResponse.<GetSkillResoponse>builder()
                    .data(new ArrayList<>())
                    .page(skillPage.getNumber())
                    .size(skillPage.getSize())
                    .totalElements(skillPage.getTotalElements())
                    .totalPages(skillPage.getTotalPages())
                    .first(skillPage.isFirst())
                    .last(skillPage.isLast())
                    .build();
        }
        List<Skill> skillList = skillPage.getContent();
        List<GetSkillResoponse> getSkillResoponseList = skillMapper.toGetSkillResponses(skillList);

        return PageResponse.<GetSkillResoponse>builder()
                .data(getSkillResoponseList)
                .page(skillPage.getNumber())
                .size(skillPage.getSize())
                .totalElements(skillPage.getTotalElements())
                .totalPages(skillPage.getTotalPages())
                .first(skillPage.isFirst())
                .last(skillPage.isLast())
                .build();
    }

    public GetSkillResoponse getSkillById(Long id) {
        Optional<Skill> skillOp = skillRepository.findById(id);
        if (skillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.SKILL_NOT_FOUND.name(),
                    "skill with id (" + id + ") not found");
        }
        Skill skill = skillOp.get();
        return skillMapper.toGetSkillResponse(skill);
    }

    public CreateSkillResponse createSkill(CreateSkillRequest request) {
        Skill skill = new Skill();
        skill.setSkillName(request.getSkillName());
        skillRepository.save(skill);

        return skillMapper.toCreateSkillResponse(skill);

    }


    public UpdateSkillResponse updateSkill(UpdateSkillRequest request, Long id) {
        Optional<Skill> skillOp = skillRepository.findById(id);
        if (skillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.SKILL_NOT_FOUND.name(),
                    "skill with id (" + id + ") not found");
        }
        Skill skill = skillOp.get();
        skill.setSkillName(request.getSkillName());
        skillRepository.save(skill);
        return skillMapper.toUpdateSkillResponse(skill);
    }

    public void deleteSkill(Long id) {
        Optional<Skill> skillOp = skillRepository.findById(id);
        if (skillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.SKILL_NOT_FOUND.name(),
                    "skill with id (" + id + ") not found");
        }
        skillRepository.delete(skillOp.get());

    }
}
