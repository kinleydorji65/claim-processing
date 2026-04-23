package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.SubmissionChannelRequestDto;
import com.claim.claim_processing.common.DTO.response.common.SubmissionChannelResponseDto;
import com.claim.claim_processing.common.DTO.update.common.SubmissionChannelUpdateDto;
import com.claim.claim_processing.common.service.common.SubmissionChannelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Submission Channel", description = "Submission channel master APIs")
@RestController
@RequestMapping("/api/claim/masters/submission-channels")
@RequiredArgsConstructor
public class SubmissionChannelController {

    private final SubmissionChannelService service;

    @GetMapping
    public ResponseEntity<List<SubmissionChannelResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionChannelResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SubmissionChannelResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @PostMapping
    public ResponseEntity<SubmissionChannelResponseDto> create(
            @RequestBody SubmissionChannelRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(requestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubmissionChannelResponseDto> update(
            @PathVariable Long id,
            @RequestBody SubmissionChannelUpdateDto updateDto) {
        return ResponseEntity.ok(service.update(id, updateDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}