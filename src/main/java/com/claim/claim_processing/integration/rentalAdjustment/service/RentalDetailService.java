package com.claim.claim_processing.integration.rentalAdjustment.service;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.rentalAdjustment.dto.RentalDetailResponseDto;

public interface RentalDetailService {

    ApiResponseDTO<List<RentalDetailResponseDto>> getRentalDetails(String identityId);
}
