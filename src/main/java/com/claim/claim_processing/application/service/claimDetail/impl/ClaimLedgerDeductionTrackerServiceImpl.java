package com.claim.claim_processing.application.service.claimDetail.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.claimDetail.ClaimLedgerDeductionTrackerRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerDeductionTracker;
import com.claim.claim_processing.application.mapper.claimDetail.ClaimLedgerDeductionTrackerMapper;
import com.claim.claim_processing.application.repository.claimDetail.ClaimLedgerDeductionTrackerRepository;
import com.claim.claim_processing.application.service.claimDetail.ClaimLedgerDeductionTrackerService;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimLedgerDeductionTrackerServiceImpl implements ClaimLedgerDeductionTrackerService {
    private final ClaimLedgerDeductionTrackerMapper claimLedgerDeductionTrackerMapper;
    private final ClaimLedgerDeductionTrackerRepository claimLedgerDeductionTrackerRepository;
    private final BenefitCalculationService benefitCalculationService;


    @Override
    public ClaimLedgerDeductionTracker create(ClaimDetail claimDetail, GeneralClaimResponse generalClaimResponse, String createdBy) {

        ClaimCalculationResponseDTO calculationResponse = null;

        LocalDate cessationDate = null;
        Long cessationTypeId = 0L;
        Long reasonTypeId = 0l;

        if (claimDetail.getNormalClaimDetail() != null) {
            cessationDate = claimDetail.getNormalClaimDetail().getDateOfTermination() != null ? claimDetail.getNormalClaimDetail().getDateOfTermination() : (claimDetail.getNormalClaimDetail().getRelievingOrderDate() != null ? claimDetail.getNormalClaimDetail().getRelievingOrderDate() : claimDetail.getNormalClaimDetail().getCessationEffectiveDate());
            cessationTypeId = claimDetail.getNormalClaimDetail().getCessationType() != null ? claimDetail.getNormalClaimDetail().getCessationType().getId() : 0;
        } 
        if(claimDetail.getPartialWithdrawalDetail() != null) {
            reasonTypeId = claimDetail.getPartialWithdrawalDetail().getWithdrawalReason().getId() != null ? claimDetail.getPartialWithdrawalDetail().getWithdrawalReason().getId() : 0;
        }
        ClaimInitialPreviewRequest benefitRequest = ClaimInitialPreviewRequest.builder()

                                        .cessationDate(cessationDate)
                                        .cessationTypeId(cessationTypeId)
                                        .claimTypeId(claimDetail.getClaimType().getId())
                                        .nppfNumber(claimDetail.getNppfNumber())
                                        .isSpecialCase(claimDetail.getIsSpecialCase() == ActivityEnum.Y)
                                        .reasonTypeId(reasonTypeId)
                                        .build();

        calculationResponse = benefitCalculationService
                                .calculateBenefit(benefitRequest)
                                .getData();
        
        BigDecimal totalPfAmount = BigDecimal.ZERO;
        BigDecimal totalPAmount = BigDecimal.ZERO;
        if(calculationResponse != null && calculationResponse.getComponents() != null) {
            totalPfAmount = calculateTotalPfAmount(calculationResponse.getComponents());
            totalPAmount = calculateTotalPensionAmount(calculationResponse.getComponents());
        }
        
        BigDecimal totalDeductedAmount = BigDecimal.ZERO;
        if (claimDetail.getDeductionDetail() != null && claimDetail.getDeductionDetail().getDeductionItems() != null) {
            totalDeductedAmount = claimDetail.getDeductionDetail().getDeductionItems().stream()
                    .filter(Objects::nonNull)
                    .map(d -> d.getDeductedAmount() != null ? d.getDeductedAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        ClaimLedgerDeductionTrackerRequestDto ledgerDeductionTrackerRequestDto = ClaimLedgerDeductionTrackerRequestDto.builder()
                .nppfNumber(claimDetail.getNppfNumber())
                .totalPfAmount(totalPfAmount)
                .totalPcAmount(totalPAmount)
                .totalDeductionAmount(totalDeductedAmount)
                .balanceAmount(totalPfAmount.add(totalPAmount).subtract(totalDeductedAmount))
                .isActive(ActivityEnum.Y)
                .build();

            ClaimLedgerDeductionTracker savedEntity = claimLedgerDeductionTrackerMapper.toEntity(ledgerDeductionTrackerRequestDto);
            savedEntity.setClaimDetail(claimDetail);
            savedEntity.setClaimType(claimDetail.getClaimType());
            savedEntity.setCreatedBy(createdBy);
            claimLedgerDeductionTrackerRepository.saveAndFlush(savedEntity);
        return savedEntity;
    }

    private BigDecimal calculateTotalPfAmount(List<ComponentBalanceDTO> components) {
    if (components == null || components.isEmpty()) {
        return BigDecimal.ZERO;
    }
    
    return components.stream()
            .filter(Objects::nonNull)
            .filter(component -> component.getCode() != null && component.getCode().startsWith("PF_"))
            .map(ComponentBalanceDTO::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

private BigDecimal calculateTotalPensionAmount(List<ComponentBalanceDTO> components) {
    if (components == null || components.isEmpty()) {
        return BigDecimal.ZERO;
    }
    
    return components.stream()
            .filter(Objects::nonNull)
            .filter(component -> component.getCode() != null && component.getCode().startsWith("P_"))
            .map(ComponentBalanceDTO::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}
}
