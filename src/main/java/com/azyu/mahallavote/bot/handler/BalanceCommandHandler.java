package com.azyu.mahallavote.bot.handler;

import com.azyu.mahallavote.bot.KeyboardHelper;
import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.domain.Vote;
import com.azyu.mahallavote.domain.enumeration.ReferralStatus;
import com.azyu.mahallavote.domain.enumeration.VoteStatus;
import com.azyu.mahallavote.repository.ReferralRepository;
import com.azyu.mahallavote.repository.VoteRepository;
import com.azyu.mahallavote.service.ProjectSettingService;
import com.azyu.mahallavote.service.TelegramUserService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class BalanceCommandHandler {

    private final TelegramUserService telegramUserService;
    private final VoteRepository voteRepository;
    private final ReferralRepository referralRepository;
    private final ProjectSettingService projectSettingService;

    public BalanceCommandHandler(
        TelegramUserService telegramUserService,
        VoteRepository voteRepository,
        ReferralRepository referralRepository,
        ProjectSettingService projectSettingService
    ) {
        this.telegramUserService = telegramUserService;
        this.voteRepository = voteRepository;
        this.referralRepository = referralRepository;
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

        // Vote rewards
        List<Vote> votes = voteRepository.findByVoterId(user.getId());
        long voteEarnings = votes
            .stream()
            .filter(v -> v.getStatus() == VoteStatus.VOTED || v.getStatus() == VoteStatus.PAID)
            .mapToLong(Vote::getAmount)
            .sum();

        // Referral rewards
        long votedReferrals = referralRepository.countByReferrerIdAndStatus(user.getId(), ReferralStatus.VOTED);
        long paidReferrals = referralRepository.countByReferrerIdAndStatus(user.getId(), ReferralStatus.PAID);
        long referralRewardPerVote = projectSettingService.getActiveValueAsLong("REFERRAL_VOTE_AMOUNT", 0L);
        long referralEarnings = (votedReferrals + paidReferrals) * referralRewardPerVote;

        long totalBalance = voteEarnings + referralEarnings;

        String text = String.format(
            "\uD83D\uDCB0 Sizning balansingiz\n\n" +
            "\uD83D\uDDF3 Ovozlar: %d ta → %d so'm\n" +
            "\uD83D\uDC65 Referallar: %d ta → %d so'm\n\n" +
            "\uD83D\uDCB5 Jami: %d so'm",
            votes.size(),
            voteEarnings,
            votedReferrals + paidReferrals,
            referralEarnings,
            totalBalance
        );

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(text);
        response.setReplyMarkup(KeyboardHelper.mainMenu());
        return response;
    }
}
