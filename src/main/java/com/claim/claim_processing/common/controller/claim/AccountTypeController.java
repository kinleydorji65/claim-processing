package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.AccountTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.AccountTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.AccountTypeUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.AccountTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/account-types")
@RequiredArgsConstructor
public class AccountTypeController {

    private final AccountTypeService accountTypeService;

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<AccountTypeResponseDto>> response =
                accountTypeService.getAllActive();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<AccountTypeResponseDto> response =
                accountTypeService.getById(id);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody AccountTypeCreateRequestDto requestDto) {

        ApiResponseDTO<AccountTypeResponseDto> response =
                accountTypeService.create(requestDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
// GET BY CODE
// -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<AccountTypeResponseDto> response =
                accountTypeService.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody AccountTypeUpdateRequestDto requestDto) {

        ApiResponseDTO<AccountTypeResponseDto> response =
                accountTypeService.update(id, requestDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DEACTIVATE
    // -----------------------------
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {

        ApiResponseDTO<String> response =
                accountTypeService.deactivate(id);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
// DELETE
// -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        ApiResponseDTO<String> response =
                accountTypeService.delete(id);

        return ResponseEntity.ok(response);
    }
}