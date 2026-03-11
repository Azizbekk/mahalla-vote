package com.azyu.mahallavote.service.dto;

/**
 * Captcha yechimi natijasi - DB ga saqlanmaydi, faqat OpenBudget ga yuborish uchun.
 */
public class CaptchaResult {

    private final String letters;
    private final String coordinatesJson;

    public CaptchaResult(String letters, String coordinatesJson) {
        this.letters = letters;
        this.coordinatesJson = coordinatesJson;
    }

    public String getLetters() {
        return letters;
    }

    public String getCoordinatesJson() {
        return coordinatesJson;
    }

    @Override
    public String toString() {
        return "CaptchaResult{letters='" + letters + "', coordinates=" + coordinatesJson + '}';
    }
}
