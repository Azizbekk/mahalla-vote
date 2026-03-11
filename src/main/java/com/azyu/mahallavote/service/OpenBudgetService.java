package com.azyu.mahallavote.service;

import com.azyu.mahallavote.service.captcha.AntiCaptchaService;
import com.azyu.mahallavote.service.dto.CaptchaResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpenBudgetService {

    private static final Logger log = LoggerFactory.getLogger(OpenBudgetService.class);
    private static final String CAPTCHA_BASE_URL = "https://openbudget.uz/api/v2/vote/mvc/captcha/";
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    private final AntiCaptchaService antiCaptchaService;

    public OpenBudgetService(AntiCaptchaService antiCaptchaService) {
        this.antiCaptchaService = antiCaptchaService;
    }

    /**
     * Extract UUID from OpenBudget initiative URL.
     * Example: https://openbudget.uz/boards/initiatives/initiative/53/b7acaad3-9b00-44ca-b3da-e6143f1cba81
     */
    public String extractUuidFromUrl(String openBudgetUrl) {
        if (openBudgetUrl == null) return null;
        Matcher matcher = UUID_PATTERN.matcher(openBudgetUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        log.warn("Could not extract UUID from URL: {}", openBudgetUrl);
        return null;
    }

    /**
     * Fetches captcha images from OpenBudget captcha page.
     * Selectors from old project: img[alt] for image A, #imageB for image B.
     * Returns String array: [imageA_base64, imageB_base64]
     */
    public String[] fetchCaptchaImages(String captchaPageUrl) {
        try {
            Document doc = Jsoup.connect(captchaPageUrl).userAgent("Mozilla/5.0").timeout(10000).get();

            // Rasm A - img[alt] (birinchi rasm)
            Element aImg = doc.select("img[alt]").first();
            if (aImg == null) {
                log.warn("Image A not found on captcha page: {}", captchaPageUrl);
                return null;
            }
            String aImageBase64 = extractBase64FromSrc(aImg.attr("src"));

            // Rasm B - id="imageB"
            Element bImg = doc.select("#imageB").first();
            if (bImg == null) {
                log.warn("Image B (#imageB) not found on captcha page: {}", captchaPageUrl);
                return null;
            }
            String bImageBase64 = extractBase64FromSrc(bImg.attr("src"));

            return new String[] { aImageBase64, bImageBase64 };
        } catch (Exception e) {
            log.error("Failed to fetch captcha from OpenBudget: {}", captchaPageUrl, e);
            return null;
        }
    }

    /**
     * Two-step captcha solving:
     * Step 1: Extract 4 letters from image A via ImageToText
     * Step 2: Split into 2-char pairs, find coordinates of those pairs in image B
     */
    public CaptchaResult solveTwoStepCaptcha(String base64ImageA, String base64ImageB) {
        // Step 1: harflarni olish
        String letters = antiCaptchaService.solveImageToText(base64ImageA);
        if (letters == null || letters.length() < 4) {
            log.warn("Failed to extract letters from image A, got: {}", letters);
            return null;
        }

        letters = letters.toUpperCase().replaceAll("[^A-Z]", "");
        if (letters.length() < 4) {
            log.warn("Not enough letters extracted from image A: {}", letters);
            return null;
        }

        String letterOne = letters.substring(0, 2);
        String letterTwo = letters.substring(2, 4);

        // Step 2: koordinatalarni topish
        String instruction = "Click on the two texts: '" + letterOne + "' and '" + letterTwo + "' in the image!";
        log.info("Captcha instruction: {}", instruction);

        String coordinatesJson = antiCaptchaService.solveImageToCoordinates(base64ImageB, instruction);
        if (coordinatesJson == null || coordinatesJson.isEmpty()) {
            log.warn("No coordinates returned from AntiCaptcha for instruction: {}", instruction);
            return null;
        }

        log.info("Captcha solved: letters={}, coordinates={}", letters, coordinatesJson);
        return new CaptchaResult(letters, coordinatesJson);
    }

    /**
     * Full captcha flow: fetch images from OpenBudget and solve via AntiCaptcha.
     */
    public CaptchaResult fetchAndSolveCaptcha(String openBudgetUrl) {
        String uuid = extractUuidFromUrl(openBudgetUrl);
        if (uuid == null) {
            log.error("Cannot extract UUID from OpenBudget URL: {}", openBudgetUrl);
            return null;
        }

        String captchaPageUrl = CAPTCHA_BASE_URL + uuid;
        log.info("Fetching captcha from: {}", captchaPageUrl);

        String[] images = fetchCaptchaImages(captchaPageUrl);
        if (images == null) {
            log.error("Failed to fetch captcha images from: {}", captchaPageUrl);
            return null;
        }

        log.info("Captcha images fetched, solving two-step captcha...");
        return solveTwoStepCaptcha(images[0], images[1]);
    }

    private String extractBase64FromSrc(String src) {
        if (src != null && src.startsWith("data:image")) {
            return src.substring(src.indexOf(",") + 1);
        }
        return src;
    }
}
