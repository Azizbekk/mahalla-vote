package com.azyu.mahallavote.web.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.azyu.mahallavote.domain.enumeration.VoteLotStatus;
import com.azyu.mahallavote.repository.VoteLotRepository;
import com.azyu.mahallavote.service.VoteLotService;
import com.azyu.mahallavote.service.dto.VoteLotDTO;
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
@RequestMapping("/api")
public class VoteLotResource {

    private static final Logger LOG = LoggerFactory.getLogger(VoteLotResource.class);

    private final VoteLotService voteLotService;
    private final VoteLotRepository voteLotRepository;

    public VoteLotResource(VoteLotService voteLotService, VoteLotRepository voteLotRepository) {
        this.voteLotService = voteLotService;
        this.voteLotRepository = voteLotRepository;
    }

    @PostMapping("/admin/vote-lots")
    public ResponseEntity<ApiResponse<VoteLotDTO>> create(@Valid @RequestBody VoteLotDTO dto) {
        LOG.debug("REST request to save VoteLot : {}", dto);
        if (dto.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "A new VoteLot cannot already have an ID");
        }
        VoteLotDTO result = voteLotService.create(dto);
        return ResponseEntity.created(URI.create("/api/admin/vote-lots/" + result.getId())).body(ApiResponse.success(result));
    }

    @PutMapping("/admin/vote-lots/{id}")
    public ResponseEntity<ApiResponse<VoteLotDTO>> update(@PathVariable Long id, @Valid @RequestBody VoteLotDTO dto) {
        LOG.debug("REST request to update VoteLot : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!voteLotRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        VoteLotDTO result = voteLotService.update(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping(value = "/admin/vote-lots/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiResponse<VoteLotDTO>> partialUpdate(@PathVariable Long id, @NotNull @RequestBody VoteLotDTO dto) {
        LOG.debug("REST request to partial update VoteLot : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!voteLotRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        return voteLotService
            .partialUpdate(dto)
            .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/vote-lots")
    public ResponseEntity<ApiResponse<List<VoteLotDTO>>> getAll(Pageable pageable) {
        LOG.debug("REST request to get all VoteLots");
        Page<VoteLotDTO> page = voteLotService.findAll(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(ApiResponse.success(page.getContent()));
    }

    @GetMapping("/admin/vote-lots/{id}")
    public ResponseEntity<ApiResponse<VoteLotDTO>> getOne(@PathVariable Long id) {
        LOG.debug("REST request to get VoteLot : {}", id);
        return voteLotService
            .findOne(id)
            .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/vote-lots/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("REST request to delete VoteLot : {}", id);
        voteLotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/vote-lots/status-list")
    public ResponseEntity<ApiResponse<List<String>>> getStatuses() {
        List<String> statuses = Arrays.stream(VoteLotStatus.values()).map(Enum::name).toList();
        return ResponseEntity.ok(ApiResponse.success(statuses));
    }
}
