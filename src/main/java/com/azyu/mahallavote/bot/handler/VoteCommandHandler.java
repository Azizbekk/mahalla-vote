package com.azyu.mahallavote.bot.handler;

import com.azyu.mahallavote.bot.KeyboardHelper;
import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.domain.Vote;
import com.azyu.mahallavote.domain.VoteLot;
import com.azyu.mahallavote.domain.enumeration.TelegramUserState;
import com.azyu.mahallavote.service.OpenBudgetService;
import com.azyu.mahallavote.service.ReferralService;
import com.azyu.mahallavote.service.TelegramUserService;
import com.azyu.mahallavote.service.VoteLotService;
import com.azyu.mahallavote.service.VoteService;
import com.azyu.mahallavote.service.dto.CaptchaResult;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class VoteCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(VoteCommandHandler.class);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?998\\d{9}$|^\\d{9}$");

    private final TelegramUserService telegramUserService;
    private final VoteService voteService;
    private final ReferralService referralService;
    private final VoteLotService voteLotService;
    private final OpenBudgetService openBudgetService;

    public VoteCommandHandler(
        TelegramUserService telegramUserService,
        VoteService voteService,
        ReferralService referralService,
        VoteLotService voteLotService,
        OpenBudgetService openBudgetService
    ) {
        this.telegramUserService = telegramUserService;
        this.voteService = voteService;
        this.referralService = referralService;
        this.voteLotService = voteLotService;
        this.openBudgetService = openBudgetService;
    }

    public SendMessage handleVoteButton(Message message) {
        Long chatId = message.getChatId();

        // Active lot tekshirish
        Optional<VoteLot> activeLot = voteLotService.findActiveLot();
        if (activeLot.isEmpty()) {
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("📭 Ovoz berish uchun ochiq lot topilmadi.\n\n" + "Tez orada yangi lot e'lon qilinadi!");
            response.setReplyMarkup(KeyboardHelper.mainMenu());
            return response;
        }

        VoteLot lot = activeLot.get();
        if (lot.getCurrentVoteCount() >= lot.getTargetVoteCount()) {
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText(
                "✅ Ushbu lot uchun yetarli ovoz yig'ildi!\n\n" +
                "📊 " +
                lot.getCurrentVoteCount() +
                " / " +
                lot.getTargetVoteCount() +
                " ovoz\n\n" +
                "Yangi lot ochilganda sizga xabar beramiz!"
            );
            response.setReplyMarkup(KeyboardHelper.mainMenu());
            return response;
        }

        telegramUserService.updateState(chatId, TelegramUserState.AWAITING_PHONE);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(
            "\uD83D\uDCF1 Ovoz berish uchun telefon raqamingizni yuboring.\n\n" +
            "Formatlar: +998991234567 yoki 991234567\n\n" +
            "Yoki pastdagi tugmani bosing:"
        );
        response.setReplyMarkup(KeyboardHelper.phoneRequestKeyboard());
        return response;
    }

    public SendMessage handlePhoneInput(Message message, Consumer<SendMessage> messageSender) {
        Long chatId = message.getChatId();

        // Active lot tekshirish
        Optional<VoteLot> activeLot = voteLotService.findActiveLot();
        if (activeLot.isEmpty()) {
            telegramUserService.updateState(chatId, TelegramUserState.HOME);
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("📭 Ovoz berish uchun ochiq lot topilmadi.");
            response.setReplyMarkup(KeyboardHelper.mainMenu());
            return response;
        }

        VoteLot lot = activeLot.get();
        if (lot.getCurrentVoteCount() >= lot.getTargetVoteCount()) {
            telegramUserService.updateState(chatId, TelegramUserState.HOME);
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("✅ Ushbu lot uchun yetarli ovoz yig'ildi!");
            response.setReplyMarkup(KeyboardHelper.mainMenu());
            return response;
        }

        String phoneNumber = extractPhoneNumber(message);

        if (phoneNumber == null) {
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("❌ Telefon raqam formati noto'g'ri. Qaytadan yuboring:");
            return response;
        }

        String normalizedPhone = normalizePhone(phoneNumber);

        if (voteService.existsByPhoneNumber(normalizedPhone)) {
            telegramUserService.updateState(chatId, TelegramUserState.HOME);
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("⚠️ Bu telefon raqam bilan allaqachon ovoz berilgan!");
            response.setReplyMarkup(KeyboardHelper.mainMenu());
            return response;
        }

        Optional<TelegramUser> userOpt = telegramUserService.findByChatId(chatId);
        if (userOpt.isEmpty()) {
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("❌ Xatolik yuz berdi. /start buyrug'ini yuboring.");
            return response;
        }

        TelegramUser user = userOpt.get();
        telegramUserService.updatePhoneNumber(chatId, normalizedPhone);

        // Captcha tekshirish boshlash
        SendMessage waitMessage = new SendMessage();
        waitMessage.setChatId(chatId.toString());
        waitMessage.setText("⏳ Captcha tekshirilmoqda, iltimos kuting...");
        messageSender.accept(waitMessage);

        // OpenBudget captcha olish va yechish
        CaptchaResult captchaResult = openBudgetService.fetchAndSolveCaptcha(lot.getOpenBudgetUrl());

        if (captchaResult == null) {
            log.error("Captcha solving failed for lot: {} chatId: {}", lot.getId(), chatId);
            telegramUserService.updateState(chatId, TelegramUserState.HOME);
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("❌ Captcha tekshirishda xatolik yuz berdi. Qaytadan urinib ko'ring.");
            response.setReplyMarkup(KeyboardHelper.mainMenu());
            return response;
        }

        log.info("Captcha solved for chatId: {}, result: {}", chatId, captchaResult);

        // TODO: OpenBudget ga ovoz yuborish (captcha + telefon raqam)
        // TODO: OTP kutish va tasdiqlash

        Vote vote = voteService.createVote(user, normalizedPhone);
        voteLotService.incrementVoteCount(lot.getId());
        referralService.markAsVoted(user.getId());
        telegramUserService.updateState(chatId, TelegramUserState.HOME);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(
            String.format(
                "✅ Ovozingiz qabul qilindi!\n\n" +
                "\uD83D\uDCDE Raqam: %s\n" +
                "\uD83D\uDCB0 Mukofot: %d so'm\n\n" +
                "⏳ Tez orada tasdiqlangach, hisobingizga tushadi.",
                normalizedPhone,
                vote.getAmount()
            )
        );
        response.setReplyMarkup(KeyboardHelper.mainMenu());
        return response;
    }

    private String extractPhoneNumber(Message message) {
        if (message.getContact() != null) {
            return message.getContact().getPhoneNumber();
        }
        String text = message.getText();
        if (text != null) {
            String cleaned = text.replaceAll("[\\s\\-()]", "");
            if (PHONE_PATTERN.matcher(cleaned).matches()) {
                return cleaned;
            }
        }
        return null;
    }

    private String normalizePhone(String phone) {
        String cleaned = phone.replaceAll("[^\\d]", "");
        if (cleaned.length() == 9) {
            return "998" + cleaned;
        }
        if (cleaned.startsWith("998")) {
            return cleaned;
        }
        return cleaned;
    }
}
