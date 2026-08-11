package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationDeductionRequestDto;
import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationDeductionRequestDto.DeductionItemDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionDetail;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionItem;
import com.claim.claim_processing.application.repository.application.ClaimApplicationDeductionDetailRepository;
import com.claim.claim_processing.application.repository.application.ClaimApplicationDeductionItemRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationDeductionDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimApplicationDeductionDetailServiceImpl
        implements ClaimApplicationDeductionDetailService {

    private final ClaimApplicationDeductionDetailRepository deductionDetailRepository;
    private final ClaimApplicationDeductionItemRepository claimApplicationDeductionItemRepository;

    @Override
public ClaimApplicationDeductionDetail saveCalculationDeductions(
        ClaimApplication claimApplication,
        ClaimApplicationDeductionRequestDto request) {

    if (claimApplication == null) {
        throw new RuntimeException("Claim application is required.");
    }

    if (request == null) {
        throw new RuntimeException("Calculation response is required.");
    }

    // if (claimApplication.getClaimType() != null && claimApplication.getClaimType().getId() == 5L) {
    //     // For Legal Recovery, the loan amount is the deduction
    //     legalLoanAdjustment = claimApplication.getCalculationSummary().getFinalPayableAmount();
    //     deductionAmount = legalLoanAdjustment;
        
    //     // ✅ Create deduction items for the loan
    //     if (request.getDeductionItems() == null) {
    //         request.setDeductionItems(new ArrayList<>());
    //     }
        
    //     // Add loan deduction item
    //     DeductionItemDto loanItem = DeductionItemDto.builder()
    //             .deductionCategory("LOAN")
    //             .deductedAmount(legalLoanAdjustment)
    //             .outstandingAmount(legalLoanAdjustment)
    //             .remainingAmount(BigDecimal.ZERO)
    //             .remarks("Legal Recovery Loan Deduction")
    //             .createdBy(request.getCreatedBy())
    //             .build();
    //     request.getDeductionItems().add(loanItem);
        
    //     // Set the deducted amount
    //     request.setDeductedAmount(legalLoanAdjustment);
        
    //     log.info("Legal Recovery - Loan Amount: {}", legalLoanAdjustment);
    // }

    // ✅ FIX: First check if a deduction detail already exists for this claim application
    ClaimApplicationDeductionDetail deductionDetail = null;
    
    // Check if claim application already has a deduction detail
    if (claimApplication.getDeductionDetail() != null) {
        // Use the existing one from the claim application
        deductionDetail = claimApplication.getDeductionDetail();
        log.debug("Found existing deduction detail for claim application: {}", claimApplication.getId());
    } else if (request.getDeductionDetailId() != null) {
        // Try to find by ID if provided
        deductionDetail = deductionDetailRepository.findById(request.getDeductionDetailId()).orElse(null);
        if (deductionDetail != null) {
            log.debug("Found deduction detail by ID: {}", request.getDeductionDetailId());
        }
    }
    
    // If still null, check by claim application ID (safety check)
    if (deductionDetail == null) {
        deductionDetail = deductionDetailRepository
            .findByClaimApplication_Id(claimApplication.getId())
            .orElse(null);
        if (deductionDetail != null) {
            log.debug("Found deduction detail by claim application ID: {}", claimApplication.getId());
        }
    }

    if (deductionDetail == null) {
        // ✅ CREATE NEW only if no existing record found
        log.info("Creating new deduction detail for claim application: {}", claimApplication.getId());
        deductionDetail = ClaimApplicationDeductionDetail.builder()
                .outstandingAmount(request.getOutstandingAmount())
                .verifiedDeductedAmount(request.getVerifiedDeductedAmount())
                .approvedDeductedAmount(request.getApprovedDeductedAmount())
                .deductedAmount((request.getDeductedAmount() != null && 
                    request.getDeductedAmount().compareTo(BigDecimal.ZERO) > 0) ? 
                    request.getDeductedAmount() : BigDecimal.valueOf(0.0))
                .remarks(request.getRemarks())
                .claimApplication(claimApplication)
                .createdBy(request.getCreatedBy())
                .build();
    } else {
        // ✅ UPDATE existing
        log.info("Updating existing deduction detail: {} for claim application: {}", 
            deductionDetail.getId(), claimApplication.getId());
        
        if (request.getOutstandingAmount() != null) {
            deductionDetail.setOutstandingAmount(request.getOutstandingAmount());
        }
        if (request.getVerifiedDeductedAmount() != null) {
            deductionDetail.setVerifiedDeductedAmount(request.getVerifiedDeductedAmount());
        }
        if (request.getApprovedDeductedAmount() != null) {
            deductionDetail.setApprovedDeductedAmount(request.getApprovedDeductedAmount());
        }
        if (request.getDeductedAmount() != null && request.getDeductedAmount().compareTo(BigDecimal.ZERO) > 0) {
            deductionDetail.setDeductedAmount(request.getDeductedAmount());
        } else if (claimApplication.getClaimType() != null && claimApplication.getClaimType().getId() == 5L) {
            deductionDetail.setDeductedAmount(claimApplication.getCalculationSummary().getFinalPayableAmount());
        }
        if (request.getRemarks() != null) {
            deductionDetail.setRemarks(request.getRemarks());
        }
        if (request.getCreatedBy() != null) {
            deductionDetail.setUpdatedBy(request.getCreatedBy());
        }
        // Ensure claim application is set
        deductionDetail.setClaimApplication(claimApplication);
    }
    
    // Save the deduction detail
    deductionDetail = deductionDetailRepository.saveAndFlush(deductionDetail);
    
    // ✅ Update the claim application's reference
    claimApplication.setDeductionDetail(deductionDetail);
    
    // Handle deduction items
    if (request.getDeductionItems() == null && 
        claimApplication.getClaimType() != null && 
        claimApplication.getClaimType().getId() == 5L) {
        // request.setDeductionItems(
        //     List.of(DeductionItemDto.builder()
        //         .deductionCategory("LOAN")
        //         .deductedAmount(legalLoanAdjustment)
        //         .outstandingAmount(BigDecimal.valueOf(0.0))
        //         .remainingAmount(BigDecimal.valueOf(0.0))
        //         .build())
        // );
    }
    
    // Add/update deduction items
    addDeductionItems(deductionDetail, request.getDeductionItems());

    return deductionDetail;
}

    private void addDeductionItems(ClaimApplicationDeductionDetail deductionDetail,
        List<DeductionItemDto> items) {

    if (items == null || items.isEmpty()) {
        // Clear all existing items if no items provided
        if (deductionDetail.getDeductionItems() != null) {
            deductionDetail.getDeductionItems().clear();
        }
        return;
    }

    // IMPORTANT: Clear existing items to handle removals
    // This ensures items removed from the request are deleted from the database
    if (deductionDetail.getDeductionItems() != null) {
        deductionDetail.getDeductionItems().clear();
    } else {
        deductionDetail.setDeductionItems(new ArrayList<>());
    }

    // Add all items from the request
    for (DeductionItemDto item : items) {
        ClaimApplicationDeductionItem entity;
        
        // Check if this is an existing item (has ID) or new (ID is 0 or null)
        if (item.getDeductionItemId() != null && item.getDeductionItemId() > 0) {
            // Try to find existing item
            entity = claimApplicationDeductionItemRepository
                    .findById(item.getDeductionItemId())
                    .orElse(null);
            
            if (entity != null) {
                // Update existing entity
                entity.setDeductionCategory(item.getDeductionCategory());
                entity.setOutstandingAmount(item.getOutstandingAmount());
                entity.setDeductedAmount(item.getDeductedAmount());
                entity.setRemainingAmount(item.getRemainingAmount());
                entity.setRemarks(item.getRemarks());
                entity.setUpdatedBy(item.getUpdatedBy());
                // Ensure it's associated with the current deduction detail
                entity.setDeductionDetail(deductionDetail);
            } else {
                // ID provided but not found - create new
                entity = createDeductionItem(deductionDetail, item);
            }
        } else {
            // New item (ID is null or 0)
            entity = createDeductionItem(deductionDetail, item);
        }
        
        // Add to the collection
        deductionDetail.getDeductionItems().add(entity);
        // Save individually to ensure it's persisted
        claimApplicationDeductionItemRepository.saveAndFlush(entity);
    }
    
    // Save the deduction detail with all items
    deductionDetailRepository.saveAndFlush(deductionDetail);
}

private ClaimApplicationDeductionItem createDeductionItem(
        ClaimApplicationDeductionDetail deductionDetail, 
        DeductionItemDto item) {
    
    return ClaimApplicationDeductionItem.builder()
            .deductionDetail(deductionDetail)
            .deductionCategory(item.getDeductionCategory())
            .outstandingAmount(item.getOutstandingAmount())
            .deductedAmount(item.getDeductedAmount())
            .remainingAmount(item.getRemainingAmount())
            .remarks(item.getRemarks())
            .createdBy(item.getCreatedBy() != null ? item.getCreatedBy() : deductionDetail.getCreatedBy())
            .build();
}

    @Override
    public ClaimApplicationDeductionDetail patchDeductionDetail(
            ClaimApplicationDeductionRequestDto request) {

        ClaimApplicationDeductionDetail deductionDetail = deductionDetailRepository
                .findById(request.getDeductionDetailId())
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

        if (request.getRemarks() != null) {
            deductionDetail.setRemarks(request.getRemarks());
        }

        if (request.getUpdatedBy() != null) {
            deductionDetail.setUpdatedBy(request.getUpdatedBy());
        }

        if (request.getDeductionItems() != null && !request.getDeductionItems().isEmpty()) {
            patchDeductionItems(deductionDetail, request.getDeductionItems(), request.getUpdatedBy());
        }

        return deductionDetailRepository.save(deductionDetail);
    }

    private void patchDeductionItems(
            ClaimApplicationDeductionDetail deductionDetail,
            List<ClaimApplicationDeductionRequestDto.DeductionItemDto> itemRequests,
            String updatedBy) {

        for (ClaimApplicationDeductionRequestDto.DeductionItemDto itemRequest : itemRequests) {

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
}
