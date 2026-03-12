package com.azyu.mahallavote.service.captcha;

import jakarta.annotation.PostConstruct;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * "Oson" background rasmlarni aniqlaydi.
 * Captcha imageB ning backgroundi taniqli ro'yxatda bormi tekshiradi.
 * Aniqlash usuli: rasmning bir necha nuqtasidagi ranglarni solishtirish.
 */
@Component
public class BackgroundMatcher {

    private static final Logger log = LoggerFactory.getLogger(BackgroundMatcher.class);

    // Solishtirish uchun sample nuqtalar (x, y) — burchaklar va chetlar, harflar tushmaydigan joylar
    private static final int[][] SAMPLE_POINTS = {
        { 5, 5 }, // yuqori chap
        { 5, 220 }, // pastki chap
        { 340, 5 }, // yuqori o'ng
        { 340, 220 }, // pastki o'ng
        { 170, 5 }, // yuqori o'rta
        { 170, 220 }, // pastki o'rta
    };

    // Ranglar o'rtasidagi max farq (0-255 per channel, 3 channel = max 765)
    private static final int COLOR_THRESHOLD = 80;

    private final List<int[]> knownBackgrounds = new ArrayList<>();
    private final List<String> backgroundNames = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadBackground("captcha-backgrounds/bg1.png", "tog-quyosh");
        loadBackground("captcha-backgrounds/bg2.png", "plyaj-dengiz");
        loadBackground("captcha-backgrounds/bg3.png", "kol-daraxt");
        log.info("Loaded {} known easy backgrounds", knownBackgrounds.size());
    }

    private void loadBackground(String resourcePath, String name) {
        try {
            InputStream is = new ClassPathResource(resourcePath).getInputStream();
            BufferedImage img = ImageIO.read(is);
            int[] colors = extractSampleColors(img);
            knownBackgrounds.add(colors);
            backgroundNames.add(name);
            log.debug("Loaded background '{}': {} sample colors", name, SAMPLE_POINTS.length);
        } catch (Exception e) {
            log.error("Failed to load background: {}", resourcePath, e);
        }
    }

    /**
     * Base64 rasmni tekshiradi — "oson" backgroundmi?
     */
    public boolean isEasyBackground(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            int[] sampleColors = extractSampleColors(img);

            for (int i = 0; i < knownBackgrounds.size(); i++) {
                int totalDiff = calculateDifference(sampleColors, knownBackgrounds.get(i));
                log.debug("Background '{}' difference: {}", backgroundNames.get(i), totalDiff);
                if (totalDiff < COLOR_THRESHOLD * SAMPLE_POINTS.length) {
                    log.info("Matched easy background: '{}'", backgroundNames.get(i));
                    return true;
                }
            }

            log.debug("No easy background matched");
            return false;
        } catch (Exception e) {
            log.error("Failed to check background", e);
            return false;
        }
    }

    private int[] extractSampleColors(BufferedImage img) {
        int[] colors = new int[SAMPLE_POINTS.length * 3]; // R, G, B per point
        for (int i = 0; i < SAMPLE_POINTS.length; i++) {
            int x = Math.min(SAMPLE_POINTS[i][0], img.getWidth() - 1);
            int y = Math.min(SAMPLE_POINTS[i][1], img.getHeight() - 1);
            Color c = new Color(img.getRGB(x, y));
            colors[i * 3] = c.getRed();
            colors[i * 3 + 1] = c.getGreen();
            colors[i * 3 + 2] = c.getBlue();
        }
        return colors;
    }

    private int calculateDifference(int[] a, int[] b) {
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff += Math.abs(a[i] - b[i]);
        }
        return diff;
    }
}
