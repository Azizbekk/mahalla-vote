package com.azyu.mahallavote.service;

import com.azyu.mahallavote.domain.Referral;
import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.domain.enumeration.ReferralStatus;
import com.azyu.mahallavote.repository.ReferralRepository;
import com.azyu.mahallavote.repository.TelegramUserRepository;
import com.azyu.mahallavote.service.dto.ReferralDTO;
import com.azyu.mahallavote.service.mapper.ReferralMapper;
import com.azyu.mahallavote.web.rest.errors.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReferralService {

    private static final Logger LOG = LoggerFactory.getLogger(ReferralService.class);

    private final ReferralRepository referralRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final ReferralMapper referralMapper;

    public ReferralService(
        ReferralRepository referralRepository,
        TelegramUserRepository telegramUserRepository,
        ReferralMapper referralMapper
    ) {
        this.referralRepository = referralRepository;
        this.telegramUserRepository = telegramUserRepository;
        this.referralMapper = referralMapper;
    }

    public ReferralDTO create(ReferralDTO dto) {
        LOG.debug("Request to save Referral : {}", dto);
        Referral entity = referralMapper.toEntity(dto);
        entity = referralRepository.save(entity);
        return referralMapper.toDto(entity);
    }

    public ReferralDTO update(ReferralDTO dto) {
        LOG.debug("Request to update Referral : {}", dto);
        Referral entity = referralRepository
            .findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Referral", dto.getId()));
        referralMapper.partialUpdate(entity, dto);
        entity = referralRepository.save(entity);
        return referralMapper.toDto(entity);
    }

    public Optional<ReferralDTO> partialUpdate(ReferralDTO dto) {
        LOG.debug("Request to partially update Referral : {}", dto);
        return referralRepository
            .findById(dto.getId())
            .map(existing -> {
                referralMapper.partialUpdate(existing, dto);
                return existing;
            })
            .map(referralRepository::save)
            .map(referralMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReferralDTO> findOne(Long id) {
        LOG.debug("Request to get Referral : {}", id);
        return referralRepository.findById(id).map(referralMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ReferralDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Referrals");
        return referralRepository.findAll(pageable).map(referralMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Referral : {}", id);
        referralRepository.deleteById(id);
    }

    // --- Bot-specific methods ---

    public Optional<Referral> registerReferral(Long referrerUserId, TelegramUser referred) {
        if (referralRepository.existsByReferredId(referred.getId())) {
            LOG.debug("Referral already exists for referred user: {}", referred.getId());
            return Optional.empty();
        }
        Optional<TelegramUser> referrerOpt = telegramUserRepository.findByUserId(referrerUserId);
        if (referrerOpt.isEmpty()) {
            LOG.warn("Referrer user not found: {}", referrerUserId);
            return Optional.empty();
        }
        TelegramUser referrer = referrerOpt.get();
        if (referrer.getId().equals(referred.getId())) {
            LOG.debug("Self-referral attempt blocked for user: {}", referrerUserId);
            return Optional.empty();
        }

        Referral referral = new Referral();
        referral.setReferrer(referrer);
        referral.setReferred(referred);
        referral.setStatus(ReferralStatus.REGISTERED);
        LOG.info("Referral created: referrer={}, referred={}", referrerUserId, referred.getUserId());
        return Optional.of(referralRepository.save(referral));
    }

    public void markAsVoted(Long referredId) {
        referralRepository
            .findByReferredId(referredId)
            .ifPresent(referral -> {
                referral.setStatus(ReferralStatus.VOTED);
                referral.setVotedAt(LocalDateTime.now());
                referralRepository.save(referral);
            });
    }

    public void markAsPaid(Long referralId) {
        referralRepository
            .findById(referralId)
            .ifPresent(referral -> {
                referral.setStatus(ReferralStatus.PAID);
                referral.setPaidAt(LocalDateTime.now());
                referralRepository.save(referral);
            });
    }

    @Transactional(readOnly = true)
    public long getReferralCount(Long referrerId) {
        return referralRepository.countByReferrerId(referrerId);
    }

    @Transactional(readOnly = true)
    public long getVotedReferralCount(Long referrerId) {
        return referralRepository.countByReferrerIdAndStatus(referrerId, ReferralStatus.VOTED);
    }
}
