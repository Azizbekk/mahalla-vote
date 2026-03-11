package com.azyu.mahallavote.bot;

import com.azyu.mahallavote.bot.handler.BalanceCommandHandler;
import com.azyu.mahallavote.bot.handler.ReferralCommandHandler;
import com.azyu.mahallavote.bot.handler.StartCommandHandler;
import com.azyu.mahallavote.bot.handler.VoteCommandHandler;
import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.domain.enumeration.TelegramUserState;
import com.azyu.mahallavote.service.TelegramUserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MahallaVoteBotService extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(MahallaVoteBotService.class);

    private final String botUsername;
    private final TelegramUserService telegramUserService;
    private final StartCommandHandler startCommandHandler;
    private final VoteCommandHandler voteCommandHandler;
    private final ReferralCommandHandler referralCommandHandler;
    private final BalanceCommandHandler balanceCommandHandler;

    public MahallaVoteBotService(
        String botToken,
        String botUsername,
        TelegramUserService telegramUserService,
        StartCommandHandler startCommandHandler,
        VoteCommandHandler voteCommandHandler,
        ReferralCommandHandler referralCommandHandler,
        BalanceCommandHandler balanceCommandHandler
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.telegramUserService = telegramUserService;
        this.startCommandHandler = startCommandHandler;
        this.voteCommandHandler = voteCommandHandler;
        this.referralCommandHandler = referralCommandHandler;
        this.balanceCommandHandler = balanceCommandHandler;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();
        Long chatId = message.getChatId();

        try {
            SendMessage response = handleMessage(message);
            if (response != null) {
                execute(response);
            }
        } catch (TelegramApiException e) {
            log.error("Error sending message to chatId {}", chatId, e);
        } catch (Exception e) {
            log.error("Error processing message from chatId {}", chatId, e);
            sendErrorMessage(chatId);
        }
    }

    private SendMessage handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();

        // Handle /start command
        if (text != null && text.startsWith("/start")) {
            return startCommandHandler.handle(message);
        }

        // Check user state for ongoing flows
        Optional<TelegramUser> userOpt = telegramUserService.findByChatId(chatId);
        if (userOpt.isPresent()) {
            TelegramUser user = userOpt.get();

            if (user.getState() == TelegramUserState.AWAITING_PHONE) {
                return voteCommandHandler.handlePhoneInput(message, this::executeSafe);
            }
        }

        // Handle menu buttons
        if (text != null) {
            switch (text) {
                case BotConstants.BTN_VOTE:
                    return voteCommandHandler.handleVoteButton(message);
                case BotConstants.BTN_REFERRAL:
                    return referralCommandHandler.handle(message);
                case BotConstants.BTN_BALANCE:
                    return balanceCommandHandler.handle(message);
                case BotConstants.BTN_WITHDRAW:
                    return handleWithdraw(chatId);
                case BotConstants.BTN_PRIZE:
                    return handlePrize(chatId);
                default:
                    return handleUnknown(chatId);
            }
        }

        // Handle contact (phone number via button)
        if (message.getContact() != null) {
            if (userOpt.isPresent() && userOpt.get().getState() == TelegramUserState.AWAITING_PHONE) {
                return voteCommandHandler.handlePhoneInput(message, this::executeSafe);
            }
        }

        return null;
    }

    private SendMessage handleWithdraw(Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("⏳ Pulni yechib olish funksiyasi tez orada ishga tushadi!");
        response.setReplyMarkup(KeyboardHelper.mainMenu());
        return response;
    }

    private SendMessage handlePrize(Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(
            "\uD83C\uDF81 iPhone 16 sovg'a dasturi\n\n" +
            "Eng ko'p referal taklif qilgan foydalanuvchi iPhone 16 yutib oladi!\n\n" +
            "Do'stlaringizni taklif qiling va g'olib bo'ling!"
        );
        response.setReplyMarkup(KeyboardHelper.mainMenu());
        return response;
    }

    private SendMessage handleUnknown(Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("Quyidagi tugmalardan birini tanlang:");
        response.setReplyMarkup(KeyboardHelper.mainMenu());
        return response;
    }

    private void executeSafe(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId {}", message.getChatId(), e);
        }
    }

    private void sendErrorMessage(Long chatId) {
        try {
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId.toString());
            msg.setText("❌ Xatolik yuz berdi. Qaytadan urinib ko'ring.");
            msg.setReplyMarkup(KeyboardHelper.mainMenu());
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send error message to chatId {}", chatId, e);
        }
    }
}
