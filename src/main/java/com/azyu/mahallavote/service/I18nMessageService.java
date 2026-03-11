package com.azyu.mahallavote.service;

import com.azyu.mahallavote.domain.I18nMessage;
import com.azyu.mahallavote.domain.enumeration.I18nMessageStatus;
import com.azyu.mahallavote.repository.I18nMessageRepository;
import com.azyu.mahallavote.service.dto.I18nMessageDTO;
import com.azyu.mahallavote.service.mapper.I18nMessageMapper;
import com.azyu.mahallavote.web.rest.errors.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class I18nMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(I18nMessageService.class);

    private final I18nMessageRepository i18nMessageRepository;
    private final I18nMessageMapper i18nMessageMapper;

    public I18nMessageService(I18nMessageRepository i18nMessageRepository, I18nMessageMapper i18nMessageMapper) {
        this.i18nMessageRepository = i18nMessageRepository;
        this.i18nMessageMapper = i18nMessageMapper;
    }

    public I18nMessageDTO create(I18nMessageDTO dto) {
        LOG.debug("Request to save I18nMessage : {}", dto);
        I18nMessage entity = i18nMessageMapper.toEntity(dto);
        entity = i18nMessageRepository.save(entity);
        return i18nMessageMapper.toDto(entity);
    }

    public I18nMessageDTO update(I18nMessageDTO dto) {
        LOG.debug("Request to update I18nMessage : {}", dto);
        I18nMessage entity = i18nMessageRepository
            .findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("I18nMessage", dto.getId()));
        i18nMessageMapper.partialUpdate(entity, dto);
        entity = i18nMessageRepository.save(entity);
        return i18nMessageMapper.toDto(entity);
    }

    public Optional<I18nMessageDTO> partialUpdate(I18nMessageDTO dto) {
        LOG.debug("Request to partially update I18nMessage : {}", dto);
        return i18nMessageRepository
            .findById(dto.getId())
            .map(existing -> {
                i18nMessageMapper.partialUpdate(existing, dto);
                return existing;
            })
            .map(i18nMessageRepository::save)
            .map(i18nMessageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<I18nMessageDTO> findOne(Long id) {
        LOG.debug("Request to get I18nMessage : {}", id);
        return i18nMessageRepository.findById(id).map(i18nMessageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<I18nMessageDTO> findAll() {
        LOG.debug("Request to get all I18nMessages");
        return i18nMessageRepository.findAll().stream().map(i18nMessageMapper::toDto).toList();
    }

    public void delete(Long id) {
        LOG.debug("Request to delete I18nMessage : {}", id);
        i18nMessageRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<I18nMessageDTO> findAllActiveForLang(String langKey) {
        LOG.debug("Request to get all active I18nMessages for lang: {}", langKey);
        return i18nMessageRepository
            .findAllByStatusOrderBySortOrderAsc(I18nMessageStatus.ACTIVE)
            .stream()
            .map(entity -> {
                I18nMessageDTO dto = new I18nMessageDTO();
                dto.setId(entity.getId());
                dto.setShortCode(entity.getShortCode());
                dto.setSortOrder(entity.getSortOrder());
                if (entity.getLocalizedName() != null) {
                    dto.setName(entity.getLocalizedName().getByLangKey(langKey));
                }
                return dto;
            })
            .toList();
    }
}
