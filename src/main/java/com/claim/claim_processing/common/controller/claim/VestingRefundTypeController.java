package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.VestingRefundTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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
    public ResponseEntity<?> create(
            @RequestBody VestingRefundTypeRequestDto requestDto
    ) {

        ApiResponseDTO<VestingRefundTypeResponseDto> response = service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody VestingRefundTypeRequestDto requestDto
    ) {

        ApiResponseDTO<VestingRefundTypeResponseDto> response = service.update(id, requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<VestingRefundTypeResponseDto>> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<VestingRefundTypeResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<VestingRefundTypeResponseDto>>> getAll() {

        ApiResponseDTO<List<VestingRefundTypeResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}