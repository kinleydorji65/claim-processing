package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationDeductionPatchRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionDetail;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionItem;
import com.claim.claim_processing.application.repository.application.ClaimApplicationDeductionDetailRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationDeductionDetailService;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.LoanAdjustmentDetailDto;
import com.claim.claim_processing.rule.claim.DTO.response.RentalAdjustmentDetailDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClaimApplicationDeductionDetailServiceImpl
        implements ClaimApplicationDeductionDetailService {

    private final ClaimApplicationDeductionDetailRepository deductionDetailRepository;

    @Override
    public ClaimApplicationDeductionDetail saveCalculationDeductions(
            ClaimApplication claimApplication,
            ClaimCalculationResponseDTO calculationResponse,
            String createdBy) {

        if (claimApplication == null) {
            throw new RuntimeException("Claim application is required.");
        }

        if (calculationResponse == null) {
            throw new RuntimeException("Calculation response is required.");
        }

        BigDecimal totalDeductedAmount = calculateTotalDeductedAmount(calculationResponse);

        if (totalDeductedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        ClaimApplicationDeductionDetail deductionDetail = ClaimApplicationDeductionDetail.builder()
                .claimApplication(claimApplication)
                .outstandingAmount(totalDeductedAmount)
                .systemDeductedAmount(totalDeductedAmount)
                .deductedAmount(totalDeductedAmount)
                .isAutoApplied(ActivityEnum.Y)
                .isManualOverride(ActivityEnum.N)
                .remarks("Generated from benefit calculation.")
                .createdBy(createdBy)
                .isActive(ActivityEnum.Y)
                .build();

        addLoanDeductionItems(deductionDetail, calculationResponse, createdBy);
        addRentalDeductionItems(deductionDetail, calculationResponse, createdBy);

        return deductionDetailRepository.save(deductionDetail);
    }

    private void addLoanDeductionItems(
            ClaimApplicationDeductionDetail deductionDetail,
            ClaimCalculationResponseDTO calculationResponse,
            String createdBy) {

        if (calculationResponse.getLoanAdjustmentResult() == null ||
                calculationResponse.getLoanAdjustmentResult().getDeductions() == null) {
            return;
        }

        for (LoanAdjustmentDetailDto loan : calculationResponse.getLoanAdjustmentResult().getDeductions()) {

            ClaimApplicationDeductionItem item = ClaimApplicationDeductionItem.builder()
                    .deductionDetail(deductionDetail)
                    .deductionCategory("LOAN")
                    .referenceNumber(
                            loan.getLoanTypeId() != null
                                    ? String.valueOf(loan.getLoanTypeId())
                                    : null)
                    .referenceName(loan.getLoanTypeName())
                    .outstandingAmount(defaultAmount(loan.getOutstandingAmount()))
                    .deductedAmount(defaultAmount(loan.getAdjustedAmount()))
                    .remainingAmount(defaultAmount(loan.getRemainingOutstandingAmount()))
                    .remarks(loan.getStatus())
                    .createdBy(createdBy)
                    .isActive(ActivityEnum.Y)
                    .build();

            deductionDetail.getDeductionItems().add(item);
        }
    }

    private void addRentalDeductionItems(
            ClaimApplicationDeductionDetail deductionDetail,
            ClaimCalculationResponseDTO calculationResponse,
            String createdBy) {

        if (calculationResponse.getRentalAdjustmentResult() == null ||
                calculationResponse.getRentalAdjustmentResult().getDeductions() == null) {
            return;
        }

        for (RentalAdjustmentDetailDto rental : calculationResponse.getRentalAdjustmentResult().getDeductions()) {

            ClaimApplicationDeductionItem item = ClaimApplicationDeductionItem.builder()
                    .deductionDetail(deductionDetail)
                    .deductionCategory("RENTAL")
                    .referenceNumber(
                            rental.getRentalId() != null
                                    ? String.valueOf(rental.getRentalId())
                                    : null)
                    .referenceName(rental.getRentalName())
                    .outstandingAmount(defaultAmount(rental.getOutstandingAmount()))
                    .deductedAmount(defaultAmount(rental.getAdjustedAmount()))
                    .createdBy(createdBy)
                    .isActive(ActivityEnum.Y)
                    .build();

            deductionDetail.getDeductionItems().add(item);
        }
    }

    private BigDecimal calculateTotalDeductedAmount(
            ClaimCalculationResponseDTO calculationResponse) {

        BigDecimal total = BigDecimal.ZERO;

        if (calculationResponse.getLoanAdjustmentResult() != null &&
                calculationResponse.getLoanAdjustmentResult().getTotalAdjustedAmount() != null) {
            total = total.add(calculationResponse.getLoanAdjustmentResult().getTotalAdjustedAmount());
        }

        if (calculationResponse.getRentalAdjustmentResult() != null &&
                calculationResponse.getRentalAdjustmentResult().getTotalAdjustedAmount() != null) {
            total = total.add(calculationResponse.getRentalAdjustmentResult().getTotalAdjustedAmount());
        }

        return total;
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public ClaimApplicationDeductionDetail patchDeductionDetail(
            ClaimApplicationDeductionPatchRequestDto request) {

        ClaimApplicationDeductionDetail deductionDetail = deductionDetailRepository.findById(request.getDeductionDetailId())
                .orElseThrow(() -> new RuntimeException(
                        "Deduction detail not found with id: " + request.getDeductionDetailId()));

        if (request.getVerifiedDeductedAmount() != null) {
            deductionDetail.setVerifiedDeductedAmount(request.getVerifiedDeductedAmount());
        }

        if (request.getApprovedDeductedAmount() != null) {
            deductionDetail.setApprovedDeductedAmount(request.getApprovedDeductedAmount());
        }

        if (request.getDeductedAmount() != null) {
            deductionDetail.setDeductedAmount(request.getDeductedAmount());
        }

        if (request.getIsManualOverride() != null) {
            deductionDetail.setIsManualOverride(request.getIsManualOverride());
        }

        if (request.getOverrideReason() != null) {
            deductionDetail.setOverrideReason(request.getOverrideReason());
        }

        if (request.getRemarks() != null) {
            deductionDetail.setRemarks(request.getRemarks());
        }

        if (request.getUpdatedBy() != null) {
            deductionDetail.setUpdatedBy(request.getUpdatedBy());
        }

        if (request.getDeductionItems() != null && !request.getDeductionItems().isEmpty()) {
            patchDeductionItems(deductionDetail, request.getDeductionItems(), request.getUpdatedBy());
        }

        recalculateDeductionTotal(deductionDetail);

        return deductionDetailRepository.save(deductionDetail);
    }

    private void patchDeductionItems(
            ClaimApplicationDeductionDetail deductionDetail,
            List<ClaimApplicationDeductionPatchRequestDto.DeductionItemPatchDto> itemRequests,
            String updatedBy) {

        for (ClaimApplicationDeductionPatchRequestDto.DeductionItemPatchDto itemRequest : itemRequests) {

            if (itemRequest.getDeductionItemId() == null) {
                continue;
            }

            ClaimApplicationDeductionItem item = deductionDetail.getDeductionItems()
                    .stream()
                    .filter(existingItem -> existingItem.getId().equals(itemRequest.getDeductionItemId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Deduction item not found with id: " + itemRequest.getDeductionItemId()));

            if (itemRequest.getDeductedAmount() != null) {
                item.setDeductedAmount(itemRequest.getDeductedAmount());
            }

            if (itemRequest.getRemainingAmount() != null) {
                item.setRemainingAmount(itemRequest.getRemainingAmount());
            }

            if (itemRequest.getRemarks() != null) {
                item.setRemarks(itemRequest.getRemarks());
            }

            if (updatedBy != null) {
                item.setUpdatedBy(updatedBy);
            }
        }
    }

    private void recalculateDeductionTotal(
            ClaimApplicationDeductionDetail deductionDetail) {

        BigDecimal totalDeducted = deductionDetail.getDeductionItems()
                .stream()
                .filter(item -> item.getIsActive() == ActivityEnum.Y)
                .map(ClaimApplicationDeductionItem::getDeductedAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        deductionDetail.setDeductedAmount(totalDeducted);
    }
}
