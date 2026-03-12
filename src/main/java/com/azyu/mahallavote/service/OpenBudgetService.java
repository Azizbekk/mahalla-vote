package com.azyu.mahallavote.service;

import com.azyu.mahallavote.service.captcha.AntiCaptchaService;
import com.azyu.mahallavote.service.captcha.BackgroundMatcher;
import com.azyu.mahallavote.service.dto.CaptchaResult;
import com.azyu.mahallavote.service.dto.SubmitResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.jsoup.Connection;
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
    private static final String CAPTCHA_POST_URL = "https://openbudget.uz/api/v2/vote/mvc/captcha";
    private static final String VERIFY_URL = "https://openbudget.uz/api/v2/vote/mvc/verify";

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    // OpenBudget saytida rasm CSS orqali shu kenglikda ko'rsatiladi: .img-lazy { max-width: 345px; }
    private static final int DISPLAY_WIDTH = 345;

    private final AntiCaptchaService antiCaptchaService;
    private final BackgroundMatcher backgroundMatcher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // chatId → session cookies (captcha POST dan olingan)
    private final Map<Long, Map<String, String>> sessionStore = new ConcurrentHashMap<>();
    // chatId → formatted phone (verify uchun)
    private final Map<Long, String> phoneStore = new ConcurrentHashMap<>();

    public OpenBudgetService(AntiCaptchaService antiCaptchaService, BackgroundMatcher backgroundMatcher) {
        this.antiCaptchaService = antiCaptchaService;
        this.backgroundMatcher = backgroundMatcher;
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

    // Captcha GET dan olingan cookie larni vaqtincha saqlash
    private final Map<Long, Map<String, String>> captchaCookieStore = new ConcurrentHashMap<>();

    /**
     * Fetches captcha images from OpenBudget captcha page.
     * Cookie larni saqlaydi — POST da ishlatiladi.
     * Returns String array: [imageA_base64, imageB_base64]
     */
    public String[] fetchCaptchaImages(Long chatId, String captchaPageUrl) {
        try {
            Connection.Response response = Jsoup.connect(captchaPageUrl).userAgent("Mozilla/5.0").timeout(10000).execute();

            // Session cookie larni saqlash
            captchaCookieStore.put(chatId, response.cookies());
            log.debug("Captcha cookies saved for chatId: {}: {}", chatId, response.cookies());

            Document doc = response.parse();

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
     * @return CaptchaResult with exactly 2 coordinates, or null if failed
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
        String instruction =
            "Click on EXACTLY 2 texts: '" + letterOne + "' and '" + letterTwo + "'. You MUST click exactly 2 points, no more, no less!";
        log.info("Captcha instruction: {}", instruction);

        String coordinatesJson = antiCaptchaService.solveImageToCoordinates(base64ImageB, instruction);
        if (coordinatesJson == null || coordinatesJson.isEmpty()) {
            log.warn("No coordinates returned from AntiCaptcha for instruction: {}", instruction);
            return null;
        }

        // Koordinatalar soni tekshirish — aynan 2 ta bo'lishi kerak
        // va rasm o'lchamiga qarab scale qilish
        try {
            JsonNode coords = objectMapper.readTree(coordinatesJson);
            if (!coords.isArray() || coords.size() != 2) {
                log.warn("Expected 2 coordinates but got {}: {}", coords.size(), coordinatesJson);
                return null;
            }

            // Rasm original kengligi ni aniqlash va koordinatalarni scale qilish
            coordinatesJson = scaleCoordinatesToDisplaySize(base64ImageB, coords);
        } catch (Exception e) {
            log.warn("Failed to parse/scale coordinates: {}", coordinatesJson, e);
            return null;
        }

        log.info("Captcha solved: letters={}, coordinates={}", letters, coordinatesJson);
        return new CaptchaResult(letters, coordinatesJson);
    }

    private static final int MAX_CAPTCHA_RETRIES = 3;
    private static final int MAX_BACKGROUND_RETRIES = 10; // oson background topish uchun max urinishlar

    /**
     * Full captcha flow: fetch images from OpenBudget and solve via AntiCaptcha.
     * Avval "oson" background kelguncha yangi captcha oladi,
     * keyin yechishga urinadi (max 3 marta).
     */
    public CaptchaResult fetchAndSolveCaptcha(Long chatId, String openBudgetUrl) {
        String uuid = extractUuidFromUrl(openBudgetUrl);
        if (uuid == null) {
            log.error("Cannot extract UUID from OpenBudget URL: {}", openBudgetUrl);
            return null;
        }

        String captchaPageUrl = CAPTCHA_BASE_URL + uuid;

        for (int attempt = 1; attempt <= MAX_CAPTCHA_RETRIES; attempt++) {
            // Oson background kelguncha yangi captcha olamiz
            String[] images = fetchEasyBackgroundCaptcha(chatId, captchaPageUrl);
            if (images == null) {
                log.error("Failed to fetch easy background captcha from: {}", captchaPageUrl);
                return null;
            }

            log.info("Easy background captcha fetched (attempt {}/{}), solving...", attempt, MAX_CAPTCHA_RETRIES);
            CaptchaResult result = solveTwoStepCaptcha(images[0], images[1]);

            if (result != null) {
                return result;
            }

            log.warn("Captcha solve attempt {}/{} failed, retrying with new images...", attempt, MAX_CAPTCHA_RETRIES);
        }

        log.error("All {} captcha solve attempts failed for: {}", MAX_CAPTCHA_RETRIES, captchaPageUrl);
        return null;
    }

    /**
     * Oson background li captcha kelguncha yangi captcha olib turadi.
     * Max MAX_BACKGROUND_RETRIES marta urinadi.
     */
    private String[] fetchEasyBackgroundCaptcha(Long chatId, String captchaPageUrl) {
        for (int i = 1; i <= MAX_BACKGROUND_RETRIES; i++) {
            log.info("Fetching captcha, looking for easy background (attempt {}/{})", i, MAX_BACKGROUND_RETRIES);

            String[] images = fetchCaptchaImages(chatId, captchaPageUrl);
            if (images == null) {
                log.error("Failed to fetch captcha images");
                return null;
            }

            if (backgroundMatcher.isEasyBackground(images[1])) {
                log.info("Easy background found on attempt {}", i);
                return images;
            }

            log.debug("Background not easy, fetching new captcha...");
        }

        log.warn("No easy background found after {} attempts, using last captcha anyway", MAX_BACKGROUND_RETRIES);
        // Oxirgi olingan captchani ishlatamiz — yechilmasa retry qiladi
        return fetchCaptchaImages(chatId, captchaPageUrl);
    }

    /**
     * Captcha yechimini OpenBudget ga yuboradi va SMS so'raydi.
     * Session cookie larni saqlaydi verify uchun.
     */
    public SubmitResult submitVoteAndRequestSms(Long chatId, String phoneNumber, String coordinatesJson) {
        try {
            String formattedPhone = formatPhoneForOpenBudget(phoneNumber);
            String pointsJson = convertCoordinatesToPoints(coordinatesJson);

            log.info("Submitting vote for chatId: {}, phone: {}, points: {}", chatId, formattedPhone, pointsJson);

            // Captcha GET dan olingan cookie larni olish va tozalash
            Map<String, String> captchaCookies = captchaCookieStore.getOrDefault(chatId, Map.of());
            captchaCookieStore.remove(chatId);

            Connection.Response response = Jsoup.connect(CAPTCHA_POST_URL)
                .method(Connection.Method.POST)
                .data("phoneNumber", formattedPhone)
                .data("points", pointsJson)
                .cookies(captchaCookies)
                .header("Origin", "https://openbudget.uz")
                .header("Referer", CAPTCHA_POST_URL)
                .userAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36"
                )
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .execute();

            String body = response.body();
            log.info("OpenBudget captcha submit response status: {}", response.statusCode());
            log.info("OpenBudget captcha submit response body: {}", body);

            // Xatolik tekshirish - error-alert div dan xabar olish
            Document doc = Jsoup.parse(body);
            Element errorAlert = doc.select("#error-alert").first();
            if (errorAlert != null && !errorAlert.hasClass("hide-element") && !errorAlert.text().isEmpty()) {
                String errorMessage = errorAlert.text().replaceAll("\\s*\\([a-f0-9]+\\)\\s*$", "").trim();
                log.warn("OpenBudget error for chatId: {}: {}", chatId, errorAlert.text());
                return SubmitResult.error(errorMessage);
            }

            // SMS kod sahifasi kelganini tekshirish
            if (body.contains("otpCode") || body.contains("СМС код")) {
                // Session cookie larni saqlash — captcha GET + POST javobidagi cookie'lar birlashtiriladi
                Map<String, String> cookies = new java.util.HashMap<>(captchaCookies);
                cookies.putAll(response.cookies());
                sessionStore.put(chatId, cookies);
                log.info("Session cookies saved for chatId: {}: {}", chatId, cookies.keySet());
                phoneStore.put(chatId, formattedPhone);
                log.info("SMS code page received for chatId: {}", chatId);
                return SubmitResult.smsSent();
            }

            // "Ovoz qabul qilindi / tekshiruv jarayonida" sahifasi
            Element successP = doc.select(".card p").first();
            if (successP != null && !successP.text().isEmpty()) {
                String message = successP.text();
                log.info("Vote accepted by OpenBudget for chatId: {}: {}", chatId, message);
                return SubmitResult.accepted(message);
            }

            log.warn("Unexpected response from OpenBudget for chatId: {}", chatId);
            return SubmitResult.error("Kutilmagan javob olindi. Qaytadan urinib ko'ring.");
        } catch (Exception e) {
            log.error("Failed to submit vote to OpenBudget for chatId: {}", chatId, e);
            return SubmitResult.error("OpenBudget bilan bog'lanishda xatolik.");
        }
    }

    /**
     * OTP kodni tekshirish - SMS tasdiqlash.
     */
    public SubmitResult verifyOtp(Long chatId, String otpCode) {
        try {
            Map<String, String> cookies = sessionStore.get(chatId);
            String phone = phoneStore.get(chatId);

            if (cookies == null || phone == null) {
                log.error("No session found for chatId: {}", chatId);
                return SubmitResult.error("Session topilmadi. Qaytadan ovoz bering.");
            }

            log.info("Verifying OTP for chatId: {}, phone: {}", chatId, phone);

            Connection.Response response = Jsoup.connect(VERIFY_URL)
                .method(Connection.Method.POST)
                .data("otpCode", otpCode)
                .data("grToken", "")
                .cookies(cookies)
                .header("Origin", "https://openbudget.uz")
                .header("Referer", CAPTCHA_POST_URL)
                .userAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36"
                )
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .execute();

            // Verify dan kelgan cookie larni yangilash (session davom etishi uchun)ok
            cookies.putAll(response.cookies());

            String body = response.body();
            log.info("OTP verify response status: {}", response.statusCode());
            log.debug("OTP verify response body: {}", body);

            Document doc = Jsoup.parse(body);
            int statusCode = response.statusCode();

            // 200 — muvaffaqiyatli
            if (statusCode == 200) {
                Element successP = doc.select(".card p").first();
                String message = (successP != null && !successP.text().isEmpty()) ? successP.text() : "Ovoz muvaffaqiyatli qabul qilindi!";
                log.info("OTP verified successfully for chatId: {}: {}", chatId, message);
                sessionStore.remove(chatId);
                phoneStore.remove(chatId);
                return SubmitResult.accepted(message);
            }

            // 400 — xatolik
            if (statusCode == 400) {
                sessionStore.remove(chatId);
                phoneStore.remove(chatId);

                // #error-alert tekshirish
                Element errorAlert = doc.select("#error-alert").first();
                if (errorAlert != null && !errorAlert.hasClass("hide-element") && !errorAlert.text().isEmpty()) {
                    log.warn("OTP verification failed for chatId: {}: {}", chatId, errorAlert.text());
                    return SubmitResult.error(errorAlert.text());
                }

                // .card h2 tekshirish (masalan: "Илтимос тизимни қайта ишга туширинг")
                Element cardH2 = doc.select(".card h2").first();
                if (cardH2 != null && !cardH2.text().isEmpty()) {
                    log.warn("OTP error page for chatId: {}: {}", chatId, cardH2.text());
                    return SubmitResult.error(cardH2.text());
                }

                log.warn("OTP 400 error for chatId: {}", chatId);
                return SubmitResult.error("Xatolik yuz berdi. Qaytadan urinib ko'ring.");
            }

            // Boshqa kutilmagan status
            log.warn("Unexpected verify response (status={}) for chatId: {}", statusCode, chatId);
            sessionStore.remove(chatId);
            phoneStore.remove(chatId);
            return SubmitResult.error("Kutilmagan javob. Qaytadan urinib ko'ring.");
        } catch (Exception e) {
            log.error("Failed to verify OTP for chatId: {}", chatId, e);
            return SubmitResult.error("OTP tekshirishda xatolik yuz berdi.");
        }
    }

    /**
     * Session tozalash.
     */
    public void clearSession(Long chatId) {
        sessionStore.remove(chatId);
        phoneStore.remove(chatId);
    }

    /**
     * AntiCaptcha koordinatalarini OpenBudget points formatiga o'tkazadi.
     * Input:  [[140,109],[191,75]]
     * Output: [{"id":"140109","x":140,"y":109},{"id":"19175","x":191,"y":75}]
     */
    String convertCoordinatesToPoints(String coordinatesJson) {
        try {
            JsonNode coordinates = objectMapper.readTree(coordinatesJson);
            ArrayNode points = objectMapper.createArrayNode();

            for (JsonNode coord : coordinates) {
                int x, y;
                if (coord.isArray()) {
                    // Format: [x, y]
                    x = coord.get(0).asInt();
                    y = coord.get(1).asInt();
                } else {
                    // Format: {"x": x, "y": y}
                    x = coord.get("x").asInt();
                    y = coord.get("y").asInt();
                }
                ObjectNode point = objectMapper.createObjectNode();
                point.put("id", String.valueOf(x) + String.valueOf(y));
                point.put("x", x);
                point.put("y", y);
                points.add(point);
            }

            return points.toString();
        } catch (Exception e) {
            log.error("Failed to convert coordinates: {}", coordinatesJson, e);
            return coordinatesJson;
        }
    }

    /**
     * Telefon raqamni OpenBudget formati ga o'tkazadi.
     * Input:  998909932084
     * Output: 90 993-20-84
     */
    String formatPhoneForOpenBudget(String phone) {
        // 998 ni olib tashlash
        String withoutPrefix = phone;
        if (phone.startsWith("998")) {
            withoutPrefix = phone.substring(3);
        }

        // 9 ta raqam: XX XXX-XX-XX
        if (withoutPrefix.length() == 9) {
            return (
                withoutPrefix.substring(0, 2) +
                " " +
                withoutPrefix.substring(2, 5) +
                "-" +
                withoutPrefix.substring(5, 7) +
                "-" +
                withoutPrefix.substring(7, 9)
            );
        }

        return withoutPrefix;
    }

    /**
     * Anti-captcha koordinatalarini sayt display o'lchamiga moslashtiradi.
     * Anti-captcha original rasm o'lchamida (masalan 601x345) koordinata qaytaradi,
     * lekin saytda rasm 345px kenglikda ko'rsatiladi (.img-lazy { max-width: 345px }).
     * Shu sababli koordinatalarni proporsional kamaytirish kerak.
     */
    private String scaleCoordinatesToDisplaySize(String base64Image, JsonNode coords) throws Exception {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        int originalWidth = image.getWidth();

        if (originalWidth <= DISPLAY_WIDTH) {
            log.debug("Image width {} <= display width {}, no scaling needed", originalWidth, DISPLAY_WIDTH);
            return coords.toString();
        }

        double scale = (double) DISPLAY_WIDTH / originalWidth;
        log.info("Scaling coordinates: original width={}, display width={}, scale={}", originalWidth, DISPLAY_WIDTH, scale);

        ArrayNode scaledCoords = objectMapper.createArrayNode();
        for (JsonNode coord : coords) {
            int x, y;
            if (coord.isArray()) {
                x = coord.get(0).asInt();
                y = coord.get(1).asInt();
            } else {
                x = coord.get("x").asInt();
                y = coord.get("y").asInt();
            }
            int scaledX = (int) Math.round(x * scale);
            int scaledY = (int) Math.round(y * scale);
            log.debug("Coordinate [{},{}] → [{},{}]", x, y, scaledX, scaledY);

            ArrayNode point = objectMapper.createArrayNode();
            point.add(scaledX);
            point.add(scaledY);
            scaledCoords.add(point);
        }

        return scaledCoords.toString();
    }

    private String extractBase64FromSrc(String src) {
        if (src != null && src.startsWith("data:image")) {
            return src.substring(src.indexOf(",") + 1);
        }
        return src;
    }
}
