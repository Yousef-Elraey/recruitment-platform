package com.application_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class CandidateFace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private Application application;
    @ManyToOne
    @JoinColumn(name = "face_code", referencedColumnName = "faceCode")
    private Face face;
    @CreatedBy
    @Column(updatable = false)
    private String updatedBy;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime updatedAt;
}
