package com.azyu.mahallavote.web.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.azyu.mahallavote.repository.TelegramUserRepository;
import com.azyu.mahallavote.service.TelegramUserService;
import com.azyu.mahallavote.service.dto.TelegramUserDTO;
import com.azyu.mahallavote.web.rest.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/telegram-users")
public class TelegramUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramUserResource.class);

    private final TelegramUserService telegramUserService;
    private final TelegramUserRepository telegramUserRepository;

    public TelegramUserResource(TelegramUserService telegramUserService, TelegramUserRepository telegramUserRepository) {
        this.telegramUserService = telegramUserService;
        this.telegramUserRepository = telegramUserRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TelegramUserDTO>> create(@Valid @RequestBody TelegramUserDTO dto) {
        LOG.debug("REST request to save TelegramUser : {}", dto);
        if (dto.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "A new TelegramUser cannot already have an ID");
        }
        TelegramUserDTO result = telegramUserService.create(dto);
        return ResponseEntity.created(URI.create("/api/admin/telegram-users/" + result.getId())).body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TelegramUserDTO>> update(@PathVariable Long id, @Valid @RequestBody TelegramUserDTO dto) {
        LOG.debug("REST request to update TelegramUser : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!telegramUserRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        TelegramUserDTO result = telegramUserService.update(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiResponse<TelegramUserDTO>> partialUpdate(@PathVariable Long id, @NotNull @RequestBody TelegramUserDTO dto) {
        LOG.debug("REST request to partial update TelegramUser : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!telegramUserRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        return telegramUserService
            .partialUpdate(dto)
            .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TelegramUserDTO>>> getAll(Pageable pageable) {
        LOG.debug("REST request to get a page of TelegramUsers");
        Page<TelegramUserDTO> page = telegramUserService.findAll(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(ApiResponse.success(page.getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TelegramUserDTO>> getOne(@PathVariable Long id) {
        LOG.debug("REST request to get TelegramUser : {}", id);
        return telegramUserService
            .findOne(id)
            .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("REST request to delete TelegramUser : {}", id);
        telegramUserService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
