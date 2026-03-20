package com.example.skillsh.web;

import com.example.skillsh.domain.dto.ai.AiRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    // Взимаме ключа от application.properties
    @Value("${gemini.api.key}")
    private String apiKey;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askAi(@RequestBody AiRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        // URL за модела Gemini 1.5 Flash (бърз и безплатен)
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Даваме инструкции на Gemini кой е той и какво го пита потребителят
        // Даваме на AI подробни инструкции как работи твоят сайт
        String systemPrompt = """
    You are 'SkillBot', the AI support assistant for 'Skill-Sharing Network'. 
    Be helpful and concise. Always reply in the language the user is speaking.
    
    Here are the available routes in the app:
    - Login: /login
    - Register Client: /register/client
    - Register Expert: /register/expert
    - Home / Search Experts: /home
    
    CRITICAL RULE: If the user wants to do an action that requires visiting one of these pages, you MUST append this exact tag at the very end of your response: ||ROUTE:/the-path||
    
    Example 1: 
    User: How do I find an expert?
    You: You can find experts by going to the Home page and using the search bar! ||ROUTE:/home||
    
    Example 2:
    User: I want to login.
    You: Please click the button below to go to the login page. ||ROUTE:/login||
    
    User says: """;

        String promptText = systemPrompt + request.getMessage();

        // Структурираме JSON тялото, което изисква Google Gemini API
        Map<String, Object> textPart = Map.of("text", promptText);
        Map<String, Object> parts = Map.of("parts", List.of(textPart));
        Map<String, Object> body = Map.of("contents", List.of(parts));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Пращаме заявката към Google
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Gemini връща сложен JSON. Извличаме само отговора стъпка по стъпка:
            // candidates[0] -> content -> parts[0] -> text
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get("parts");
            String aiText = (String) responseParts.get(0).get("text");

            // Връщаме го на Angular в същия формат, който той очаква!
            return ResponseEntity.ok(Map.of("reply", aiText));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("reply", "Извинете, в момента нямам връзка със сървърите на Google Gemini."));
        }
    }
}