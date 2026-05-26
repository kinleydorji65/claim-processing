package com.claim.claim_processing.claim.controller.application;

import com.claim.claim_processing.claim.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.claim.DTO.response.application.ClaimApplicationResponseDto;
import com.claim.claim_processing.claim.service.application.ClaimApplicationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/applications")
@RequiredArgsConstructor
public class ClaimApplicationController {

    private final ClaimApplicationService claimApplicationService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ClaimApplicationResponseDto>> create(
            @RequestBody ClaimApplicationRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(claimApplicationService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ClaimApplicationRequestDto request
    ) {
        return ResponseEntity.ok(claimApplicationService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(claimApplicationService.getById(id));
    }

    @GetMapping("/application-number/{applicationNumber}")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationResponseDto>> getByApplicationNumber(
            @PathVariable String applicationNumber
    ) {
        return ResponseEntity.ok(
                claimApplicationService.getByApplicationNumber(applicationNumber)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ClaimApplicationResponseDto>>> getAll() {
        return ResponseEntity.ok(claimApplicationService.getAll());
    }

    @GetMapping("/member-code/{memberCode}")
    public ResponseEntity<ApiResponseDTO<List<ClaimApplicationResponseDto>>> getByMemberCode(
            @PathVariable String memberCode
    ) {
        return ResponseEntity.ok(claimApplicationService.getByMemberCode(memberCode));
    }

    @GetMapping("/nppf-number/{nppfNumber}")
    public ResponseEntity<ApiResponseDTO<List<ClaimApplicationResponseDto>>> getByNppfNumber(
            @PathVariable String nppfNumber
    ) {
        return ResponseEntity.ok(claimApplicationService.getByNppfNumber(nppfNumber));
    }
}