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
public class PersonalInfo {

    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String linkedin;
    private String github;
}