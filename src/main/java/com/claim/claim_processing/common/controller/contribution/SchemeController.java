package com.claim.claim_processing.common.controller.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.SchemeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.contribution.SchemeUpdateRequestDto;
import com.claim.claim_processing.common.service.contribution.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/schemes")
@RequiredArgsConstructor
public class SchemeController {

    private final SchemeService schemeService;

    @GetMapping
    public ResponseEntity<List<SchemeTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(schemeService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchemeTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schemeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SchemeTypeResponseDto> create(
            @RequestBody SchemeCreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schemeService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchemeTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody SchemeUpdateRequestDto requestDto) {
        return ResponseEntity.ok(schemeService.update(id, requestDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        schemeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
