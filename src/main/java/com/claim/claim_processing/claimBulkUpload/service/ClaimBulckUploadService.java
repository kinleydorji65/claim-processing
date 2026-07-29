package com.claim.claim_processing.claimBulkUpload.service;

import java.util.List;

import com.claim.claim_processing.claimBulkUpload.dto.ClaimBulkUploadRequestDTO;
import com.claim.claim_processing.claimBulkUpload.dto.ClaimBulkUploadResponseDTO;

public interface ClaimBulckUploadService {
    ClaimBulkUploadResponseDTO uploadClaims(List<ClaimBulkUploadRequestDTO> requests);
}
