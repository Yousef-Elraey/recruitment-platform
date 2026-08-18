package com.Candidate_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "candidateSkills")
public class CandidateSkill extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "candidate_id", referencedColumnName = "id")
    private Candidate candidate;
    @ManyToOne
    @JoinColumn(name = "skill_id", referencedColumnName = "id")
    private Skill skill;
    private Integer yearsOfExperience;
}
