package com.Candidate_Service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "candidate_skills",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_candidate_skill",
                                  columnNames = {"candidate_id", "skill_id"}
                )
        }
)
@Entity
public class CandidateSkill extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    private Integer yearsOfExperience;
}
