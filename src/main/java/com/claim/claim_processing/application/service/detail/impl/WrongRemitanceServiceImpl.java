package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO;
import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO.WrongRemittanceCalculationComponentRequestDTO;
import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO.WrongRemittanceForfeitedRequestDTO;
import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO.WrongRemittanceRecalculatedMonthRequestDTO;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.WrongRemittanceForfeited;
import com.claim.claim_processing.application.entity.calculation.WrongRemittanceCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.WrongRemittanceRecalculatedMonth;
import com.claim.claim_processing.application.entity.detail.WrongRemitance;
import com.claim.claim_processing.application.repository.application.WrongRemittanceForfeitedRepository;
import com.claim.claim_processing.application.repository.calculation.WrongRemittanceCalculationComponentRepository;
import com.claim.claim_processing.application.repository.calculation.WrongRemittanceRecalculatedMonthRepository;
import com.claim.claim_processing.application.repository.detail.WrongRemitanceRepository;
import com.claim.claim_processing.application.service.detail.WrongRemitanceService;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WrongRemitanceServiceImpl implements WrongRemitanceService {

    private final WrongRemitanceRepository wrongRemitanceRepository;
    private final WrongRemittanceCalculationComponentRepository wrongRemitanceComponentRepository;
    private final WrongRemittanceForfeitedRepository forfeitedRepository;
    private final ComponentMasterRepository componentMasterRepository;
    private final WrongRemittanceRecalculatedMonthRepository recalculatedMonthRepository;

    private static final String SYSTEM_USER = "SYSTEM";

    @Override
    public List<WrongRemitance> create(ClaimApplication claimApplication, List<WrongRemitanceRequestDTO> requests) {
        List<WrongRemitance> responses = new ArrayList<>();
        for (WrongRemitanceRequestDTO request : requests) {
            log.info("Creating wrong remitance record for NPPF: {}, Year: {}, Application: {}",
                    request.getNppfNumber(), request.getTargetYear(),
                    claimApplication != null ? claimApplication.getApplicationNumber() : "null");

            // Validate required fields
            validateRequired(request);

            // Check if wrong remitance already exists for this application (One-to-One)
            if (claimApplication != null) {
                boolean exists = wrongRemitanceRepository
                        .existsByClaimApplication_Id(claimApplication.getId());
                if (exists) {
                    throw ClaimException.conflict(
                            "Wrong remitance record already exists for application: "
                                    + claimApplication.getApplicationNumber());
                }
            }

            // Build entity
            WrongRemitance entity = buildEntityFromRequest(claimApplication, request);

            // Set audit fields
            entity.setCreatedBy(SYSTEM_USER);
            entity.setUpdatedBy(SYSTEM_USER);

            // Save
            WrongRemitance savedEntity = wrongRemitanceRepository.save(entity);

            List<WrongRemittanceCalculationComponent> components = addWrongRemitanceComponent(savedEntity, request.getComponents());
            List<WrongRemittanceForfeited> forfeitedComponents = addForFeitedComponent(savedEntity, request.getForfeitedRequest());
            List<WrongRemittanceRecalculatedMonth> recalculatedMonths = addRecalculatedMonths(savedEntity, request.getRecalculateMonths());
            
            savedEntity.setCalculationComponents(components);
            savedEntity.setForfeitedComponents(forfeitedComponents);
            savedEntity.setRecalculatedMonths(recalculatedMonths);
            log.info("Wrong remitance record created with ID: {}", savedEntity.getId());
            responses.add(savedEntity);
        }

        return responses;
    }

    @Override
    public List<WrongRemitance> update(ClaimApplication claimApplication, List<WrongRemitanceRequestDTO> requests) {
        List<WrongRemitance> responses = new ArrayList<>();
        for (WrongRemitanceRequestDTO request : requests) {
            log.info("Updating wrong remitance record for NPPF: {}, Year: {}, Application: {}",
                    request.getNppfNumber(), request.getTargetYear(),
                    claimApplication != null ? claimApplication.getApplicationNumber() : "null");

            // Validate required fields
            validateRequired(request);

            // Find existing entity
            if (request.getId() == null) {
                throw ClaimException.singleValidationError(
                        "id",
                        "Wrong remittance ID is required for update");
            }

            WrongRemitance existingEntity = wrongRemitanceRepository
                    .findById(request.getId())
                    .orElseThrow(() -> ClaimException.resourceNotFound(
                            "Wrong remittance record",
                            request.getId().toString()
                    ));

            // Verify the claim application matches
            if (claimApplication != null && 
                (existingEntity.getClaimApplication() == null || 
                 !existingEntity.getClaimApplication().getId().equals(claimApplication.getId()))) {
                throw ClaimException.conflict(
                        "Wrong remittance record does not belong to the specified application");
            }

            // Update entity
            updateEntityFromRequest(existingEntity, request);
            existingEntity.setUpdatedBy(SYSTEM_USER);

            // Save
            WrongRemitance updatedEntity = wrongRemitanceRepository.save(existingEntity);
            List<WrongRemittanceCalculationComponent> components = addWrongRemitanceComponent(updatedEntity, request.getComponents());
            List<WrongRemittanceForfeited> forfeitedComponents = addForFeitedComponent(updatedEntity, request.getForfeitedRequest());
            List<WrongRemittanceRecalculatedMonth> recalculatedMonths = addRecalculatedMonths(updatedEntity, request.getRecalculateMonths());
            
            
            updatedEntity.setCalculationComponents(components);
            updatedEntity.setForfeitedComponents(forfeitedComponents);
            updatedEntity.setRecalculatedMonths(recalculatedMonths);
            log.info("Wrong remitance record updated with ID: {}", updatedEntity.getId());
            responses.add(updatedEntity);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public WrongRemitance getById(Long id) {
        log.info("Fetching wrong remitance record with ID: {}", id);

        return wrongRemitanceRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Wrong remitance record",
                        id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WrongRemitance> getByClaimApplication(ClaimApplication claimApplication) {
        log.info("Fetching wrong remitance record for application: {}",
                claimApplication != null ? claimApplication.getApplicationNumber() : "null");

        if (claimApplication == null) {
            return new ArrayList<>();
        }

        // FIXED: Added 'return' statement
        return wrongRemitanceRepository.findByClaimApplication_Id(claimApplication.getId());
    }

    // ===== PRIVATE HELPER METHODS =====

    private void validateRequired(WrongRemitanceRequestDTO request) {
        if (request.getNppfNumber() == null || request.getNppfNumber().isBlank()) {
            throw ClaimException.singleValidationError(
                    "nppfNumber",
                    "NPPF number is required");
        }
        if (request.getTargetYear() == null || request.getTargetYear().isBlank()) {
            throw ClaimException.singleValidationError(
                    "targetYear",
                    "Target year is required");
        }
    }

    private WrongRemitance buildEntityFromRequest(ClaimApplication claimApplication,
            WrongRemitanceRequestDTO request) {
        return WrongRemitance.builder()
                .claimApplication(claimApplication)
                .nppfNumber(request.getNppfNumber())
                .memberName(request.getMemberName())
                .targetYear(request.getTargetYear())
                // Opening balances
                .openingPfMc(request.getOpeningPfMc())
                .openingPfEc(request.getOpeningPfEc())
                .openingPfImc(request.getOpeningPfImc())
                .openingPfIec(request.getOpeningPfIec())
                .openingPMc(request.getOpeningPMc())
                .openingPEc(request.getOpeningPEc())
                .openingPImc(request.getOpeningPImc())
                .openingPIec(request.getOpeningPIec())
                .openingGc(request.getOpeningGc())
                .openingGic(request.getOpeningGic())
                .openingVc(request.getOpeningVc())
                .openingVic(request.getOpeningVic())
                .openingIvc(request.getOpeningIvc())
                .openingIgc(request.getOpeningIgc())
                // Closing balances
                .closingPfMc(request.getClosingPfMc())
                .closingPfEc(request.getClosingPfEc())
                .closingPfImc(request.getClosingPfImc())
                .closingPfIec(request.getClosingPfIec())
                .closingPMc(request.getClosingPMc())
                .closingPEc(request.getClosingPEc())
                .closingPImc(request.getClosingPImc())
                .closingPIec(request.getClosingPIec())
                .closingGc(request.getClosingGc())
                .closingGic(request.getClosingGic())
                .closingVc(request.getClosingVc())
                .closingVic(request.getClosingVic())
                .closingIvc(request.getClosingIvc())
                .closingIgc(request.getClosingIgc())
                // Recalculation totals
                .totalRecalculatedContributions(request.getTotalRecalculatedContributions())
                .totalRecalculatedInterest(request.getTotalRecalculatedInterest())
                .totalRecalculatedAmount(request.getTotalRecalculatedAmount())
                // Configuration
                .withInterest(request.getWithInterest() != null && request.getWithInterest() ? "Y" : "N")
                .appliedInterestRate(request.getAppliedInterestRate())
                .yearBasis(request.getYearBasis())
                .calculationDate(request.getCalculationDate())
                // Selected months
                .selectedMonths(request.getSelectedMonths() != null
                        ? String.join(", ", request.getSelectedMonths())
                        : null)
                .selectedMonthCount(request.getSelectedMonthCount())
                // Status
                .status(request.getStatus())
                .message(request.getMessage())
                .yearsProcessed(request.getYearsProcessed())
                .fromYear(request.getFromYear())
                .toYear(request.getToYear())
                .build();
    }

    private void updateEntityFromRequest(WrongRemitance entity, WrongRemitanceRequestDTO request) {
        entity.setNppfNumber(request.getNppfNumber());
        entity.setMemberName(request.getMemberName());
        entity.setTargetYear(request.getTargetYear());
        // Opening balances
        entity.setOpeningPfMc(request.getOpeningPfMc());
        entity.setOpeningPfEc(request.getOpeningPfEc());
        entity.setOpeningPfImc(request.getOpeningPfImc());
        entity.setOpeningPfIec(request.getOpeningPfIec());
        entity.setOpeningPMc(request.getOpeningPMc());
        entity.setOpeningPEc(request.getOpeningPEc());
        entity.setOpeningPImc(request.getOpeningPImc());
        entity.setOpeningPIec(request.getOpeningPIec());
        entity.setOpeningGc(request.getOpeningGc());
        entity.setOpeningGic(request.getOpeningGic());
        entity.setOpeningVc(request.getOpeningVc());
        entity.setOpeningVic(request.getOpeningVic());
        entity.setOpeningIvc(request.getOpeningIvc());
        entity.setOpeningIgc(request.getOpeningIgc());
        // Closing balances
        entity.setClosingPfMc(request.getClosingPfMc());
        entity.setClosingPfEc(request.getClosingPfEc());
        entity.setClosingPfImc(request.getClosingPfImc());
        entity.setClosingPfIec(request.getClosingPfIec());
        entity.setClosingPMc(request.getClosingPMc());
        entity.setClosingPEc(request.getClosingPEc());
        entity.setClosingPImc(request.getClosingPImc());
        entity.setClosingPIec(request.getClosingPIec());
        entity.setClosingGc(request.getClosingGc());
        entity.setClosingGic(request.getClosingGic());
        entity.setClosingVc(request.getClosingVc());
        entity.setClosingVic(request.getClosingVic());
        entity.setClosingIvc(request.getClosingIvc());
        entity.setClosingIgc(request.getClosingIgc());
        // Recalculation totals
        entity.setTotalRecalculatedContributions(request.getTotalRecalculatedContributions());
        entity.setTotalRecalculatedInterest(request.getTotalRecalculatedInterest());
        entity.setTotalRecalculatedAmount(request.getTotalRecalculatedAmount());
        // Configuration
        entity.setWithInterest(request.getWithInterest() != null && request.getWithInterest() ? "Y" : "N");
        entity.setAppliedInterestRate(request.getAppliedInterestRate());
        entity.setYearBasis(request.getYearBasis());
        entity.setCalculationDate(request.getCalculationDate());
        // Selected months
        entity.setSelectedMonths(
                request.getSelectedMonths() != null ? String.join(", ", request.getSelectedMonths())
                        : null);
        entity.setSelectedMonthCount(request.getSelectedMonthCount());
        // Status
        entity.setStatus(request.getStatus());
        entity.setMessage(request.getMessage());
        entity.setYearsProcessed(request.getYearsProcessed());
        entity.setFromYear(request.getFromYear());
        entity.setToYear(request.getToYear());
    }

    private List<WrongRemittanceCalculationComponent> addWrongRemitanceComponent(
        WrongRemitance wrongRemitance,
        List<WrongRemittanceCalculationComponentRequestDTO> requests) {

    List<WrongRemittanceCalculationComponent> savedComponents = new ArrayList<>();
    
    if (requests == null || requests.isEmpty()) {
        return savedComponents;
    }
    
    for (WrongRemittanceCalculationComponentRequestDTO request : requests) {
        // Get component master
        ComponentMaster componentMaster = componentMasterRepository
                .findByCode(request.getComponentCode())
                .orElseThrow(() -> new RuntimeException("Component master not found for code: " + request.getComponentCode()));
        
        // Check if component already exists
        WrongRemittanceCalculationComponent entity = wrongRemitanceComponentRepository
                .findById(request.getId())
                .orElse(null);
        
        if (entity == null) {
            // Create new entity
            entity = WrongRemittanceCalculationComponent.builder()
                    .componentMaster(componentMaster)
                    .amount(request.getAmount())
                    .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "SYSTEM")
                    .build();
        } else {
            // Update existing entity
            entity.setComponentMaster(componentMaster);
            entity.setAmount(request.getAmount());
            entity.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "SYSTEM");
        }
        
        // Set the wrong remitance relationship
        entity.setWrongRemitance(wrongRemitance);
        
        // Save
        WrongRemittanceCalculationComponent savedEntity = wrongRemitanceComponentRepository.save(entity);
        savedComponents.add(savedEntity);
    }
    
    return savedComponents;
}

private List<WrongRemittanceForfeited> addForFeitedComponent(
        WrongRemitance wrongRemitance,
        List<WrongRemittanceForfeitedRequestDTO> requests) {
    
    List<WrongRemittanceForfeited> savedForfeitedComponents = new ArrayList<>();
    
    if (requests == null || requests.isEmpty()) {
        return savedForfeitedComponents;
    }
    
    for (WrongRemittanceForfeitedRequestDTO request : requests) {
        WrongRemittanceForfeited entity = forfeitedRepository.findById(request.getId()).orElse(null);
        
        if (entity == null) {
            // Create new entity
            entity = WrongRemittanceForfeited.builder()
                    .componentCode(request.getComponentCode())
                    .componentName(request.getComponentName())
                    .amount(request.getAmount())
                    .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "SYSTEM")
                    .build();
        } else {
            // Update existing entity
            if (request.getComponentCode() != null) {
                entity.setComponentCode(request.getComponentCode());
            }
            if (request.getComponentName() != null) {
                entity.setComponentName(request.getComponentName());
            }
            if (request.getAmount() != null) {
                entity.setAmount(request.getAmount());
            }
            entity.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "SYSTEM");
        }
        
        // Set the wrong remitance relationship
        entity.setWrongRemitance(wrongRemitance);
        
        // Save
        WrongRemittanceForfeited savedEntity = forfeitedRepository.save(entity);
        savedForfeitedComponents.add(savedEntity);
    }
    
    return savedForfeitedComponents;
}

private List<WrongRemittanceRecalculatedMonth> addRecalculatedMonths(
        WrongRemitance wrongRemitance, 
        List<WrongRemittanceRecalculatedMonthRequestDTO> requests) {

    List<WrongRemittanceRecalculatedMonth> savedMonths = new ArrayList<>();
    
    if (requests == null || requests.isEmpty()) {
        return savedMonths;
    }

    for (WrongRemittanceRecalculatedMonthRequestDTO request : requests) {
        // Always create new entity using builder (no update)
        WrongRemittanceRecalculatedMonth entity = WrongRemittanceRecalculatedMonth.builder()
                .month(request.getMonth())
                .invoiceDate(request.getInvoiceDate())
                .daysForInterest(request.getDaysForInterest())
                .interestRate(request.getInterestRate())
                // PF Components
                .pfMc(request.getPfMc())
                .pfEc(request.getPfEc())
                .pfImc(request.getPfImc())
                .pfIec(request.getPfIec())
                // Pension Components
                .pMc(request.getPMc())
                .pEc(request.getPEc())
                .pImc(request.getPImc())
                .pIec(request.getPIec())
                // Gratuity Components
                .gc(request.getGc())
                .gic(request.getGic())
                // Voluntary Components
                .vc(request.getVc())
                .vic(request.getVic())
                // Interest on Voluntary & Gratuity
                .ivc(request.getIvc())
                .igc(request.getIgc())
                // Totals
                .totalContribution(request.getTotalContribution())
                .totalInterest(request.getTotalInterest())
                .totalAmount(request.getTotalAmount())
                .status(request.getStatus())
                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "SYSTEM")
                .updatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "SYSTEM")
                .build();
        
        // Set the wrong remitance relationship
        entity.setWrongRemitance(wrongRemitance);
        
        // Save the entity
        WrongRemittanceRecalculatedMonth savedEntity = recalculatedMonthRepository.save(entity);
        savedMonths.add(savedEntity);
    }
    
    // Update the wrong remitance with the new months
    wrongRemitance.setRecalculatedMonths(savedMonths);
    
    return savedMonths;
}
}