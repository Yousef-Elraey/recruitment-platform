package com.Candidate_Service.education.maper;

import com.Candidate_Service.education.dto.request.CreateEducationRequest;
import com.Candidate_Service.education.dto.response.CreateEducationResponse;
import com.Candidate_Service.entity.Education;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    public Education toEducation(CreateEducationRequest request);

    CreateEducationResponse toCreateEducationResponse(Education education);
}
