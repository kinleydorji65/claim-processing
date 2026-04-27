package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialWithdrawalReasonUpdateDto;
import com.claim.claim_processing.common.service.partial.PartialReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partial-reasons")
@RequiredArgsConstructor
public class PartialReasonController {

    private final PartialReasonService service;

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<PartialWithdrawalReasonResponseDto> create(
            @RequestBody PartialWithdrawalReasonRequestDto requestDto) {

        return ResponseEntity.ok(service.create(requestDto));
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<PartialWithdrawalReasonResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // ================= GET BY CODE =================

    @GetMapping("/code/{code}")
    public ResponseEntity<PartialWithdrawalReasonResponseDto> getByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(service.getByCode(code));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<PartialWithdrawalReasonResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ================= GET ALL ACTIVE =================

    @GetMapping("/active")
    public ResponseEntity<List<PartialWithdrawalReasonResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<PartialWithdrawalReasonResponseDto> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalReasonUpdateDto updateDto) {

        return ResponseEntity.ok(service.update(id, updateDto));
    }

    // ================= DEACTIVATE (SOFT DELETE) =================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {

        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}