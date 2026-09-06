package com.Candidate_Service.cv.parsing_data.parsing;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CvAiParser {

    private final ChatClient chatClient;

    public CvAiParser(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ParsedCvResponse parse(String cvText) {
        ParsedCvResponse response = chatClient.prompt()
                .user(spec -> spec
                        .text("""
                            You are an HR recruitment parser. Extract candidate details 
                            from the provided CV text into the requested schema.
                            
                            CV Text:
                            {cvText}
                            """)
                        .param("cvText", cvText)
                )
                .call()
                .entity(ParsedCvResponse.class);

        return response;
    }
}