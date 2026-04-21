package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.TerminationReasonCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.TerminationReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.TerminationReasonUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.TerminationReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/termination-reasons")
@RequiredArgsConstructor
public class TerminationReasonController {

    private final TerminationReasonService terminationReasonService;

    @GetMapping
    public ResponseEntity<List<TerminationReasonResponseDto>> getAllActive() {
        return ResponseEntity.ok(terminationReasonService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TerminationReasonResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(terminationReasonService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TerminationReasonResponseDto> create(
            @RequestBody TerminationReasonCreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(terminationReasonService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TerminationReasonResponseDto> update(
            @PathVariable Long id,
            @RequestBody TerminationReasonUpdateRequestDto requestDto) {
        return ResponseEntity.ok(terminationReasonService.update(id, requestDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        terminationReasonService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
