package com.claim.claim_processing.integration.rentalAdjustment.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.rentalAdjustment.dto.RentalDetailResponseDto;
import com.claim.claim_processing.integration.rentalAdjustment.service.RentalDetailService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalDetailServiceImpl implements RentalDetailService {

    @Override
    public ApiResponseDTO<List<RentalDetailResponseDto>> getRentalDetails(String identityId) {
        List<RentalDetailResponseDto> response = List.of(

                RentalDetailResponseDto.builder()
                        .rentalType("Residential")
                        .outstandingAmount(new BigDecimal(30))
                        .isRetained("N")
                        .build()
        );
        return ApiResponseDTO.success(response);
    }
}
