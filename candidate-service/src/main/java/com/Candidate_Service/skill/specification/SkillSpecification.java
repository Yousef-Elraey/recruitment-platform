package com.Candidate_Service.skill.specification;

import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Skill;
import org.springframework.data.jpa.domain.Specification;

public class SkillSpecification {
    public static Specification<Skill> hasSkillName(String skillName) {
        return (root, query, cb) ->
                skillName == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("skillName")),
                        "%" + skillName.toLowerCase() + "%");
    }

}
