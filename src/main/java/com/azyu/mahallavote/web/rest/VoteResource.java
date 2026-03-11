package com.azyu.mahallavote.web.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.azyu.mahallavote.domain.enumeration.VoteStatus;
import com.azyu.mahallavote.repository.VoteRepository;
import com.azyu.mahallavote.service.VoteService;
import com.azyu.mahallavote.service.dto.VoteDTO;
import com.azyu.mahallavote.web.rest.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Arrays;
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
@RequestMapping("/api/admin/votes")
public class VoteResource {

    private static final Logger LOG = LoggerFactory.getLogger(VoteResource.class);

    private final VoteService voteService;
    private final VoteRepository voteRepository;

    public VoteResource(VoteService voteService, VoteRepository voteRepository) {
        this.voteService = voteService;
        this.voteRepository = voteRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoteDTO>> create(@Valid @RequestBody VoteDTO dto) {
        LOG.debug("REST request to save Vote : {}", dto);
        if (dto.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "A new Vote cannot already have an ID");
        }
        VoteDTO result = voteService.create(dto);
        return ResponseEntity.created(URI.create("/api/admin/votes/" + result.getId())).body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VoteDTO>> update(@PathVariable Long id, @Valid @RequestBody VoteDTO dto) {
        LOG.debug("REST request to update Vote : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!voteRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        VoteDTO result = voteService.update(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiResponse<VoteDTO>> partialUpdate(@PathVariable Long id, @NotNull @RequestBody VoteDTO dto) {
        LOG.debug("REST request to partial update Vote : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!voteRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        return voteService
            .partialUpdate(dto)
            .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoteDTO>>> getAll(Pageable pageable) {
        LOG.debug("REST request to get a page of Votes");
        Page<VoteDTO> page = voteService.findAll(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(ApiResponse.success(page.getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoteDTO>> getOne(@PathVariable Long id) {
        LOG.debug("REST request to get Vote : {}", id);
        return voteService
            .findOne(id)
            .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("REST request to delete Vote : {}", id);
        voteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status-list")
    public ResponseEntity<ApiResponse<List<String>>> getStatuses() {
        List<String> statuses = Arrays.stream(VoteStatus.values()).map(Enum::name).toList();
        return ResponseEntity.ok(ApiResponse.success(statuses));
    }
}
