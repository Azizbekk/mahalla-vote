package com.azyu.mahallavote.web.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.azyu.mahallavote.domain.enumeration.ProjectSettingStatus;
import com.azyu.mahallavote.domain.enumeration.ProjectSettingValueType;
import com.azyu.mahallavote.repository.ProjectSettingRepository;
import com.azyu.mahallavote.service.ProjectSettingService;
import com.azyu.mahallavote.service.dto.ProjectSettingDTO;
import com.azyu.mahallavote.web.rest.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/project-settings")
public class ProjectSettingResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectSettingResource.class);

    private final ProjectSettingService projectSettingService;
    private final ProjectSettingRepository projectSettingRepository;

    public ProjectSettingResource(ProjectSettingService projectSettingService, ProjectSettingRepository projectSettingRepository) {
        this.projectSettingService = projectSettingService;
        this.projectSettingRepository = projectSettingRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectSettingDTO>> create(@Valid @RequestBody ProjectSettingDTO dto) {
        LOG.debug("REST request to save ProjectSetting : {}", dto);
        if (dto.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "A new ProjectSetting cannot already have an ID");
        }
        ProjectSettingDTO result = projectSettingService.create(dto);
        return ResponseEntity.created(URI.create("/api/admin/project-settings/" + result.getId())).body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectSettingDTO>> update(@PathVariable Long id, @Valid @RequestBody ProjectSettingDTO dto) {
        LOG.debug("REST request to update ProjectSetting : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!projectSettingRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        ProjectSettingDTO result = projectSettingService.update(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiResponse<ProjectSettingDTO>> partialUpdate(
        @PathVariable Long id,
        @NotNull @RequestBody ProjectSettingDTO dto
    ) {
        LOG.debug("REST request to partial update ProjectSetting : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!projectSettingRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        return projectSettingService
            .partialUpdate(dto)
            .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectSettingDTO>>> getAll() {
        LOG.debug("REST request to get all ProjectSettings");
        return ResponseEntity.ok(ApiResponse.success(projectSettingService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectSettingDTO>> getOne(@PathVariable Long id) {
        LOG.debug("REST request to get ProjectSetting : {}", id);
        return projectSettingService
            .findOne(id)
            .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("REST request to delete ProjectSetting : {}", id);
        projectSettingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/value-type-list")
    public ResponseEntity<ApiResponse<List<String>>> getValueTypes() {
        List<String> types = Arrays.stream(ProjectSettingValueType.values()).map(Enum::name).toList();
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/status-list")
    public ResponseEntity<ApiResponse<List<String>>> getStatuses() {
        List<String> statuses = Arrays.stream(ProjectSettingStatus.values()).map(Enum::name).toList();
        return ResponseEntity.ok(ApiResponse.success(statuses));
    }
}
