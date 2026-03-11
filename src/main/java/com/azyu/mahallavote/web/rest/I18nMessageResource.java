package com.azyu.mahallavote.web.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.azyu.mahallavote.domain.enumeration.I18nMessageStatus;
import com.azyu.mahallavote.repository.I18nMessageRepository;
import com.azyu.mahallavote.service.I18nMessageService;
import com.azyu.mahallavote.service.dto.I18nMessageDTO;
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
@RequestMapping("/api")
public class I18nMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(I18nMessageResource.class);

    private final I18nMessageService i18nMessageService;
    private final I18nMessageRepository i18nMessageRepository;

    public I18nMessageResource(I18nMessageService i18nMessageService, I18nMessageRepository i18nMessageRepository) {
        this.i18nMessageService = i18nMessageService;
        this.i18nMessageRepository = i18nMessageRepository;
    }

    // ---- Public endpoint (no auth required) ----

    @GetMapping("/public/i18n-messages/all")
    public ResponseEntity<ApiResponse<List<I18nMessageDTO>>> getAllForLang(@RequestParam(defaultValue = "uzLat") String lang) {
        LOG.debug("REST request to get all active I18nMessages for lang: {}", lang);
        return ResponseEntity.ok(ApiResponse.success(i18nMessageService.findAllActiveForLang(lang)));
    }

    // ---- Admin CRUD endpoints ----

    @PostMapping("/admin/i18n-messages")
    public ResponseEntity<ApiResponse<I18nMessageDTO>> create(@Valid @RequestBody I18nMessageDTO dto) {
        LOG.debug("REST request to save I18nMessage : {}", dto);
        if (dto.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "A new I18nMessage cannot already have an ID");
        }
        I18nMessageDTO result = i18nMessageService.create(dto);
        return ResponseEntity.created(URI.create("/api/admin/i18n-messages/" + result.getId())).body(ApiResponse.success(result));
    }

    @PutMapping("/admin/i18n-messages/{id}")
    public ResponseEntity<ApiResponse<I18nMessageDTO>> update(@PathVariable Long id, @Valid @RequestBody I18nMessageDTO dto) {
        LOG.debug("REST request to update I18nMessage : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!i18nMessageRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        I18nMessageDTO result = i18nMessageService.update(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping(value = "/admin/i18n-messages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiResponse<I18nMessageDTO>> partialUpdate(@PathVariable Long id, @NotNull @RequestBody I18nMessageDTO dto) {
        LOG.debug("REST request to partial update I18nMessage : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!i18nMessageRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        return i18nMessageService
            .partialUpdate(dto)
            .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/i18n-messages")
    public ResponseEntity<ApiResponse<List<I18nMessageDTO>>> getAll() {
        LOG.debug("REST request to get all I18nMessages");
        return ResponseEntity.ok(ApiResponse.success(i18nMessageService.findAll()));
    }

    @GetMapping("/admin/i18n-messages/{id}")
    public ResponseEntity<ApiResponse<I18nMessageDTO>> getOne(@PathVariable Long id) {
        LOG.debug("REST request to get I18nMessage : {}", id);
        return i18nMessageService
            .findOne(id)
            .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/i18n-messages/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("REST request to delete I18nMessage : {}", id);
        i18nMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/i18n-messages/status-list")
    public ResponseEntity<ApiResponse<List<String>>> getStatuses() {
        List<String> statuses = Arrays.stream(I18nMessageStatus.values()).map(Enum::name).toList();
        return ResponseEntity.ok(ApiResponse.success(statuses));
    }
}
