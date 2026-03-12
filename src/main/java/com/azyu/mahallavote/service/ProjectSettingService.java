package com.azyu.mahallavote.service;

import com.azyu.mahallavote.domain.ProjectSetting;
import com.azyu.mahallavote.domain.enumeration.ProjectSettingStatus;
import com.azyu.mahallavote.repository.ProjectSettingRepository;
import com.azyu.mahallavote.service.dto.ProjectSettingDTO;
import com.azyu.mahallavote.service.mapper.ProjectSettingMapper;
import com.azyu.mahallavote.web.rest.errors.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectSettingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectSettingService.class);

    private final ProjectSettingRepository projectSettingRepository;
    private final ProjectSettingMapper projectSettingMapper;

    // In-memory cache: settingKey → settingValue
    private final Map<String, Optional<String>> cache = new ConcurrentHashMap<>();

    public ProjectSettingService(ProjectSettingRepository projectSettingRepository, ProjectSettingMapper projectSettingMapper) {
        this.projectSettingRepository = projectSettingRepository;
        this.projectSettingMapper = projectSettingMapper;
    }

    public ProjectSettingDTO create(ProjectSettingDTO dto) {
        LOG.debug("Request to save ProjectSetting : {}", dto);
        ProjectSetting entity = projectSettingMapper.toEntity(dto);
        entity = projectSettingRepository.save(entity);
        cache.clear();
        return projectSettingMapper.toDto(entity);
    }

    public ProjectSettingDTO update(ProjectSettingDTO dto) {
        LOG.debug("Request to update ProjectSetting : {}", dto);
        ProjectSetting entity = projectSettingRepository
            .findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("ProjectSetting", dto.getId()));
        projectSettingMapper.partialUpdate(entity, dto);
        entity = projectSettingRepository.save(entity);
        cache.clear();
        return projectSettingMapper.toDto(entity);
    }

    public Optional<ProjectSettingDTO> partialUpdate(ProjectSettingDTO dto) {
        LOG.debug("Request to partially update ProjectSetting : {}", dto);
        Optional<ProjectSettingDTO> result = projectSettingRepository
            .findById(dto.getId())
            .map(existing -> {
                projectSettingMapper.partialUpdate(existing, dto);
                return existing;
            })
            .map(projectSettingRepository::save)
            .map(projectSettingMapper::toDto);
        cache.clear();
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<ProjectSettingDTO> findOne(Long id) {
        LOG.debug("Request to get ProjectSetting : {}", id);
        return projectSettingRepository.findById(id).map(projectSettingMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProjectSettingDTO> findAll() {
        LOG.debug("Request to get all ProjectSettings");
        return projectSettingRepository.findAll().stream().map(projectSettingMapper::toDto).toList();
    }

    public void delete(Long id) {
        LOG.debug("Request to delete ProjectSetting : {}", id);
        projectSettingRepository.deleteById(id);
        cache.clear();
    }

    // --- Helper methods for bot and internal usage ---

    @Transactional(readOnly = true)
    public Optional<String> getActiveValue(String settingKey) {
        return cache.computeIfAbsent(settingKey, key ->
            projectSettingRepository.findBySettingKeyAndStatus(key, ProjectSettingStatus.ACTIVE).map(ProjectSetting::getSettingValue)
        );
    }

    @Transactional(readOnly = true)
    public Long getActiveValueAsLong(String settingKey, Long defaultValue) {
        return getActiveValue(settingKey)
            .map(value -> {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    LOG.warn("Cannot parse setting '{}' value '{}' as Long, using default: {}", settingKey, value, defaultValue);
                    return defaultValue;
                }
            })
            .orElse(defaultValue);
    }
}
