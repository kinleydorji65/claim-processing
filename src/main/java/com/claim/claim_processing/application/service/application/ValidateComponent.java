package com.claim.claim_processing.application.service.application;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ValidateComponent {
    ApiResponseDTO<String> validateComponent(String nppfNumber, String componentCode);
}
