package com.alphasolutions.eventapi.service;

import java.util.List;
import java.util.Map;

public interface AIRequestService {
    List<Map<String,Object>> sendRequestToGemini(Map<String, Object> request);
    String extractHistoryQuestions(List<Map<String,Object>> existingQuestion);

}
