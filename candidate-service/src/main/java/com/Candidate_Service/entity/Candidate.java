package com.Candidate_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "candidates")

public class Candidate extends BaseEntity{

    @Column(nullable = false, unique = true)
    private Long userId;

    private String phone;

    private String address;

    @Column(length = 2000)
    private String summary;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Experience> experiences;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<CandidateSkill> candidateSkills;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Education> educations;

    @Enumerated(EnumType.STRING)
    private CandidateStatus Status;
}