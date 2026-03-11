package com.azyu.mahallavote.bot;

import com.azyu.mahallavote.bot.handler.BalanceCommandHandler;
import com.azyu.mahallavote.bot.handler.ReferralCommandHandler;
import com.azyu.mahallavote.bot.handler.StartCommandHandler;
import com.azyu.mahallavote.bot.handler.VoteCommandHandler;
import com.azyu.mahallavote.service.ProjectSettingService;
import com.azyu.mahallavote.service.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    private static final Logger log = LoggerFactory.getLogger(BotConfig.class);

    private final ProjectSettingService projectSettingService;
    private final TelegramUserService telegramUserService;
    private final StartCommandHandler startCommandHandler;
    private final VoteCommandHandler voteCommandHandler;
    private final ReferralCommandHandler referralCommandHandler;
    private final BalanceCommandHandler balanceCommandHandler;

    public BotConfig(
        ProjectSettingService projectSettingService,
        TelegramUserService telegramUserService,
        StartCommandHandler startCommandHandler,
        VoteCommandHandler voteCommandHandler,
        ReferralCommandHandler referralCommandHandler,
        BalanceCommandHandler balanceCommandHandler
    ) {
        this.projectSettingService = projectSettingService;
        this.telegramUserService = telegramUserService;
        this.startCommandHandler = startCommandHandler;
        this.voteCommandHandler = voteCommandHandler;
        this.referralCommandHandler = referralCommandHandler;
        this.balanceCommandHandler = balanceCommandHandler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initBot() {
        try {
            String botToken = projectSettingService.getActiveValue("TELEGRAM_BOT_TOKEN").orElse("");
            String botUsername = projectSettingService.getActiveValue("TELEGRAM_BOT_USERNAME").orElse("");

            if (botToken.isBlank()) {
                log.warn("TELEGRAM_BOT_TOKEN is empty in app_setting, bot will not start");
                return;
            }

            MahallaVoteBotService botService = new MahallaVoteBotService(
                botToken,
                botUsername,
                telegramUserService,
                startCommandHandler,
                voteCommandHandler,
                referralCommandHandler,
                balanceCommandHandler
            );

            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(botService);
            log.info("Telegram bot registered: {}", botUsername);
        } catch (Exception e) {
            log.warn("Failed to initialize Telegram bot: {}", e.getMessage());
        }
    }
}
