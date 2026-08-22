package com.application_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Application extends BaseEntity {

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @ManyToOne
    @JoinColumn(name = "face_code", referencedColumnName = "faceCode")
    Face face;

    @OneToMany(mappedBy = "application")
    private List<CandidateFace> faceHistory;

    @Column(name = "recruiter_id")
    private Long recruiterId;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "score")
    private Float score;
}
