package com.Candidate_Service.cv.parsing_data.ollama;

import com.Candidate_Service.cv.parsing_data.parsing.ParsedCvResponse;
import com.Candidate_Service.cv.parsing_data.parsing.CvAiParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OllamaCvParser implements CvAiParser {

    private final ObjectMapper objectMapper;

    private final RestClient restClient =
            RestClient.builder()
                    .baseUrl("http://localhost:11434")
                    .build();

    @Override
    public ParsedCvResponse parse(String cvText) {

        String prompt = """
                You are a CV parsing AI.

                Extract ONLY information that actually exists in the CV.
                DO NOT invent information.
                If information is missing, use null or an empty array.

                Return ONLY valid JSON.
                Do not use Markdown.
                Do not wrap the JSON in ```json.
                Do not add explanations before or after the JSON.

                IMPORTANT:
                The JSON structure MUST exactly match this schema:

                {
                  "personalInfo": {
                    "name": "string or null",
                    "phone": "string or null",
                    "email": "string or null",
                    "linkedIn": "string or null",
                    "github": "string or null"
                  },

                  "summary": "string or null",

                  "skills": [
                    {
                      "name": "string",
                      "level": "string or null"
                    }
                  ],

                  "experience": [
                    {
                      "companyName": "string",
                      "jobTitle": "string",
                      "startDate": "string or null",
                      "endDate": "string or null",
                      "description": "string or null"
                    }
                  ],

                  "education": [
                    {
                      "institution": "string",
                      "degree": "string or null",
                      "fieldOfStudy": "string or null",
                      "startDate": "string or null",
                      "endDate": "string or null"
                    }
                  ],

                  "certifications": [
                    {
                      "name": "string",
                      "issuer": "string or null",
                      "issueDate": "string or null"
                    }
                  ],

                  "projects": [
                    {
                      "name": "string",
                      "description": "string or null",
                      "technologies": []
                    }
                  ],

                  "languages": [
                    {
                      "language": "string",
                      "proficiency": "string or null"
                    }
                  ]
                }

                IMPORTANT RULES FOR SKILLS:
                - skills MUST always be an array of JSON objects.
                - NEVER return skills as an array of strings.
                - Each skill object MUST contain "name".
                - "level" should be null if the CV does not specify a skill level.

                IMPORTANT RULES FOR LANGUAGES:
                - languages MUST always be an array of JSON objects.
                - NEVER return languages as an array of strings.
                - Each language object MUST contain "language".
                - "proficiency" should be null if the CV does not specify proficiency.
                        
                        NEVER return skills as strings.
                                
                        WRONG:
                        "skills": ["Java", "Spring Boot"]
                                
                        CORRECT:
                        "skills": [
                            {
                                "name": "Java",
                                "level": null
                            },
                            {
                                "name": "Spring Boot",
                                "level": null
                            }
                        ]
                                
                        NEVER return languages as strings.
                                
                        WRONG:
                        "languages": ["Arabic", "English"]
                                
                        CORRECT:
                        "languages": [
                            {
                                "language": "Arabic",
                                "proficiency": "Native"
                            },
                            {
                                "language": "English",
                                "proficiency": "Very Good"
                            }
                        ]

                CV:
                %s
                """.formatted(cvText);

        OllamaChatRequest request =
                OllamaChatRequest.builder()
                        .model("qwen2.5:3b")
                        .messages(List.of(
                                OllamaMessage.builder()
                                        .role("user")
                                        .content(prompt)
                                        .build()
                        ))
                        .stream(false)
                        .format("json")
                        .build();

        OllamaChatResponse response =
                restClient.post()
                        .uri("/api/chat")
                        .body(request)
                        .retrieve()
                        .body(OllamaChatResponse.class);

        if (response == null ||
                response.getMessage() == null ||
                response.getMessage().getContent() == null) {

            throw new IllegalStateException(
                    "Empty response received from Ollama"
            );
        }

        String json =
                response.getMessage().getContent();

        System.out.println("Ollama JSON response:");
        System.out.println(json);

        try {

            return objectMapper.readValue(
                    json,
                    ParsedCvResponse.class
            );

        } catch (JsonProcessingException e) {

            e.printStackTrace();

            throw new IllegalStateException(
                    "Failed to convert Ollama response to ParsedCvResponse",
                    e
            );
        }
    }
}