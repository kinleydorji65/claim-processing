package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeRuleMapRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeRuleMapResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimTypeRuleMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/claim-type-rule-map")
@RequiredArgsConstructor
public class ClaimTypeRuleMapController {

    private final ClaimTypeRuleMapService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ClaimTypeRuleMapResponseDto>> create(
            @Valid @RequestBody ClaimTypeRuleMapRequestDto requestDto
    ) {

        ClaimTypeRuleMapResponseDto response = service.create(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ClaimTypeRuleMapResponseDto>builder()
                        .success(true)
                        .message("Claim Type Rule Map created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClaimTypeRuleMapResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ClaimTypeRuleMapRequestDto requestDto
    ) {

        ClaimTypeRuleMapResponseDto response = service.update(id, requestDto);

        return ResponseEntity.ok(
                ApiResponse.<ClaimTypeRuleMapResponseDto>builder()
                        .success(true)
                        .message("Claim Type Rule Map updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClaimTypeRuleMapResponseDto>> getById(
            @PathVariable Long id
    ) {

        ClaimTypeRuleMapResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ClaimTypeRuleMapResponseDto>builder()
                        .success(true)
                        .message("Claim Type Rule Map fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClaimTypeRuleMapResponseDto>>> getAll() {

        List<ClaimTypeRuleMapResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ClaimTypeRuleMapResponseDto>>builder()
                        .success(true)
                        .message("Claim Type Rule Map list fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/claim-type/{claimTypeId}")
    public ResponseEntity<ApiResponse<List<ClaimTypeRuleMapResponseDto>>> getByClaimTypeId(
            @PathVariable Long claimTypeId
    ) {

        List<ClaimTypeRuleMapResponseDto> response =
                service.getByClaimTypeId(claimTypeId);

        return ResponseEntity.ok(
                ApiResponse.<List<ClaimTypeRuleMapResponseDto>>builder()
                        .success(true)
                        .message("Mappings fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/rule-type/{ruleTypeId}")
    public ResponseEntity<ApiResponse<List<ClaimTypeRuleMapResponseDto>>> getByRuleTypeId(
            @PathVariable Long ruleTypeId
    ) {

        List<ClaimTypeRuleMapResponseDto> response =
                service.getByRuleTypeId(ruleTypeId);

        return ResponseEntity.ok(
                ApiResponse.<List<ClaimTypeRuleMapResponseDto>>builder()
                        .success(true)
                        .message("Mappings fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Claim Type Rule Map deleted successfully")
                        .data(null)
                        .build()
        );
    }
}