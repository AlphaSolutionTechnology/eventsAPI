package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.GeneratedQuestionLimitException;
import com.alphasolutions.eventapi.exception.InvalidApiKeyException;
import com.alphasolutions.eventapi.utils.Prompt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.naming.LimitExceededException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AIRequestServiceImpl implements AIRequestService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final List<Map<String, Object>> history = new LinkedList<>();


    @Override
    public List<Map<String, Object>> sendRequestToGemini(Map<String, Object> request) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new InvalidApiKeyException("Gemini API key not set");
        }
        
        String userMessage = (String) request.get("text");
        int questionCount = Integer.parseInt(request.getOrDefault("questionCount", 2).toString());
        if (questionCount > 20 ) {
            throw new GeneratedQuestionLimitException("Question limit exceeded: maximum is 20 questions");
        }
        if(questionCount < 1){
            questionCount = 1;
        }
        String historyPrompt = extractHistoryQuestions((List<Map<String,Object>>) request.get("existingQuestion"));
        String prompt = Prompt.GEMINIPROMPT.getFormattedPrompt(historyPrompt,userMessage,String.valueOf(questionCount));
        Map<String, Object> payload = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> parts = getMaps(response);

            String jsonResponse = (String) parts.get(0).get("text");
            jsonResponse = jsonResponse.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "").trim();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> parsedJson = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

                // Add new questions to history with timestamp
                for (Map<String, Object> question : parsedJson) {
                    question.put("timestamp", System.currentTimeMillis());
                }
                history.addAll(parsedJson);
                
                // Clean up old questions (older than 24 hours) and maintain size limit
                long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
                history.removeIf(question -> 
                    (Long) question.getOrDefault("timestamp", 0L) < twentyFourHoursAgo
                );
                
                // If still over limit, remove oldest entries
                while (history.size() > 50) {
                    history.remove(0);
                }

                return parsedJson;
            } catch (Exception e) {
                throw new InternalError("Failed to process API response");
            }
        } catch (Exception e) {
            throw new InternalError("Failed to communicate with AI service");
        }
    }

    private static List<Map<String, Object>> getMaps(ResponseEntity<Map> response) {
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("candidates")) {
            throw new InternalError("Invalid API response format");
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
        if (candidates.isEmpty()) {
            throw new InternalError("No candidates in API response");
        }

        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        if (parts.isEmpty()) {
            throw new InternalError("No content parts in API response");
        }
        return parts;
    }


    @Override
    public String extractHistoryQuestions(List<Map<String,Object>> history) {
        
        if (history != null && !history.isEmpty()) {
            StringBuilder historyPrompt = new StringBuilder();
            for (Map<String,Object> question : history) {
                historyPrompt.append("- Question: ").append(question.get("questionText")).append("\n");
                historyPrompt.append("  Options: ").append(String.join(", ", (List<String>) question.get("choices"))).append("\n");
                historyPrompt.append("  Correct Answer: ").append(question.get("correctAnswer")).append("\n\n");
            }
            return historyPrompt.toString();
        }
        return "";

    }
}
