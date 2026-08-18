package com.Candidate_Service.cv.parsing_data.ollama;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaChatRequest {

    private String model;

    private List<OllamaMessage> messages;

    private boolean stream;

    private String format;

}
