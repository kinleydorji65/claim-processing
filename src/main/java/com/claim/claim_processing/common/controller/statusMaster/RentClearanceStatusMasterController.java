package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RentClearanceStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.RentClearanceStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.RentClearanceStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/rent-clearance-status")
@RequiredArgsConstructor
public class RentClearanceStatusMasterController {

    private final RentClearanceStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RentClearanceStatusResponseDto>> create(
            @RequestBody RentClearanceStatusRequestDto dto) {

        ApiResponseDTO<RentClearanceStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RentClearanceStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RentClearanceStatusRequestDto dto) {

        ApiResponseDTO<RentClearanceStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RentClearanceStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<RentClearanceStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<RentClearanceStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<RentClearanceStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RentClearanceStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<RentClearanceStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<RentClearanceStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<RentClearanceStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}