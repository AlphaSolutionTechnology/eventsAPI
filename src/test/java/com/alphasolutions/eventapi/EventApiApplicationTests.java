package com.alphasolutions.eventapi;

import org.junit.jupiter.api.Test;
import org.springframework.ai.autoconfigure.vertexai.gemini.VertexAiGeminiAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@EnableAutoConfiguration(exclude = VertexAiGeminiAutoConfiguration.class)
class EventApiApplicationTests {

    @Test
    void contextLoads() {
        // Teste básico
    }
}
