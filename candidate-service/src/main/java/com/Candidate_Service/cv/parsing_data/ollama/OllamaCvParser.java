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

        String prompt = "\n" +
                "                You are a CV parsing AI.\n" +
                "\n" +
                "                Extract ONLY information that actually exists in the CV.\n" +
                "                DO NOT invent information.\n" +
                "                If information is missing, use null or an empty array.\n" +

                "  IMPORTANT RULES FOR DATES:\n" +
                "\n" +
                "- All fields named \"startDate\" and \"endDate\" MUST be returned as JSON strings using the exact format \"YYYY-MM-DD\".\n" +
                "- These dates are intended to be deserialized into Java LocalDate.\n" +
                "- NEVER return dates in another format.\n" +
                "- NEVER return a date containing a time.\n" +
                "- NEVER return a date containing a timezone.\n" +
                "- NEVER return values such as \"June 2024\", \"06/2024\", \"2024\", \"Present\", or \"Current\" for startDate or endDate.\n" +
                "- If the CV provides an exact day, convert it to \"YYYY-MM-DD\".\n" +
                "- If the CV does NOT provide enough information to determine an exact day, return null.\n" +
                "- If an experience or education is still ongoing and the CV says \"Present\", \"Current\", or equivalent, set endDate to null.\n" +
                "- Do NOT invent a day, month, or year.\n" +
                "- If the date is missing, return null.\n" +
                "\n" +
                "CORRECT:\n" +
                "\"startDate\": \"2023-09-15\",\n" +
                "\"endDate\": \"2025-06-30\"\n" +
                "\n" +
                "CORRECT:\n" +
                "\"startDate\": null,\n" +
                "\"endDate\": null\n" +
                "\n" +
                "WRONG:\n" +
                "\"startDate\": \"September 2023\"\n" +
                "\n" +
                "WRONG:\n" +
                "\"startDate\": \"09/2023\"\n" +
                "\n" +
                "WRONG:\n" +
                "\"startDate\": \"2023\"\n" +
                "\n" +
                "WRONG:\n" +
                "\"endDate\": \"Present\"\n" +
                "\n" +
                "WRONG:\n" +
                "\"endDate\": \"2025-06-30T00:00:00\"\n" +
                "\n" +
                "IMPORTANT:\n" +
                "The JSON itself must contain date strings in \"YYYY-MM-DD\" format\n"+

                "\n" +
                "                Return ONLY valid JSON.\n" +
                "                Do not use Markdown.\n" +
                "                Do not wrap the JSON in ```json.\n" +
                "                Do not add explanations before or after the JSON.\n" +
                "\n" +
                "                IMPORTANT:\n" +
                "                The JSON structure MUST exactly match this schema:\n" +
                "\n" +
                "                {\n" +
                "                  \"personalInfo\": {\n" +
                "                    \"name\": \"string or null\",\n" +
                "                    \"phone\": \"string or null\",\n" +
                "                    \"email\": \"string or null\",\n" +
                "                    \"linkedIn\": \"string or null\",\n" +
                "                    \"github\": \"string or null\"\n" +
                "                  },\n" +
                "\n" +
                "                  \"summary\": \"string or null\",\n" +
                "\n" +
                "                  \"skills\": [\n" +
                "                    {\n" +
                "                      \"name\": \"string\",\n" +
                "                      \"level\": \"string or null\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "\n" +
                "                  \"experience\": [\n" +
                "                    {\n" +
                "                      \"companyName\": \"string\",\n" +
                "                      \"jobTitle\": \"string\",\n" +
                "                      \"startDate\": \"YYYY-MM-DD string or null\",\n"  +
                "                       \"endDate\": \"YYYY-MM-DD string or null\",\n" +
                "                      \"description\": \"string or null\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "\n" +
                "                  \"education\": [\n" +
                "                    {\n" +
                "                      \"institution\": \"string\",\n" +
                "                      \"degree\": \"string or null\",\n" +
                "                      \"fieldOfStudy\": \"string or null\",\n" +
                "                      \"startDate\": \"YYYY-MM-DD string or null\",\n" +
                "                       \"endDate\": \"YYYY-MM-DD string or null\",\n" +
                "                    }\n" +
                "                  ],\n" +
                "\n" +
                "                  \"certifications\": [\n" +
                "                    {\n" +
                "                      \"name\": \"string\",\n" +
                "                      \"issuer\": \"string or null\",\n" +
                "                      \"issueDate\": \"string or null\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "\n" +
                "                  \"projects\": [\n" +
                "                    {\n" +
                "                      \"name\": \"string\",\n" +
                "                      \"description\": \"string or null\",\n" +
                "                      \"technologies\": []\n" +
                "                    }\n" +
                "                  ],\n" +
                "\n" +
                "                  \"languages\": [\n" +
                "                    {\n" +
                "                      \"language\": \"string\",\n" +
                "                      \"proficiency\": \"string or null\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                }\n" +
                "\n" +
                "                IMPORTANT RULES FOR SKILLS:\n" +
                "                - skills MUST always be an array of JSON objects.\n" +
                "                - NEVER return skills as an array of strings.\n" +
                "                - Each skill object MUST contain \"name\".\n" +
                "                - \"level\" should be null if the CV does not specify a skill level.\n" +
                "\n" +
                "                IMPORTANT RULES FOR LANGUAGES:\n" +
                "                - languages MUST always be an array of JSON objects.\n" +
                "                - NEVER return languages as an array of strings.\n" +
                "                - Each language object MUST contain \"language\".\n" +
                "                - \"proficiency\" should be null if the CV does not specify proficiency.\n" +
                "                        \n" +
                "                        NEVER return skills as strings.\n" +
                "                                \n" +
                "                        WRONG:\n" +
                "                        \"skills\": [\"Java\", \"Spring Boot\"]\n" +
                "                                \n" +
                "                        CORRECT:\n" +
                "                        \"skills\": [\n" +
                "                            {\n" +
                "                                \"name\": \"Java\",\n" +
                "                                \"level\": null\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"name\": \"Spring Boot\",\n" +
                "                                \"level\": null\n" +
                "                            }\n" +
                "                        ]\n" +
                "                                \n" +
                "                        NEVER return languages as strings.\n" +
                "                                \n" +
                "                        WRONG:\n" +
                "                        \"languages\": [\"Arabic\", \"English\"]\n" +
                "                                \n" +
                "                        CORRECT:\n" +
                "                        \"languages\": [\n" +
                "                            {\n" +
                "                                \"language\": \"Arabic\",\n" +
                "                                \"proficiency\": \"Native\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"language\": \"English\",\n" +
                "                                \"proficiency\": \"Very Good\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "\n" +
                "                CV:\n" +
                "                %s ".formatted(cvText);

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