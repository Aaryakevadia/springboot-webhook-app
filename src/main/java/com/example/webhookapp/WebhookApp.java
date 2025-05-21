package com.example.webhookapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class WebhookApp implements CommandLineRunner {

    // Webhook endpoint
    private final String webhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    // Authorization Token — used the one from the problem statement
    private final String token = "test_2u1hBQ9PTz7kXrRWQZzAtcTb0wU";

    public static void main(String[] args) {
        SpringApplication.run(WebhookApp.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            sendFinalQuery();
        } catch (Exception e) {
            System.out.println("❌ Error sending webhook request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendFinalQuery() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);  // ✅ Adds: Authorization: Bearer <token>

        // Read JSON body from file (put the file in src/main/resources/)
        String json = Files.readString(Paths.get("src/main/resources/testWebhookSample.json"));

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        System.out.println("🔗 Sending POST to: " + webhookUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);

        System.out.println("✅ Response Status: " + response.getStatusCode());
        System.out.println("📦 Response Body: " + response.getBody());
    }
}
