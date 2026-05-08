package com.claim.claim_processing.common.controller.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedPeriodRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedPeriodRuleResponseDto;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedPeriodRuleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unclaimed-period-rule")
@RequiredArgsConstructor
public class UnclaimedPeriodRuleMasterController {

    private final UnclaimedPeriodRuleMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<UnclaimedPeriodRuleResponseDto>> create(
            @RequestBody UnclaimedPeriodRuleRequestDto dto) {

        UnclaimedPeriodRuleResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedPeriodRuleResponseDto>builder()
                        .success(true)
                        .message("Unclaimed period rule created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedPeriodRuleResponseDto>> update(
            @PathVariable Long id,
            @RequestBody UnclaimedPeriodRuleRequestDto dto) {

        UnclaimedPeriodRuleResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedPeriodRuleResponseDto>builder()
                        .success(true)
                        .message("Unclaimed period rule updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedPeriodRuleResponseDto>> getById(
            @PathVariable Long id) {

        UnclaimedPeriodRuleResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedPeriodRuleResponseDto>builder()
                        .success(true)
                        .message("Unclaimed period rule fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY RULE NAME =================
    @GetMapping("/rule-name/{ruleName}")
    public ResponseEntity<ApiResponse<UnclaimedPeriodRuleResponseDto>> getByRuleName(
            @PathVariable String ruleName) {

        UnclaimedPeriodRuleResponseDto response = service.getByRuleName(ruleName);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedPeriodRuleResponseDto>builder()
                        .success(true)
                        .message("Unclaimed period rule fetched by rule name successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY PERIOD VALUE =================
    @GetMapping("/period-value/{periodValue}")
    public ResponseEntity<ApiResponse<UnclaimedPeriodRuleResponseDto>> getByPeriodValue(
            @PathVariable Integer periodValue) {

        UnclaimedPeriodRuleResponseDto response =
                service.getByPeriodValue(periodValue);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedPeriodRuleResponseDto>builder()
                        .success(true)
                        .message("Unclaimed period rule fetched by period value successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<UnclaimedPeriodRuleResponseDto>>> getAll() {

        List<UnclaimedPeriodRuleResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedPeriodRuleResponseDto>>builder()
                        .success(true)
                        .message("All unclaimed period rules fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UnclaimedPeriodRuleResponseDto>>> getAllActive() {

        List<UnclaimedPeriodRuleResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedPeriodRuleResponseDto>>builder()
                        .success(true)
                        .message("Active unclaimed period rules fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Unclaimed period rule deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}