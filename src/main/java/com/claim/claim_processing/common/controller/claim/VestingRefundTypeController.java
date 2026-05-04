package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.VestingRefundTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;
import com.claim.claim_processing.common.service.claim.VestingRefundTypeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/vesting-refund-type")
@RequiredArgsConstructor
public class VestingRefundTypeController {

    private final VestingRefundTypeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<VestingRefundTypeResponseDto>> create(
            @RequestBody VestingRefundTypeRequestDto requestDto
    ) {

        VestingRefundTypeResponseDto response = service.create(requestDto);

        return ResponseEntity.ok(
                ApiResponse.<VestingRefundTypeResponseDto>builder()
                        .success(true)
                        .message("Vesting Refund Type created successfully")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<VestingRefundTypeResponseDto>> update(
            @PathVariable Long id,
            @RequestBody VestingRefundTypeRequestDto requestDto
    ) {

        VestingRefundTypeResponseDto response = service.update(id, requestDto);

        return ResponseEntity.ok(
                ApiResponse.<VestingRefundTypeResponseDto>builder()
                        .success(true)
                        .message("Vesting Refund Type updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VestingRefundTypeResponseDto>> getById(
            @PathVariable Long id
    ) {

        VestingRefundTypeResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<VestingRefundTypeResponseDto>builder()
                        .success(true)
                        .message("Vesting Refund Type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VestingRefundTypeResponseDto>>> getAll() {

        List<VestingRefundTypeResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<VestingRefundTypeResponseDto>>builder()
                        .success(true)
                        .message("Vesting Refund Types fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Vesting Refund Type deleted successfully")
                        .data(null)
                        .build()
        );
    }
}