package com.claim.claim_processing.application.service.application;

import java.math.BigDecimal;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationSummaryRequest;
import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationOtherRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;

public interface ClaimApplicationCalculationService {
    ClaimApplicationCalculationSummary initialCreate(ClaimApplication claimApplication, ClaimApplicationOtherRequestDto otherRequest);
    ClaimApplicationCalculationSummary createForCalculation(ClaimApplication claimApplication, ClaimApplicationCalculationSummaryRequest request);
    ClaimApplicationCalculationSummary patch(long calculationId, ClaimApplicationCalculationSummaryRequest request);
}
