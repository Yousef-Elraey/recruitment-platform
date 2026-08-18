package com.Candidate_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "experiences")
public class Experience extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", referencedColumnName = "id")
    private Candidate candidate;

    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean current;

    @Column(length = 2000)
    private String description;
}
