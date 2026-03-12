package com.azyu.mahallavote.service.captcha;

import com.azyu.mahallavote.service.ProjectSettingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AntiCaptchaService {

    private static final Logger log = LoggerFactory.getLogger(AntiCaptchaService.class);
    private static final String API_URL = "https://api.anti-captcha.com";

    private final ProjectSettingService projectSettingService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AntiCaptchaService(ProjectSettingService projectSettingService) {
        this.projectSettingService = projectSettingService;
    }

    private String getApiKey() {
        return projectSettingService.getActiveValue("ANTICAPTCHA_API_KEY").orElse("");
    }

    /**
     * Solve image-to-text captcha (returns recognized text).
     */
    public String solveImageToText(String base64Image) {
        try {
            ObjectNode task = objectMapper.createObjectNode();
            task.put("type", "ImageToTextTask");
            task.put("body", base64Image);
            task.put("numeric", 2);
            task.put("minLength", 4);
            task.put("maxLength", 4);

            int taskId = createTask(task);
            return pollTextResult(taskId);
        } catch (Exception e) {
            log.error("Failed to solve ImageToText captcha", e);
            return null;
        }
    }

    /**
     * Solve image-to-coordinates captcha (returns click coordinates).
     * Used for OpenBudget "click on matching image" captchas.
     * Returns coordinates as JSON string, e.g. [[246,144],[74,18]]
     */
    public String solveImageToCoordinates(String base64Image, String comment) {
        try {
            ObjectNode task = objectMapper.createObjectNode();
            task.put("type", "ImageToCoordinatesTask");
            task.put("body", base64Image);
            task.put("comment", comment != null ? comment : "Click on the matching image");
            task.put("mode", "points");

            int taskId = createTask(task);
            return pollCoordinatesResult(taskId);
        } catch (Exception e) {
            log.error("Failed to solve ImageToCoordinates captcha", e);
            return null;
        }
    }

    private String cachedApiKey;

    private int createTask(ObjectNode task) throws Exception {
        cachedApiKey = getApiKey();
        ObjectNode request = objectMapper.createObjectNode();
        request.put("clientKey", cachedApiKey);
        request.set("task", task);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String response = restTemplate.postForObject(API_URL + "/createTask", new HttpEntity<>(request.toString(), headers), String.class);

        JsonNode result = objectMapper.readTree(response);
        if (result.get("errorId").asInt() != 0) {
            throw new RuntimeException("AntiCaptcha error: " + result.get("errorDescription").asText());
        }
        int taskId = result.get("taskId").asInt();
        log.debug("AntiCaptcha task created: {}", taskId);
        return taskId;
    }

    private String pollTextResult(int taskId) throws Exception {
        // OCR avtomatik — tez tayyor bo'ladi
        for (int i = 0; i < 4; i++) { // max: 4×2s = 8s
            Thread.sleep(2000);
            JsonNode result = getTaskResult(taskId);
            String status = result.get("status").asText();
            if ("ready".equals(status)) {
                return result.get("solution").get("text").asText();
            }
        }
        log.warn("AntiCaptcha task {} timed out", taskId);
        return null;
    }

    private String pollCoordinatesResult(int taskId) throws Exception {
        Thread.sleep(3000); // odam yechadi — ko'proq vaqt kerak
        for (int i = 0; i < 6; i++) { // max: 3s + 6×2s = 15s
            Thread.sleep(2000);
            JsonNode result = getTaskResult(taskId);
            String status = result.get("status").asText();
            if ("ready".equals(status)) {
                JsonNode coordinates = result.get("solution").get("coordinates");
                return coordinates.toString();
            }
        }
        log.warn("AntiCaptcha task {} timed out", taskId);
        return null;
    }

    private JsonNode getTaskResult(int taskId) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("clientKey", cachedApiKey);
        request.put("taskId", taskId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String response = restTemplate.postForObject(
            API_URL + "/getTaskResult",
            new HttpEntity<>(request.toString(), headers),
            String.class
        );

        JsonNode result = objectMapper.readTree(response);
        if (result.get("errorId").asInt() != 0) {
            throw new RuntimeException("AntiCaptcha error: " + result.get("errorDescription").asText());
        }
        return result;
    }
}
