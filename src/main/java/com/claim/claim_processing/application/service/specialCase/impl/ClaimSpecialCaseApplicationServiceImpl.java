package com.claim.claim_processing.application.service.specialCase.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto.SpecialCaseComponentBalanceDTO;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto.SpecialCaseComponentBalanceResponseDTO;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseComponent;
import com.claim.claim_processing.application.mapper.application.ClaimSpecialCaseApplicationMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.application.ClaimSpecialCaseApplicationRepository;
import com.claim.claim_processing.application.repository.application.ClaimSpecialCaseComponentRepository;
import com.claim.claim_processing.application.service.specialCase.ClaimSpecialCaseApplicationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.pension.PensionDetail;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.repository.pension.PensionDetailRepository;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseRefundReasonMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO.ComponentBalanceDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimSpecialCaseApplicationServiceImpl implements ClaimSpecialCaseApplicationService {

    private final ClaimSpecialCaseApplicationMapper claimSpecialCaseApplicationMapper;
    private final ClaimSpecialCaseApplicationRepository claimSpecialCaseApplicationRepository;
    private final ClaimSpecialCaseComponentRepository claimSpecialCaseComponentRepository;
    private final ComponentMasterRepository componentMasterRepository;
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
                        .orElseThrow(() -> ClaimException.notFound(
                                "Pension detail not found for NPPF number: " + claimApplication.getNppfNumber()));
            }

            // Fetch reserve account for forfeited component case
            ReserveAccount reserveAccount = null;
            if (dto.getCaseType() != null && dto.getCaseType().toString().equals("CLAIM_FORFEITED_COMPONENT")) {
                reserveAccount = reserveAccountRepository
                        .findByNppfNumber(claimApplication.getNppfNumber())
                        .orElseThrow(() -> ClaimException.notFound(
                                "Reserve account not found for NPPF number: " + claimApplication.getNppfNumber()));
            }

            // Create entity from DTO
            SpecialCaseRefundReasonMaster specialCaseRefundReasonMaster = null;
            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationMapper.toEntity(dto);
            if (dto.getCaseType() != null && dto.getCaseType().toString().equals("SPECIAL_NORMAL_CLAIM")) {
                specialCaseRefundReasonMaster = specialCaseRefundReasonMasterRepository
                        .findById(dto.getCaseReasonId())
                        .orElseThrow(() -> ClaimException
                                .notFound("No Case Detail found with id: " + dto.getCaseReasonId()));
                entity.setEligibleClaimAmount(calculateTotalComponentAmount(dto.getComponentBalances()));
            }
            // Set default values
            entity.setCaseReasonId(dto.getCaseReasonId());
            entity.setIsActive("Y");
            entity.setCreatedBy(dto.getCreatedBy());
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

            // Set forfeited snapshot if applicable
            if (dto.getCaseType() != null && dto.getCaseType().toString().equals("SPECIAL_NORMAL_CLAIM")
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
            List<SpecialCaseComponentBalanceResponseDTO> specialClaimComponents = storeSpecialCaseComponents(
                    saved,
                    dto.getComponentBalances(),
                    claimApplication.getCreatedBy());
            // Build response
            ClaimSpecialCaseApplicationResponseDto response = claimSpecialCaseApplicationMapper.toResponseDto(saved);
            response.setReserveAccountId(
                    entity.getReserveAccount() != null ? entity.getReserveAccount().getId() : null);
            response.setPensionAccountId(
                    entity.getPensionAccount() != null ? entity.getPensionAccount().getId() : null);
            response.setCaseReasonId(specialCaseRefundReasonMaster != null ?specialCaseRefundReasonMaster.getId() : null);
            response.setCaseReasonName(specialCaseRefundReasonMaster != null ?specialCaseRefundReasonMaster.getName() : null);
            response.setComponents(specialClaimComponents);
            return ApiResponseDTO.success(response);

        } catch (ClaimException e) {
            log.error("Claim exception: {}", e.getMessage());
            throw ClaimException.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating special case application", e);
            throw ClaimException.internalError("Failed to create special case: " + e.getMessage());
        }
    }

    private BigDecimal calculateTotalComponentAmount(List<SpecialCaseComponentBalanceDTO> components) {
    if (components == null || components.isEmpty()) {
        log.warn("No components found, returning ZERO");
        return BigDecimal.ZERO;
    }
    
    BigDecimal total = components.stream()
            .filter(Objects::nonNull)
            .map(SpecialCaseComponentBalanceDTO::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    log.info("Total component amount calculated: {}", total);
    return total;
}
    /**
     * PATCH - Update an existing special case application (partial update)
     */
    @Override
    @Transactional
    public ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> patch(
            ClaimSpecialCaseApplicationRequestDto dto,
            ClaimApplication claimApplication) {

        try {
            log.info("Patching special case application with ID: {}", dto.getId());

            // Find existing entity
            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationRepository
                    .findById(dto.getId())
                    .orElseThrow(() -> ClaimException
                            .notFound("Special case application not found with ID: " + dto.getId()));

            // Update only allowed fields
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
                        .orElseThrow(() -> ClaimException
                                .notFound("Reserve account not found with ID: " + dto.getReserveAccountId()));
                entity.setReserveAccount(reserveAccount);
            }

            // Update audit fields
            entity.setUpdatedBy(dto.getUpdatedBy());
            entity.setClaimApplication(claimApplication);
            entity.setUpdatedAt(LocalDateTime.now());

            // Save updated entity
            ClaimSpecialCaseApplication saved = claimSpecialCaseApplicationRepository.save(entity);
            log.info("Special case application patched with ID: {}", saved.getId());

            // Build response
            ClaimSpecialCaseApplicationResponseDto response = claimSpecialCaseApplicationMapper.toResponseDto(saved);

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
     * GET BY APPLICATION NUMBER - Get special case application by application
     * number
     */
    @Override
    public ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> getByApplicationNumber(String applicationNumber) {
        try {
            log.info("Fetching special case application by application number: {}", applicationNumber);

            // First find the claim application by application number
            ClaimApplication claimApplication = claimApplicationRepository
                    .findByApplicationNumber(applicationNumber)
                    .orElse(null);

            if (claimApplication == null) {
                return ApiResponseDTO.notFound("Claim application not found with number: " + applicationNumber);
            }

            // Then find the special case application by claim application ID
            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationRepository
                    .findByClaimApplicationId(claimApplication.getId())
                    .orElse(null);

            if (entity == null) {
                return ApiResponseDTO.notFound("Special case application not found for claim: " + applicationNumber);
            }

            ClaimSpecialCaseApplicationResponseDto response = claimSpecialCaseApplicationMapper.toResponseDto(entity);

            return ApiResponseDTO.success(response);

        } catch (ClaimException e) {
            log.error("Resource not found: {}", e.getMessage());
            throw ClaimException.internalError(e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching special case application", e);
            throw ClaimException.internalError("Failed to fetch special case: " + e.getMessage());
        }
    }

    // =============================================
    // COMPONENT MANAGEMENT METHODS
    // =============================================

    /**
     * STORE SPECIAL CASE COMPONENTS - Store components for a special case
     * application
     * 
     * @param specialCaseApplicationId The ID of the special case application
     * @param components               List of component balance DTOs
     * @param createdBy                The user creating the components
     * @return List of saved ClaimSpecialCaseComponent entities
     */

    public List<SpecialCaseComponentBalanceResponseDTO> storeSpecialCaseComponents(
            ClaimSpecialCaseApplication claimSpecialCaseApplication,
            List<com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto.SpecialCaseComponentBalanceDTO> components,
            String createdBy) {

        log.info("Storing components for special case application ID: {}",
                claimSpecialCaseApplication != null ? claimSpecialCaseApplication.getId() : "null");

        // 1. Validate inputs
        if (claimSpecialCaseApplication == null) {
            throw new RuntimeException("Special case application is required");
        }

        if (components == null || components.isEmpty()) {
            log.info("No components to store for special case application: {}",
                    claimSpecialCaseApplication.getId());
            return new ArrayList<>();
        }
        // 3. Save new components
        List<SpecialCaseComponentBalanceResponseDTO> savedComponents = new ArrayList<>();

        for (SpecialCaseComponentBalanceDTO componentDto : components) {
            if (componentDto == null || componentDto.getCode() == null) {
                log.warn("Skipping null component or component with null code");
                continue;
            }

            // Get component master by code
            ComponentMaster componentMaster = componentMasterRepository
                    .findByCode(componentDto.getCode())
                    .orElse(null);

            if (componentMaster == null) {
                log.warn("Component master not found for code: {}", componentDto.getCode());
                // Continue without component master - it's optional
            }

            // Determine component type
            String componentType = determineComponentType(componentDto);

            // Build the entity
            ClaimSpecialCaseComponent component = ClaimSpecialCaseComponent.builder()
                    .specialCaseApplication(claimSpecialCaseApplication)
                    .componentCode(componentDto.getCode())
                    .componentMaster(componentMaster)
                    .componentName(componentDto.getName() != null ? componentDto.getName() : componentDto.getCode())
                    .amount(componentDto.getAmount() != null ? componentDto.getAmount() : BigDecimal.ZERO)
                    .componentType(componentType)
                    .notes("Component from special case calculation")
                    .isActive("Y")
                    .createdBy(createdBy != null ? createdBy : claimSpecialCaseApplication.getCreatedBy())
                    .build();

            ClaimSpecialCaseComponent saved = claimSpecialCaseComponentRepository.save(component);

            // ✅ Convert saved entity back to DTO
            SpecialCaseComponentBalanceResponseDTO savedDto = SpecialCaseComponentBalanceResponseDTO.builder()
                    .code(saved.getComponentCode())
                    .name(saved.getComponentName())
                    .type(saved.getComponentType())
                    .amount(saved.getAmount())
                    .percentalAmount(saved.getPercentageAmount())
                    .build();

            savedComponents.add(savedDto);

            log.info("✅ Saved component: {} with amount: {} and type: {}",
                    componentDto.getCode(),
                    componentDto.getAmount(),
                    componentType);
        }

        log.info("✅ Saved {} components for special case application: {}",
                savedComponents.size(), claimSpecialCaseApplication.getId());

        return savedComponents;
    }

    /**
     * GET COMPONENTS BY SPECIAL CASE APPLICATION ID - Get all components for a
     * special case application
     * 
     * @param specialCaseApplicationId The ID of the special case application
     * @return List of ClaimSpecialCaseComponent entities
     */

    /**
     * Determine the component type based on the component code and metadata
     */
    private String determineComponentType(SpecialCaseComponentBalanceDTO component) {
        if (component == null || component.getCode() == null) {
            return "ELIGIBLE";
        }

        String code = component.getCode().toUpperCase();
        String type = component.getType();

        // If type is explicitly provided, use it
        if (type != null) {
            if (type.equalsIgnoreCase("INTEREST")) {
                return "INTEREST";
            }
            if (type.equalsIgnoreCase("CONTRIBUTION")) {
                return "CONTRIBUTION";
            }
        }

        // Determine by component code
        if (code.startsWith("PF_I") || code.startsWith("P_I") ||
                code.endsWith("_IMC") || code.endsWith("_IEC") ||
                code.contains("INTEREST")) {
            return "INTEREST";
        }

        // Default to ELIGIBLE
        return "ELIGIBLE";
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
}