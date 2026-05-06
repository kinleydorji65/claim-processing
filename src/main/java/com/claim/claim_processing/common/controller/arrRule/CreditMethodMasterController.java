package com.claim.claim_processing.common.controller.arrRule;

import com.claim.claim_processing.common.DTO.request.arrRule.CreditMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.arrRule.CreditMethodResponseDto;
import com.claim.claim_processing.common.service.arrRule.CreditMethodMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/credit-methods")
@RequiredArgsConstructor
public class CreditMethodMasterController {

    private final CreditMethodMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<CreditMethodResponseDto>> create(
            @RequestBody CreditMethodRequestDto request
    ) {
        CreditMethodResponseDto response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CreditMethodResponseDto>builder()
                        .success(true)
                        .message("Credit method created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditMethodResponseDto>> update(
            @PathVariable Long id,
            @RequestBody CreditMethodRequestDto request
    ) {
        CreditMethodResponseDto response = service.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<CreditMethodResponseDto>builder()
                        .success(true)
                        .message("Credit method updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditMethodResponseDto>> getById(
            @PathVariable Long id
    ) {
        CreditMethodResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<CreditMethodResponseDto>builder()
                        .success(true)
                        .message("Credit method fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CreditMethodResponseDto>> getByCode(
            @PathVariable String code
    ) {
        CreditMethodResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<CreditMethodResponseDto>builder()
                        .success(true)
                        .message("Credit method fetched successfully by code")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CreditMethodResponseDto>>> getAllActive() {

        List<CreditMethodResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<CreditMethodResponseDto>>builder()
                        .success(true)
                        .message("Active credit methods fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE (SOFT DELETE) =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Credit method deleted successfully")
                        .data(null)
                        .build()
        );
    }
}