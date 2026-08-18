package com.Candidate_Service.cv.parsing_data.parsing;

import com.Candidate_Service.cv.parsing_data.dto.*;
import com.Candidate_Service.entity.CvFileInfo;
import com.Candidate_Service.entity.CvParsingData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CvParsingDataService {

    private final CvParsingDataRepository cvParsingDataRepository;
    private final ObjectMapper objectMapper;

    public void saveParsedData(ParsedCvResponse parsedCv,
                               CvFileInfo cvFileInfo) {

        CvParsingData parsingData = new CvParsingData();
        parsingData.setCv(cvFileInfo.getCv())
                .setPersonalInfo(objectMapper.valueToTree(parsedCv.getPersonalInfo()))
                .setSummary(parsedCv.getSummary())
                .setSkills(objectMapper.valueToTree(parsedCv.getSkills()))
                .setExperience(objectMapper.valueToTree(parsedCv.getExperience()))
                .setEducation(objectMapper.valueToTree(parsedCv.getEducation()))
                .setCertifications(objectMapper.valueToTree(parsedCv.getCertifications()))
                .setProjects(objectMapper.valueToTree(parsedCv.getProjects()))
                .setLanguages(objectMapper.valueToTree(parsedCv.getLanguages()))
                .setParsedAt(LocalDateTime.now());
        cvParsingDataRepository.save(parsingData);
    }

    public ParsedCvResponse loadParsedCv(CvParsingData cvParsingData){
        ParsedCvResponse parsedCvResponse = new ParsedCvResponse();
        parsedCvResponse.setPersonalInfo(objectMapper.convertValue(cvParsingData.getPersonalInfo(), PersonalInfo.class))
                .setSummary(cvParsingData.getSummary())
                .setSkills(objectMapper.convertValue(cvParsingData.getSkills(), new TypeReference<List<ParsedSkill>>() {}))
                .setExperience(objectMapper.convertValue(cvParsingData.getExperience(), new TypeReference<List<ParsedExperience>>() {}))
                .setEducation(objectMapper.convertValue(cvParsingData.getEducation(), new TypeReference<List<ParsedEducation>>() {}))
                .setCertifications(objectMapper.convertValue(cvParsingData.getCertifications(), new TypeReference<List<ParsedCertification>>() {}))
                .setProjects(objectMapper.convertValue(cvParsingData.getProjects(), new TypeReference<List<ParsedProject>>() {}))
                .setLanguages(objectMapper.convertValue(cvParsingData.getLanguages(), new TypeReference<List<ParsedLanguage>>() {}));

        return parsedCvResponse;
    }


}