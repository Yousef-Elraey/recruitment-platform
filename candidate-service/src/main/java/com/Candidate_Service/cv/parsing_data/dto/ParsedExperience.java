package com.Candidate_Service.cv.parsing_data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ParsedExperience {

    private String companyName;
    private String jobTitle;
    private String startDate;
    private String endDate;
    private String description;
}