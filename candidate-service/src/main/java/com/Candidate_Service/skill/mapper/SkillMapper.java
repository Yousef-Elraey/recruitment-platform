package com.Candidate_Service.skill.mapper;

import com.Candidate_Service.entity.Skill;
import com.Candidate_Service.skill.dto.response.CreateSkillResponse;
import com.Candidate_Service.skill.dto.response.GetSkillResoponse;
import com.Candidate_Service.skill.dto.response.UpdateSkillResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    CreateSkillResponse toCreateSkillResponse(Skill skill);

    List<GetSkillResoponse> toGetSkillResponses(List<Skill> skillList);

    GetSkillResoponse toGetSkillResponse(Skill skill);

    UpdateSkillResponse toUpdateSkillResponse(Skill skill);
}
