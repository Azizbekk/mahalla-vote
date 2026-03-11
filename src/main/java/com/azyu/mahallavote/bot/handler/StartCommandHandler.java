package com.azyu.mahallavote.bot.handler;

import com.azyu.mahallavote.bot.BotConstants;
import com.azyu.mahallavote.bot.KeyboardHelper;
import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.service.ReferralService;
import com.azyu.mahallavote.service.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(StartCommandHandler.class);

    private final TelegramUserService telegramUserService;
    private final ReferralService referralService;

    public StartCommandHandler(TelegramUserService telegramUserService, ReferralService referralService) {
        this.telegramUserService = telegramUserService;
        this.referralService = referralService;
    }

    public SendMessage handle(Message message) {
        Long chatId = message.getChatId();
        TelegramUser user = telegramUserService.registerUserIfNotExists(message.getFrom(), chatId);

        String text = message.getText();
        if (text != null && text.contains(BotConstants.REF_PREFIX)) {
            handleReferral(text, user);
        }

        String welcomeText = String.format(
            "\uD83E\uDD1D Hurmatli %s!\n\n" + "Mahalla ovoz berish botiga xush kelibsiz!\n" + "Quyidagi tugmalardan birini tanlang:",
            user.getFirstName() != null ? user.getFirstName() : "foydalanuvchi"
        );

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(welcomeText);
        response.setReplyMarkup(KeyboardHelper.mainMenu());
        return response;
    }

    private void handleReferral(String text, TelegramUser referred) {
        try {
            String refPart = text.substring(text.indexOf(BotConstants.REF_PREFIX) + BotConstants.REF_PREFIX.length());
            Long referrerUserId = Long.parseLong(refPart.trim());
            referralService.registerReferral(referrerUserId, referred);
        } catch (NumberFormatException e) {
            log.warn("Invalid referral link format: {}", text);
        }
    }
}
