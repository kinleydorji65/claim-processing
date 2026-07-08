package com.claim.claim_processing.application.service.application.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;
import com.claim.claim_processing.application.mapper.application.ClaimSpecialCaseApplicationMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.application.ClaimSpecialCaseApplicationRepository;
import com.claim.claim_processing.application.service.application.ClaimSpecialCaseApplicationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.pension.PensionDetail;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.pension.PensionDetailRepository;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseRefundReasonMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimSpecialCaseApplicationServiceImpl implements ClaimSpecialCaseApplicationService {
    
    private final ClaimSpecialCaseApplicationMapper claimSpecialCaseApplicationMapper;
    private final ClaimSpecialCaseApplicationRepository claimSpecialCaseApplicationRepository;
    private final PensionDetailRepository pensionDetailRepository;
    private final ReserveAccountRepository reserveAccountRepository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final SpecialCaseRefundReasonMasterRepository specialCaseRefundReasonMasterRepository;

    /**
     * CREATE - Create a new special case application
     */
    @Override
    @Transactional
    public ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> create(
            ClaimSpecialCaseApplicationRequestDto dto, 
            ClaimApplication claimApplication) {
        
        try {
            log.info("Creating special case application for claim ID: {}", 
                    claimApplication != null ? claimApplication.getId() : "null");
            
            // Validate claim application
            if (claimApplication == null) {
                return ApiResponseDTO.notFound("Claim application is required");
            }

            if (dto.getCaseType() == null) {
                return ApiResponseDTO.notFound("Case type is required");
            }
            
            // Check if special case already exists for this claim
            ClaimSpecialCaseApplication checkClaimApplication = claimSpecialCaseApplicationRepository
                    .findByClaimApplicationId(claimApplication.getId())
                    .orElse(null);
            if (checkClaimApplication != null) {
                return ApiResponseDTO.notFound("Special case application already exists for this claim");
            }
            
            // Fetch pension details
            PensionDetail pensionDetail = null;
            if (dto.getCaseType() != null && dto.getCaseType().toString().equals("CONVERSION_FROM_PENSION_TO_LUMSUM")) {
                pensionDetail = pensionDetailRepository
                        .findByNppfNumber(claimApplication.getNppfNumber())
                        .orElseThrow(() -> ClaimException.notFound("Pension detail not found for NPPF number: " + claimApplication.getNppfNumber()));
            }
            
            // Fetch reserve account for forfeited component case
            ReserveAccount reserveAccount = null;
            if (dto.getCaseType() != null && dto.getCaseType().toString().equals("CLAIM_FORFEITED_COMPONENT")) {
                reserveAccount = reserveAccountRepository
                        .findByNppfNumber(claimApplication.getNppfNumber())
                        .orElseThrow(() -> ClaimException.notFound("Reserve account not found for NPPF number: " + claimApplication.getNppfNumber()));
            }
            
            // Create entity from DTO
            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationMapper.toEntity(dto);
            
            SpecialCaseRefundReasonMaster specialCaseRefundReasonMaster = specialCaseRefundReasonMasterRepository.findById(dto.getCaseReasonId()).orElseThrow(() -> ClaimException.notFound("No Case Detail found with id: " + dto.getCaseReasonId()));
            // Set default values
            entity.setCaseReasonId(dto.getCaseReasonId());
            entity.setIsActive("Y");
            entity.setCreatedBy(getCurrentUser());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setClaimApplication(claimApplication);
            
            // Set pension snapshot
            if (pensionDetail != null) {
                entity.setPensionType(pensionDetail.getPensionType());
                entity.setPensionStartDate(pensionDetail.getPensionStartDate());
                entity.setTotalContributionYears(pensionDetail.getTotalContributionYears());
                entity.setTotalPensionAmount(pensionDetail.getTotalPensionFund());
                entity.setPensionAccount(pensionDetail);
            }
            
            // Set forfeited snapshot if applicable
            if (dto.getCaseType() != null && dto.getCaseType().toString().equals("CLAIM_FORFEITED_COMPONENT") 
                    && reserveAccount != null) {
                entity.setTotalForfeitedAmount(reserveAccount.getForfeitedAmount());
                entity.setEligibleClaimAmount(calculateEligibleClaimAmount(reserveAccount.getForfeitedAmount()));
                entity.setForfeitedDate(reserveAccount.getCreatedAt());
                entity.setComponentCodes(reserveAccount.getComponentCodes());
                entity.setReserveAccount(reserveAccount);
            }
            
            // Save entity
            ClaimSpecialCaseApplication saved = claimSpecialCaseApplicationRepository.saveAndFlush(entity);
            log.info("Special case application created with ID: {}", saved.getId());
            
            // Build response
            ClaimSpecialCaseApplicationResponseDto response = 
                    claimSpecialCaseApplicationMapper.toResponseDto(saved);
            response.setReserveAccountId(entity.getReserveAccount() != null ? entity.getReserveAccount().getId() : null);
            response.setPensionAccountId(entity.getPensionAccount() != null ? entity.getPensionAccount().getId() : null);
            response.setCaseReasonId(specialCaseRefundReasonMaster.getId());
            response.setCaseReasonName(specialCaseRefundReasonMaster.getName());
            return ApiResponseDTO.success(response);
            
        } catch (ClaimException e) {
            log.error("Claim exception: {}", e.getMessage());
            throw ClaimException.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating special case application", e);
            throw ClaimException.internalError("Failed to create special case: " + e.getMessage());
        }
    }

    /**
     * PATCH - Update an existing special case application (partial update)
     */
    @Override
    @Transactional
    public ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> patch(ClaimSpecialCaseApplicationRequestDto dto, ClaimApplication claimApplication) {
        
        try {
            log.info("Patching special case application with ID: {}", dto.getId());
            
            // Find existing entity
            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationRepository
                    .findById(dto.getId())
                    .orElseThrow(() -> ClaimException.notFound("Special case application not found with ID: " + dto.getId()));
            
            // Update only allowed fields (caseReason, currentBenefitType, requestedBenefitType, 
            // requestedAmount, reserveAccountId)
            if (dto.getCaseReasonId() != null) {
                entity.setCaseReasonId(dto.getCaseReasonId());
            }
            if (dto.getCurrentBenefitType() != null) {
                entity.setCurrentBenefitType(dto.getCurrentBenefitType());
            }
            if (dto.getRequestedBenefitType() != null) {
                entity.setRequestedBenefitType(dto.getRequestedBenefitType());
            }
            if (dto.getRequestedAmount() != null) {
                entity.setRequestedAmount(dto.getRequestedAmount());
            }
            if (dto.getReserveAccountId() != null) {
                ReserveAccount reserveAccount = reserveAccountRepository.findById(dto.getReserveAccountId())
                        .orElseThrow(() -> ClaimException.notFound("Reserve account not found with ID: " + dto.getReserveAccountId()));
                entity.setReserveAccount(reserveAccount);
            }
            
            // Update audit fields
            entity.setUpdatedBy(getCurrentUser());
            entity.setClaimApplication(claimApplication);
            entity.setUpdatedAt(LocalDateTime.now());
            
            // Save updated entity
            ClaimSpecialCaseApplication saved = claimSpecialCaseApplicationRepository.save(entity);
            log.info("Special case application patched with ID: {}", saved.getId());
            
            // Build response
            ClaimSpecialCaseApplicationResponseDto response = 
                    claimSpecialCaseApplicationMapper.toResponseDto(saved);
            
            return ApiResponseDTO.success(response);
            
        } catch (ClaimException e) {
            log.error("Resource not found: {}", e.getMessage());
            throw ClaimException.internalError(e.getMessage());
        } catch (Exception e) {
            log.error("Error patching special case application", e);
            throw ClaimException.internalError("Failed to patch special case: " + e.getMessage());
        }
    }

    /**
     * GET BY APPLICATION NUMBER - Get special case application by application number
     */
    @Override
    public ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> getByApplicationNumber(String applicationNumber) {
        try {
            log.info("Fetching special case application by application number: {}", applicationNumber);
            
            // First find the claim application by application number
            ClaimApplication claimApplication = claimApplicationRepository
                    .findByApplicationNumber(applicationNumber)
                    .orElse(null);
            
            // Then find the special case application by claim application ID
            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationRepository
                    .findByClaimApplicationId(claimApplication.getId())
                    .orElseThrow(null);
                if (entity == null) {
                return null;
            }
            ClaimSpecialCaseApplicationResponseDto response = 
                    claimSpecialCaseApplicationMapper.toResponseDto(entity);
            
            return ApiResponseDTO.success(response);
            
        } catch (ClaimException e) {
            log.error("Resource not found: {}", e.getMessage());
            throw ClaimException.internalError(e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching special case application", e);
            throw ClaimException.internalError("Failed to fetch special case: " + e.getMessage());
        }
    }

    /**
     * Calculate eligible claim amount (80% of total forfeited)
     */
    private BigDecimal calculateEligibleClaimAmount(BigDecimal totalForfeited) {
        if (totalForfeited == null || totalForfeited.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return totalForfeited.multiply(BigDecimal.valueOf(0.8))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Get current user from security context
     */
    private String getCurrentUser() {
        // TODO: Implement actual user retrieval from security context
        return "SYSTEM";
    }
}