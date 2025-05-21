package com.example.webhookapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChallengeRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void triggerOnStartup() {
        try {
            //
            String registrationUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> registrationBody = Map.of(
                    "name", "Aarya",
                    "regNo", "1032221688",
                    "email", "1032221688@mitwpu.edu.in"
            );

            HttpHeaders regHeaders = new HttpHeaders();
            regHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> regEntity = new HttpEntity<>(registrationBody, regHeaders);
            ResponseEntity<String> regResponse = restTemplate.postForEntity(registrationUrl, regEntity, String.class);

            JsonNode json = objectMapper.readTree(regResponse.getBody());
            String webhookUrl = json.get("webhook").asText();
            String accessToken = json.get("accessToken").asText();

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            //SQL Query
            String finalQuery = "SELECT " +
                    "e1.EMP_ID, " +
                    "e1.FIRST_NAME, " +
                    "e1.LAST_NAME, " +
                    "d.DEPARTMENT_NAME, " +
                    "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                    "FROM EMPLOYEE e1 " +
                    "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                    "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT " +
                    "AND e1.DOB < e2.DOB " +
                    "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                    "ORDER BY e1.EMP_ID DESC;";


            String jsonPayload = objectMapper.writeValueAsString(Map.of("finalQuery", finalQuery));


            HttpHeaders answerHeaders = new HttpHeaders();
            answerHeaders.setContentType(MediaType.APPLICATION_JSON);
            answerHeaders.set("Authorization", "Bearer " + accessToken); // Manually add JWT

            // Debug info
            System.out.println("Sending to Webhook: " + webhookUrl);
            System.out.println("Authorization: Bearer " + accessToken);
            System.out.println("Payload: " + jsonPayload);

            HttpEntity<String> answerEntity = new HttpEntity<>(jsonPayload, answerHeaders);
            ResponseEntity<String> answerResponse = restTemplate.postForEntity(webhookUrl, answerEntity, String.class);

            System.out.println("Webhook Response Code: " + answerResponse.getStatusCode());
            System.out.println("Webhook Response Body: " + answerResponse.getBody());

        } catch (Exception e) {
            System.err.println("Error during webhook process:");
            e.printStackTrace();
        }
    }
}
