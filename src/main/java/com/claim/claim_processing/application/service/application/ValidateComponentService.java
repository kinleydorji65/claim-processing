package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ValidateComponentService {
    ApiResponseDTO<String> validateComponent(String nppfNumber, String componentCode);
    ApiResponseDTO<String> updateComponents(String nppfNumber, List<SpecialCaseComponentBalanceResponseDTO> components);
}
