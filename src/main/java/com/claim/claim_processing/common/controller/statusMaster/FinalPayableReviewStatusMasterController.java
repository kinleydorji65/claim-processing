package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.FinalPayableReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.FinalPayableReviewStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.FinalPayableReviewStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/final-payable-review-status")
@RequiredArgsConstructor
public class FinalPayableReviewStatusMasterController {

    private final FinalPayableReviewStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<FinalPayableReviewStatusResponseDto>> create(
            @RequestBody FinalPayableReviewStatusRequestDto dto
    ) {

        ApiResponseDTO<FinalPayableReviewStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<FinalPayableReviewStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody FinalPayableReviewStatusRequestDto dto
    ) {

        ApiResponseDTO<FinalPayableReviewStatusResponseDto> response =
                service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<FinalPayableReviewStatusResponseDto>> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<FinalPayableReviewStatusResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<FinalPayableReviewStatusResponseDto>> getByCode(
            @PathVariable String code
    ) {

        ApiResponseDTO<FinalPayableReviewStatusResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<FinalPayableReviewStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<FinalPayableReviewStatusResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<FinalPayableReviewStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<FinalPayableReviewStatusResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(
            @PathVariable Long id
    ) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}