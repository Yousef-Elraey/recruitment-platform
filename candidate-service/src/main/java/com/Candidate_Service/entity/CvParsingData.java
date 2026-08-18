package com.Candidate_Service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "cv_parsing_data")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CvParsingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "cv_id",
            nullable = false,
            unique = true
    )
    private Cv cv;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "personal_info", columnDefinition = "json")
    private JsonNode personalInfo;

    @Column(name = "summary")
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skills", columnDefinition = "json")
    private JsonNode skills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experience", columnDefinition = "json")
    private JsonNode experience;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "education", columnDefinition = "json")
    private JsonNode education;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "certifications", columnDefinition = "json")
    private JsonNode certifications;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "projects", columnDefinition = "json")
    private JsonNode projects;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "languages", columnDefinition = "json")
    private JsonNode languages;

    private LocalDateTime parsedAt;
}