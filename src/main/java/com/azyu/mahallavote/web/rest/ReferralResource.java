package com.azyu.mahallavote.web.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.azyu.mahallavote.domain.enumeration.ReferralStatus;
import com.azyu.mahallavote.repository.ReferralRepository;
import com.azyu.mahallavote.service.ReferralService;
import com.azyu.mahallavote.service.dto.ReferralDTO;
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
@RequestMapping("/api/admin/referrals")
public class ReferralResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReferralResource.class);

    private final ReferralService referralService;
    private final ReferralRepository referralRepository;

    public ReferralResource(ReferralService referralService, ReferralRepository referralRepository) {
        this.referralService = referralService;
        this.referralRepository = referralRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReferralDTO>> create(@Valid @RequestBody ReferralDTO dto) {
        LOG.debug("REST request to save Referral : {}", dto);
        if (dto.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "A new Referral cannot already have an ID");
        }
        ReferralDTO result = referralService.create(dto);
        return ResponseEntity.created(URI.create("/api/admin/referrals/" + result.getId())).body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferralDTO>> update(@PathVariable Long id, @Valid @RequestBody ReferralDTO dto) {
        LOG.debug("REST request to update Referral : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!referralRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        ReferralDTO result = referralService.update(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiResponse<ReferralDTO>> partialUpdate(@PathVariable Long id, @NotNull @RequestBody ReferralDTO dto) {
        LOG.debug("REST request to partial update Referral : {}, {}", id, dto);
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid ID");
        }
        if (!referralRepository.existsById(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "Entity not found");
        }
        return referralService
            .partialUpdate(dto)
            .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReferralDTO>>> getAll(Pageable pageable) {
        LOG.debug("REST request to get a page of Referrals");
        Page<ReferralDTO> page = referralService.findAll(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(ApiResponse.success(page.getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferralDTO>> getOne(@PathVariable Long id) {
        LOG.debug("REST request to get Referral : {}", id);
        return referralService
            .findOne(id)
            .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("REST request to delete Referral : {}", id);
        referralService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status-list")
    public ResponseEntity<ApiResponse<List<String>>> getStatuses() {
        List<String> statuses = Arrays.stream(ReferralStatus.values()).map(Enum::name).toList();
        return ResponseEntity.ok(ApiResponse.success(statuses));
    }
}
