package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reserve-accounts")
@RequiredArgsConstructor
public class ReserveAccountController {

    private final ReserveAccountService service;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ReserveAccountResponseDto>> create(
            @RequestBody ReserveAccountRequestDto dto
    ) {
        log.info("Received request to create Reserve Account for member: {}", dto.getMemberCode());
        ApiResponseDTO<ReserveAccountResponseDto> response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ReserveAccountResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ReserveAccountRequestDto dto
    ) {
        log.info("Received request to update Reserve Account with ID: {}", id);
        ApiResponseDTO<ReserveAccountResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ReserveAccountResponseDto>> getById(
            @PathVariable Long id
    ) {
        log.info("Received request to fetch Reserve Account with ID: {}", id);
        ApiResponseDTO<ReserveAccountResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> getAll() {
        log.info("Received request to fetch all Reserve Accounts");
        ApiResponseDTO<List<ReserveAccountResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // DELETE (Soft Delete)
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(
            @PathVariable Long id
    ) {
        log.info("Received request to delete Reserve Account with ID: {}", id);
        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY NPPF NUMBER
    // -------------------------------
    @GetMapping("/by-nppf/{nppfNumber}")
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> getByNppfNumber(
            @PathVariable String nppfNumber
    ) {
        log.info("Received request to fetch Reserve Accounts by NPPF: {}", nppfNumber);
        ApiResponseDTO<List<ReserveAccountResponseDto>> response = service.getByNppfNumber(nppfNumber);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY IDENTITY NUMBER
    // -------------------------------
    @GetMapping("/by-identity/{identityNumber}")
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> getByIdentityNumber(
            @PathVariable String identityNumber
    ) {
        log.info("Received request to fetch Reserve Accounts by Identity: {}", identityNumber);
        ApiResponseDTO<List<ReserveAccountResponseDto>> response = service.getByIdentityNumber(identityNumber);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY STATUS
    // -------------------------------
    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> getByStatus(
            @PathVariable String status
    ) {
        log.info("Received request to fetch Reserve Accounts by Status: {}", status);
        ApiResponseDTO<List<ReserveAccountResponseDto>> response = service.getByStatus(status);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY ACCOUNT CODE
    // -------------------------------
    @GetMapping("/by-account-code/{accountCode}")
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> getByAccountCode(
            @PathVariable String accountCode
    ) {
        log.info("Received request to fetch Reserve Accounts by Account Code: {}", accountCode);
        ApiResponseDTO<List<ReserveAccountResponseDto>> response = service.getByAccountCode(accountCode);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // RELEASE AMOUNT FROM RESERVE
    // -------------------------------
    @PatchMapping("/{id}/release")
    public ResponseEntity<ApiResponseDTO<ReserveAccountResponseDto>> releaseAmount(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam String releasedBy,
            @RequestParam String releaseReference
    ) {
        log.info("Received request to release {} from Reserve Account ID: {}", amount, id);
        ApiResponseDTO<ReserveAccountResponseDto> response = service.releaseAmount(
                id, amount, releasedBy, releaseReference
        );
        return ResponseEntity.ok(response);
    }
}