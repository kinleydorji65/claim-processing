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
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
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
    private final ClaimSpecialCaseComponentRepository claimSpecialCaseComponentRepository;
    private final ComponentMasterRepository componentMasterRepository;
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

            // Check if special case already exists for this claim
            ClaimSpecialCaseApplication checkClaimApplication = claimSpecialCaseApplicationRepository
                    .findByClaimApplicationId(claimApplication.getId())
                    .orElse(null);
            if (checkClaimApplication != null) {
                return ApiResponseDTO.notFound("Special case application already exists for this claim");
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
        List<SpecialCaseComponentBalanceDTO> componentBalances = dto.getComponentBalances();
        
        if (componentBalances != null && !componentBalances.isEmpty()) {
            totalAmount = componentBalances.stream()
                    .filter(Objects::nonNull)
                    .map(SpecialCaseComponentBalanceDTO::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            log.info("Calculated total amount from {} components: {}", componentBalances.size(), totalAmount);
        } 


            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationMapper.toEntity(dto);
            SpecialCaseRefundReasonMaster caseReason = specialCaseRefundReasonMasterRepository.findById(dto.getCaseReasonId()).orElse(null);
            // Set default values
            entity.setSpecialCaseRefundReasonMaster(caseReason != null ? caseReason : null);
            entity.setIsActive("Y");
            entity.setCreatedBy(dto.getCreatedBy());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setClaimApplication(claimApplication);
            entity.setTotalAmount(totalAmount);

            // Save entity
            ClaimSpecialCaseApplication saved = claimSpecialCaseApplicationRepository.saveAndFlush(entity);
            log.info("Special case application created with ID: {}", saved.getId());
            List<SpecialCaseComponentBalanceResponseDTO> specialClaimComponents = storeSpecialCaseComponents(
                    saved,
                    dto.getComponentBalances(),
                    claimApplication.getCreatedBy());
            // Build response
            ClaimSpecialCaseApplicationResponseDto response = claimSpecialCaseApplicationMapper.toResponseDto(saved);
            response.setCaseReasonId(caseReason != null ?caseReason.getId() : null);
            response.setCaseReasonName(caseReason != null ?caseReason.getName() : null);
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
            SpecialCaseRefundReasonMaster caseReason = specialCaseRefundReasonMasterRepository.findById(dto.getCaseReasonId()).orElse(null);

            // Update only allowed fields
            if (dto.getCaseReasonId() != null) {
                entity.setSpecialCaseRefundReasonMaster(caseReason);
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
        List<SpecialCaseComponentBalanceDTO> components,
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

    // 2. Save new components
    List<SpecialCaseComponentBalanceResponseDTO> savedComponents = new ArrayList<>();

    for (SpecialCaseComponentBalanceDTO componentDto : components) {
        if (componentDto == null || componentDto.getCode() == null) {
            log.warn("Skipping null component or component with null code");
            continue;
        }

        // ✅ Get component master by code using the REPOSITORY
        ComponentMaster componentMaster = componentMasterRepository
                .findByCode(componentDto.getCode())
                .orElse(null);

        // ✅ Build the entity with ALL required fields
        ClaimSpecialCaseComponent component = ClaimSpecialCaseComponent.builder()
                .specialCaseApplication(claimSpecialCaseApplication)
                .componentMaster(componentMaster != null ? componentMaster : null)
                .amount(componentDto.getAmount() != null ? componentDto.getAmount() : BigDecimal.ZERO)
                .isActive("Y")
                .createdBy(createdBy != null ? createdBy : claimSpecialCaseApplication.getCreatedBy())
                .build();

        // ✅ Save the component
        ClaimSpecialCaseComponent saved = claimSpecialCaseComponentRepository.save(component);
        log.debug("Saved component with ID: {}", saved.getId());


        // ✅ Convert saved entity back to DTO
        SpecialCaseComponentBalanceResponseDTO savedDto = SpecialCaseComponentBalanceResponseDTO.builder()
                .code(componentMaster != null ? componentMaster.getCode() : componentDto.getCode())
                .name(componentMaster.getName())
                .amount(saved.getAmount())
                .build();

        savedComponents.add(savedDto);

        log.info("✅ Saved component: {} with amount: {}",
                componentDto.getCode(),
                componentDto.getAmount());
    }

    log.info("✅ Saved {} components for special case application: {}",
            savedComponents.size(), claimSpecialCaseApplication.getId());

    return savedComponents;
}
}