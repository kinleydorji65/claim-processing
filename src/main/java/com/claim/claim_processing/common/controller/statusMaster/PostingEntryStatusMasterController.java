package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingEntryStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingEntryStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.PostingEntryStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/posting-entry-status")
@RequiredArgsConstructor
public class PostingEntryStatusMasterController {

    private final PostingEntryStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PostingEntryStatusResponseDto>> create(
            @RequestBody PostingEntryStatusRequestDto dto) {

        ApiResponseDTO<PostingEntryStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PostingEntryStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PostingEntryStatusRequestDto dto) {

        ApiResponseDTO<PostingEntryStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PostingEntryStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<PostingEntryStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<PostingEntryStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<PostingEntryStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PostingEntryStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<PostingEntryStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<PostingEntryStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<PostingEntryStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}