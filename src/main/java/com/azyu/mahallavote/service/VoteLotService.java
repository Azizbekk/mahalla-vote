package com.azyu.mahallavote.service;

import com.azyu.mahallavote.domain.VoteLot;
import com.azyu.mahallavote.domain.enumeration.VoteLotStatus;
import com.azyu.mahallavote.repository.VoteLotRepository;
import com.azyu.mahallavote.service.dto.VoteLotDTO;
import com.azyu.mahallavote.service.mapper.VoteLotMapper;
import com.azyu.mahallavote.web.rest.errors.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteLotService {

    private static final Logger LOG = LoggerFactory.getLogger(VoteLotService.class);

    private final VoteLotRepository voteLotRepository;
    private final VoteLotMapper voteLotMapper;

    public VoteLotService(VoteLotRepository voteLotRepository, VoteLotMapper voteLotMapper) {
        this.voteLotRepository = voteLotRepository;
        this.voteLotMapper = voteLotMapper;
    }

    public VoteLotDTO create(VoteLotDTO dto) {
        LOG.debug("Request to save VoteLot : {}", dto);
        VoteLot entity = voteLotMapper.toEntity(dto);
        entity.setCurrentVoteCount(0);
        entity = voteLotRepository.save(entity);
        return voteLotMapper.toDto(entity);
    }

    public VoteLotDTO update(VoteLotDTO dto) {
        LOG.debug("Request to update VoteLot : {}", dto);
        VoteLot entity = voteLotRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("VoteLot", dto.getId()));
        voteLotMapper.partialUpdate(entity, dto);
        entity = voteLotRepository.save(entity);
        return voteLotMapper.toDto(entity);
    }

    public Optional<VoteLotDTO> partialUpdate(VoteLotDTO dto) {
        LOG.debug("Request to partially update VoteLot : {}", dto);
        return voteLotRepository
            .findById(dto.getId())
            .map(existing -> {
                voteLotMapper.partialUpdate(existing, dto);
                return existing;
            })
            .map(voteLotRepository::save)
            .map(voteLotMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<VoteLotDTO> findOne(Long id) {
        LOG.debug("Request to get VoteLot : {}", id);
        return voteLotRepository.findById(id).map(voteLotMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<VoteLotDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all VoteLots");
        return voteLotRepository.findAll(pageable).map(voteLotMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete VoteLot : {}", id);
        voteLotRepository.deleteById(id);
    }

    // --- Bot-specific methods ---

    @Transactional(readOnly = true)
    public Optional<VoteLot> findActiveLot() {
        return voteLotRepository.findFirstByStatusOrderByIdDesc(VoteLotStatus.ACTIVE);
    }

    public void incrementVoteCount(Long lotId) {
        VoteLot lot = voteLotRepository.findById(lotId).orElseThrow(() -> new ResourceNotFoundException("VoteLot", lotId));
        lot.setCurrentVoteCount(lot.getCurrentVoteCount() + 1);
        if (lot.getCurrentVoteCount() >= lot.getTargetVoteCount()) {
            lot.setStatus(VoteLotStatus.COMPLETED);
            LOG.info("VoteLot {} completed: {}/{}", lotId, lot.getCurrentVoteCount(), lot.getTargetVoteCount());
        }
        voteLotRepository.save(lot);
    }
}
