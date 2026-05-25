package com.claim.claim_processing.document.service;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.document.dto.DocumentTypeResponseDto;

public interface DocumentService {
    ApiResponseDTO<List<DocumentTypeResponseDto>> generateClaimDocument(Long claimId);
}
