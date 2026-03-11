package com.azyu.mahallavote.bot.handler;

import com.azyu.mahallavote.bot.BotConstants;
import com.azyu.mahallavote.bot.KeyboardHelper;
import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.service.ProjectSettingService;
import com.azyu.mahallavote.service.ReferralService;
import com.azyu.mahallavote.service.TelegramUserService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class ReferralCommandHandler {

    private final TelegramUserService telegramUserService;
    private final ReferralService referralService;
    private final ProjectSettingService projectSettingService;

    public ReferralCommandHandler(
        TelegramUserService telegramUserService,
        ReferralService referralService,
        ProjectSettingService projectSettingService
    ) {
        this.telegramUserService = telegramUserService;
        this.referralService = referralService;
        this.projectSettingService = projectSettingService;
    }

    public SendMessage handle(Message message) {
        Long chatId = message.getChatId();
        Optional<TelegramUser> userOpt = telegramUserService.findByChatId(chatId);
        if (userOpt.isEmpty()) {
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("❌ Avval /start buyrug'ini yuboring.");
            return response;
        }

        TelegramUser user = userOpt.get();
        long referralCount = referralService.getReferralCount(user.getId());
        long votedCount = referralService.getVotedReferralCount(user.getId());

        Long rewardAmount = projectSettingService.getActiveValueAsLong("REFERRAL_VOTE_AMOUNT", 0L);
        String botUsername = projectSettingService.getActiveValue("TELEGRAM_BOT_USERNAME").orElse("");

        String referralLink = String.format("https://t.me/%s?start=%s%d", botUsername, BotConstants.REF_PREFIX, user.getUserId());

        String text = String.format(
            "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66 Referal dasturi\n\n" +
            "\uD83D\uDD17 Sizning havolangiz:\n%s\n\n" +
            "\uD83D\uDC65 Taklif qilganlar: %d\n" +
            "✅ Ovoz berganlar: %d\n" +
            "\uD83D\uDCB0 Har bir ovoz uchun: %d so'm\n\n" +
            "Do'stlaringizga yuboring va mukofot oling!",
            referralLink,
            referralCount,
            votedCount,
            rewardAmount
        );

        InlineKeyboardButton shareButton = new InlineKeyboardButton();
        shareButton.setText("\uD83D\uDCE4 Do'stlarga yuborish");
        shareButton.setSwitchInlineQuery("Mahalla ovoz berish botiga qo'shiling! " + referralLink);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(text);
        response.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(shareButton))));
        return response;
    }
}
