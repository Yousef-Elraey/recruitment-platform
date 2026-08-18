package com.Candidate_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
@Table(name = "skills")
public class Skill extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String skillName;
}
