package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.TelegramUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByUserId(Long userId);

    Optional<TelegramUser> findByChatId(Long chatId);

    Optional<TelegramUser> findByUsername(String username);

    Optional<TelegramUser> findByPhoneNumber(String phoneNumber);
}
