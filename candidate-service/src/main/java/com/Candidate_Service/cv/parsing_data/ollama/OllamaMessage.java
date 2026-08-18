package com.Candidate_Service.cv.parsing_data.ollama;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaMessage {

    private String role;

    private String content;
}