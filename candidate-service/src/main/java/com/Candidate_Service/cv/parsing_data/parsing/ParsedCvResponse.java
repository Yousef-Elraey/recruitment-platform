package com.Candidate_Service.cv.parsing_data.parsing;

import com.Candidate_Service.cv.parsing_data.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ParsedCvResponse {
    private PersonalInfo personalInfo;

    private String summary;

    private List<ParsedSkill> skills;

    private List<ParsedExperience> experience;

    private List<ParsedEducation> education;

    private List<ParsedCertification> certifications;

    private List<ParsedProject> projects;

    private List<ParsedLanguage> languages;
}

