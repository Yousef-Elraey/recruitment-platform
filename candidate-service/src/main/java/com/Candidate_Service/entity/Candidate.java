package com.Candidate_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Candidate extends BaseEntity{

    @Column(nullable = false, unique = true)
    private Long userId;

    private String phone;

    private String address;

    @Column(length = 2000)
    private String summary;

    @OneToMany(mappedBy = "candidate",cascade = CascadeType.ALL)
    private List<CandidateExperience> experiences;

    @OneToMany(mappedBy = "candidate",cascade = CascadeType.ALL)
    private List<CandidateSkill> skills;
    @OneToMany(mappedBy = "candidate",cascade = CascadeType.ALL)
    private List<CandidateEducation> educations;
    @Enumerated(EnumType.STRING)
    private Status status;
}