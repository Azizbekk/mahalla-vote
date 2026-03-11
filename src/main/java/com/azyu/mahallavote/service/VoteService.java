package com.azyu.mahallavote.service;

import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.domain.Vote;
import com.azyu.mahallavote.domain.enumeration.VoteStatus;
import com.azyu.mahallavote.repository.VoteRepository;
import com.azyu.mahallavote.service.dto.VoteDTO;
import com.azyu.mahallavote.service.mapper.VoteMapper;
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
public class VoteService {

    private static final Logger LOG = LoggerFactory.getLogger(VoteService.class);

    private final VoteRepository voteRepository;
    private final ProjectSettingService projectSettingService;
    private final VoteMapper voteMapper;

    public VoteService(VoteRepository voteRepository, ProjectSettingService projectSettingService, VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.projectSettingService = projectSettingService;
        this.voteMapper = voteMapper;
    }

    public VoteDTO create(VoteDTO dto) {
        LOG.debug("Request to save Vote : {}", dto);
        Vote entity = voteMapper.toEntity(dto);
        entity = voteRepository.save(entity);
        return voteMapper.toDto(entity);
    }

    public VoteDTO update(VoteDTO dto) {
        LOG.debug("Request to update Vote : {}", dto);
        Vote entity = voteRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Vote", dto.getId()));
        voteMapper.partialUpdate(entity, dto);
        entity = voteRepository.save(entity);
        return voteMapper.toDto(entity);
    }

    public Optional<VoteDTO> partialUpdate(VoteDTO dto) {
        LOG.debug("Request to partially update Vote : {}", dto);
        return voteRepository
            .findById(dto.getId())
            .map(existing -> {
                voteMapper.partialUpdate(existing, dto);
                return existing;
            })
            .map(voteRepository::save)
            .map(voteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<VoteDTO> findOne(Long id) {
        LOG.debug("Request to get Vote : {}", id);
        return voteRepository.findById(id).map(voteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<VoteDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Votes");
        return voteRepository.findAll(pageable).map(voteMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Vote : {}", id);
        voteRepository.deleteById(id);
    }

    // --- Bot-specific methods ---

    public Vote createVote(TelegramUser voter, String phoneNumber) {
        Long amount = projectSettingService.getActiveValueAsLong("VOTE_AMOUNT", 0L);

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setPhoneNumber(phoneNumber);
        vote.setAmount(amount);
        vote.setStatus(VoteStatus.PENDING);
        LOG.info("Creating vote for user {} with phone {}", voter.getUserId(), phoneNumber);
        return voteRepository.save(vote);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return voteRepository.existsByPhoneNumber(phoneNumber);
    }

    public void markAsVoted(Long voteId) {
        voteRepository
            .findById(voteId)
            .ifPresent(vote -> {
                vote.setStatus(VoteStatus.VOTED);
                vote.setVotedAt(LocalDateTime.now());
                voteRepository.save(vote);
            });
    }

    public void markAsPaid(Long voteId) {
        voteRepository
            .findById(voteId)
            .ifPresent(vote -> {
                vote.setStatus(VoteStatus.PAID);
                vote.setPaidAt(LocalDateTime.now());
                voteRepository.save(vote);
            });
    }

    @Transactional(readOnly = true)
    public long getVoteCountByUser(Long voterId) {
        return voteRepository.countByVoterIdAndStatus(voterId, VoteStatus.VOTED);
    }
}
