package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims/reserve-accounts")
@RequiredArgsConstructor
public class ReserveAccountController {

    private final ReserveAccountService service;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    public ResponseEntity<ReserveAccountResponseDto> create(
            @RequestBody ReserveAccountRequestDto dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ReserveAccountResponseDto> update(
            @PathVariable Long id,
            @RequestBody ReserveAccountRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ReserveAccountResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<List<ReserveAccountResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------
    // FILTER BY ACCOUNT TYPE
    // -------------------------------
    @GetMapping("/by-account-type/{accountTypeId}")
    public ResponseEntity<List<ReserveAccountResponseDto>> getByAccountType(
            @PathVariable Long accountTypeId
    ) {
        return ResponseEntity.ok(service.getByAccountTypeId(accountTypeId));
    }

    // -------------------------------
    // FILTER BY SCHEME TYPE
    // -------------------------------
    @GetMapping("/by-scheme-type/{schemeTypeId}")
    public ResponseEntity<List<ReserveAccountResponseDto>> getBySchemeType(
            @PathVariable Long schemeTypeId
    ) {
        return ResponseEntity.ok(service.getBySchemeTypeId(schemeTypeId));
    }
}