package com.claim.claim_processing.integration.rentalAdjustment.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.repository.adjustmentMaster.RentalAdjustmentMasterRepository;
import com.claim.claim_processing.integration.rentalAdjustment.dto.RentalDetailResponseDto;
import com.claim.claim_processing.integration.rentalAdjustment.service.RentalDetailService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalDetailServiceImpl implements RentalDetailService {

    private final RentalAdjustmentMasterRepository repository;

    @Override
    public ApiResponseDTO<List<RentalDetailResponseDto>> getRentalDetails(String identityId) {

        BigDecimal percentage = repository.findAll().get(0).getPercentage();
        List<RentalDetailResponseDto> response = List.of(

                RentalDetailResponseDto.builder()
                        .rentalType("Residential")
                        .status("Active")
                        .rentalPercentage(percentage)
                        .outstandingAmount(new BigDecimal(30000))
                        .rentalAmount(new BigDecimal(5000.00))
                        .build(),

                RentalDetailResponseDto.builder()
                        .rentalType("Commercial")
                        .status("Completed")
                        .rentalPercentage(percentage)
                        .outstandingAmount(new BigDecimal(50000))
                        .rentalAmount(new BigDecimal(10000.00))
                        .build()
        );
        return ApiResponseDTO.success(response);
    }
}
