package com.claim.claim_processing.document.controller;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.document.dto.DocumentTypeResponseDto;
import com.claim.claim_processing.document.service.DocumentService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claim/documents")
@RequiredArgsConstructor
public class DocumentMasterController {

    private final DocumentService documentService;

    @GetMapping("/generate/{claimTypeId}")
    public ResponseEntity<?> generateClaimDocument(
            @PathVariable Long claimTypeId) {

        ApiResponseDTO<List<DocumentTypeResponseDto>> response =
                documentService.generateClaimDocument(claimTypeId);

        return ResponseEntity.ok(response);
    }
}