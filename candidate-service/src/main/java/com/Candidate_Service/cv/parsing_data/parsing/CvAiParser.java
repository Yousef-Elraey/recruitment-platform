package com.Candidate_Service.cv.parsing_data.parsing;

import org.springframework.stereotype.Service;

@Service
public interface CvAiParser {

//    private final OpenAIClient client;

//    public CvAiParser(@Value("${openai.api-key}") String apiKey) {
//
//
//        this.client = OpenAIOkHttpClient.fromEnv();
//    }

    public ParsedCvResponse parse(String cvText);
//
//
//    {
//
//        if (cvText == null || cvText.isBlank()) {
//            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CV_DATA_INVALID.name(),
//                    "cv is empty");
//        }
//
//
//        StructuredResponseCreateParams<ParsedCvResponse> params =
//                ResponseCreateParams.builder()
//                        .model(ChatModel.GPT_5_2)
//                        .input("""
//                                You are an expert CV parser.
//
//                                Analyze the following CV and extract
//                                the information into the requested
//                                structured format.
//
//                                Rules:
//                                - Extract only information actually present.
//                                - Do not invent information.
//                                - If information is missing, use null or an empty list.
//                                - Keep each experience separate.
//                                - Keep each education entry separate.
//                                - Keep each project separate.
//                                - Keep each skill separate.
//
//                                CV:
//
//                                """ + cvText)
//                        .text(ParsedCvResponse.class)
//                        .build();
//
//        StructuredResponse<ParsedCvResponse> response =
//                client.responses().create(params);
//
//        return response.output()
//                .stream()
//                .flatMap(item -> item.message().stream())
//                .flatMap(message -> message.content().stream())
//                .flatMap(content -> content.outputText().stream())
//                .findFirst()
//                .orElseThrow(() ->
//                        new IllegalStateException(
//                                "No structured CV response returned"
//                        ));
//    }
}