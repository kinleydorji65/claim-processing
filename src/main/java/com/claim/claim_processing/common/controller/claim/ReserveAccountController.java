package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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
    public ResponseEntity<?> create(
            @RequestBody ReserveAccountRequestDto dto
    ) {

        ApiResponseDTO<ReserveAccountResponseDto> response =
                service.create(dto);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ReserveAccountRequestDto dto
    ) {

        ApiResponseDTO<ReserveAccountResponseDto> response =
                service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<ReserveAccountResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<ReserveAccountResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // FILTER BY ACCOUNT TYPE
    // -------------------------------
    @GetMapping("/by-account-type/{accountTypeId}")
    public ResponseEntity<?> getByAccountType(
            @PathVariable Long accountTypeId
    ) {

        ApiResponseDTO<List<ReserveAccountResponseDto>> response =
                service.getByAccountTypeId(accountTypeId);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // FILTER BY SCHEME TYPE
    // -------------------------------
    @GetMapping("/by-scheme-type/{schemeTypeId}")
    public ResponseEntity<?> getBySchemeType(
            @PathVariable Long schemeTypeId
    ) {

        ApiResponseDTO<List<ReserveAccountResponseDto>> response =
                service.getBySchemeTypeId(schemeTypeId);

        return ResponseEntity.ok(response);
    }
}