package com.azyu.mahallavote.service;

import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.domain.enumeration.TelegramUserState;
import com.azyu.mahallavote.repository.TelegramUserRepository;
import com.azyu.mahallavote.service.dto.TelegramUserDTO;
import com.azyu.mahallavote.service.mapper.TelegramUserMapper;
import com.azyu.mahallavote.web.rest.errors.ResourceNotFoundException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
@Transactional
public class TelegramUserService {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramUserService.class);

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramUserMapper telegramUserMapper;

    public TelegramUserService(TelegramUserRepository telegramUserRepository, TelegramUserMapper telegramUserMapper) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramUserMapper = telegramUserMapper;
    }

    public TelegramUserDTO create(TelegramUserDTO dto) {
        LOG.debug("Request to save TelegramUser : {}", dto);
        TelegramUser entity = telegramUserMapper.toEntity(dto);
        entity = telegramUserRepository.save(entity);
        return telegramUserMapper.toDto(entity);
    }

    public TelegramUserDTO update(TelegramUserDTO dto) {
        LOG.debug("Request to update TelegramUser : {}", dto);
        TelegramUser entity = telegramUserRepository
            .findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("TelegramUser", dto.getId()));
        telegramUserMapper.partialUpdate(entity, dto);
        entity = telegramUserRepository.save(entity);
        return telegramUserMapper.toDto(entity);
    }

    public Optional<TelegramUserDTO> partialUpdate(TelegramUserDTO dto) {
        LOG.debug("Request to partially update TelegramUser : {}", dto);
        return telegramUserRepository
            .findById(dto.getId())
            .map(existing -> {
                telegramUserMapper.partialUpdate(existing, dto);
                return existing;
            })
            .map(telegramUserRepository::save)
            .map(telegramUserMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<TelegramUserDTO> findOne(Long id) {
        LOG.debug("Request to get TelegramUser : {}", id);
        return telegramUserRepository.findById(id).map(telegramUserMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TelegramUserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TelegramUsers");
        return telegramUserRepository.findAll(pageable).map(telegramUserMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete TelegramUser : {}", id);
        telegramUserRepository.deleteById(id);
    }

    // --- Bot-specific methods ---

    public TelegramUser registerUserIfNotExists(User tgUser, Long chatId) {
        Optional<TelegramUser> existing = telegramUserRepository.findByUserId(tgUser.getId());
        if (existing.isPresent()) {
            return existing.get();
        }
        TelegramUser user = new TelegramUser();
        user.setUserId(tgUser.getId());
        user.setChatId(chatId);
        user.setUsername(tgUser.getUserName());
        user.setFirstName(tgUser.getFirstName());
        user.setLastName(tgUser.getLastName());
        user.setLanguageCode(tgUser.getLanguageCode());
        user.setState(TelegramUserState.HOME);
        LOG.info("Registering new Telegram user: {} (chatId: {})", tgUser.getId(), chatId);
        return telegramUserRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<TelegramUser> findByChatId(Long chatId) {
        return telegramUserRepository.findByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public Optional<TelegramUser> findByUserId(Long userId) {
        return telegramUserRepository.findByUserId(userId);
    }

    public void updateState(Long chatId, TelegramUserState state) {
        telegramUserRepository
            .findByChatId(chatId)
            .ifPresent(user -> {
                user.setState(state);
                telegramUserRepository.save(user);
                LOG.debug("Updated state for chatId {}: {}", chatId, state);
            });
    }

    public void updatePhoneNumber(Long chatId, String phoneNumber) {
        telegramUserRepository
            .findByChatId(chatId)
            .ifPresent(user -> {
                user.setPhoneNumber(phoneNumber);
                telegramUserRepository.save(user);
            });
    }
}
