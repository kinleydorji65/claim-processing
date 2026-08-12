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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // Component code mapping: Request code -> Database code
    private static final Map<String, String> COMPONENT_CODE_MAPPING = new HashMap<>();
    static {
        COMPONENT_CODE_MAPPING.put("GIC", "IGC");  // Map GIC to IGC
        COMPONENT_CODE_MAPPING.put("VIC", "IVC");  // Map VIC to IVC
    }

    @Override
    public List<WrongRemitance> create(ClaimApplication claimApplication, List<WrongRemitanceRequestDTO> requests) {
        List<WrongRemitance> responses = new ArrayList<>();
        for (WrongRemitanceRequestDTO request : requests) {
            log.info("Processing wrong remitance record for NPPF: {}, Year: {}, Application: {}",
                    request.getNppfNumber(), request.getTargetYear(),
                    claimApplication != null ? claimApplication.getApplicationNumber() : "null");

            // Validate required fields
            validateRequired(request);

            WrongRemitance entity;
            boolean isUpdate = false;

            // Check if wrong remitance already exists for this application
            if (claimApplication != null) {
                List<WrongRemitance> existingRecords = wrongRemitanceRepository
                        .findByClaimApplication_Id(claimApplication.getId());
                
                if (!existingRecords.isEmpty()) {
                    entity = existingRecords.get(0);
                    isUpdate = true;
                    log.info("Updating existing wrong remitance record (ID: {}) for application: {}", 
                            entity.getId(), claimApplication.getApplicationNumber());
                } else {
                    entity = new WrongRemitance();
                    log.info("Creating new wrong remitance record for application: {}", 
                            claimApplication.getApplicationNumber());
                }
            } else {
                entity = new WrongRemitance();
                log.info("Creating wrong remitance record without claim application");
            }

            // Build or update entity
            if (isUpdate) {
                updateEntityFromRequest(entity, claimApplication, request);
                entity.setUpdatedBy(SYSTEM_USER);
                entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            } else {
                buildEntityFromRequest(entity, claimApplication, request);
                entity.setCreatedBy(SYSTEM_USER);
                entity.setUpdatedBy(SYSTEM_USER);
            }

            // Save the entity
            WrongRemitance savedEntity = wrongRemitanceRepository.save(entity);

            // ===== HANDLE CHILD COLLECTIONS PROPERLY =====
            
            // 1. Handle Calculation Components - UPSERT (Update or Insert)
            if (savedEntity.getCalculationComponents() == null) {
                savedEntity.setCalculationComponents(new ArrayList<>());
            }
            
            if (request.getComponents() != null && !request.getComponents().isEmpty()) {
                List<WrongRemittanceCalculationComponent> processedComponents = 
                        processWrongRemitanceComponents(savedEntity, request.getComponents());
                // Clear existing and add all processed components
                savedEntity.getCalculationComponents().clear();
                savedEntity.getCalculationComponents().addAll(processedComponents);
                log.info("Processed {} calculation components (upsert)", processedComponents.size());
            } else {
                // If no components in request, clear existing
                savedEntity.getCalculationComponents().clear();
                log.info("Cleared all calculation components");
            }

            // 2. Handle Forfeited Components - UPSERT (Update or Insert)
            if (savedEntity.getForfeitedComponents() == null) {
                savedEntity.setForfeitedComponents(new ArrayList<>());
            }
            
            if (request.getForfeitedRequest() != null && !request.getForfeitedRequest().isEmpty()) {
                List<WrongRemittanceForfeited> processedForfeited = 
                        processForfeitedComponents(savedEntity, request.getForfeitedRequest());
                savedEntity.getForfeitedComponents().clear();
                savedEntity.getForfeitedComponents().addAll(processedForfeited);
                log.info("Processed {} forfeited components (upsert)", processedForfeited.size());
            } else {
                savedEntity.getForfeitedComponents().clear();
                log.info("Cleared all forfeited components");
            }

            // 3. Handle Recalculated Months - UPSERT (Update or Insert)
            if (savedEntity.getRecalculatedMonths() == null) {
                savedEntity.setRecalculatedMonths(new ArrayList<>());
            }
            
            if (request.getRecalculateMonths() != null && !request.getRecalculateMonths().isEmpty()) {
                List<WrongRemittanceRecalculatedMonth> processedMonths = 
                        processRecalculatedMonths(savedEntity, request.getRecalculateMonths());
                savedEntity.getRecalculatedMonths().clear();
                savedEntity.getRecalculatedMonths().addAll(processedMonths);
                log.info("Processed {} recalculated months (upsert)", processedMonths.size());
            } else {
                savedEntity.getRecalculatedMonths().clear();
                log.info("Cleared all recalculated months");
            }

            // Save again to persist the child entities
            WrongRemitance finalEntity = wrongRemitanceRepository.save(savedEntity);

            log.info("Wrong remitance record {} with ID: {}", 
                    isUpdate ? "updated" : "created", finalEntity.getId());
            responses.add(finalEntity);
        }

        return responses;
    }

    @Override
    public List<WrongRemitance> update(ClaimApplication claimApplication, List<WrongRemitanceRequestDTO> requests) {
        return create(claimApplication, requests);
    }

    @Override
    @Transactional(readOnly = true)
    public WrongRemitance getById(Long id) {
        log.info("Fetching wrong remitance record with ID: {}", id);
        
        if (id == null || id <= 0) {
            return null;
        }

        return wrongRemitanceRepository.findById(id)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WrongRemitance> getByClaimApplication(ClaimApplication claimApplication) {
        log.info("Fetching wrong remitance records for application: {}",
                claimApplication != null ? claimApplication.getApplicationNumber() : "null");

        if (claimApplication == null) {
            return new ArrayList<>();
        }

        return wrongRemitanceRepository.findByClaimApplication_Id(claimApplication.getId());
    }

    // ===== UPSERT METHODS FOR CHILD ENTITIES =====

    /**
     * Process WrongRemittanceCalculationComponent entities (UPSERT)
     * If ID is present and > 0, update existing; otherwise create new
     * Maps GIC -> IGC and VIC -> IVC automatically
     * Skips components with null or zero amounts
     */
    private List<WrongRemittanceCalculationComponent> processWrongRemitanceComponents(
            WrongRemitance wrongRemitance,
            List<WrongRemittanceCalculationComponentRequestDTO> requests) {

        List<WrongRemittanceCalculationComponent> components = new ArrayList<>();
        
        if (requests == null || requests.isEmpty()) {
            return components;
        }
        
        List<String> missingComponents = new ArrayList<>();
        int skippedZeroAmount = 0;
        int updatedCount = 0;
        int createdCount = 0;
        
        for (WrongRemittanceCalculationComponentRequestDTO request : requests) {
            // Skip if component code is null or empty
            if (request.getComponentCode() == null || request.getComponentCode().isBlank()) {
                log.warn("Skipping component with null/empty code");
                continue;
            }
            
            // Skip if amount is null or zero
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                skippedZeroAmount++;
                log.debug("Skipping component {} with zero/null amount", request.getComponentCode());
                continue;
            }
            
            // Get the mapped code (GIC -> IGC, VIC -> IVC)
            String originalCode = request.getComponentCode().trim().toUpperCase();
            String mappedCode = COMPONENT_CODE_MAPPING.getOrDefault(originalCode, originalCode);
            
            if (!originalCode.equals(mappedCode)) {
                log.debug("Mapping component code: {} -> {}", originalCode, mappedCode);
            }
            
            // Get component master using mapped code
            Optional<ComponentMaster> componentMasterOpt = componentMasterRepository
                    .findByCode(mappedCode);
            
            if (componentMasterOpt.isEmpty()) {
                missingComponents.add(originalCode + " (mapped to: " + mappedCode + ")");
                log.warn("Component master not found for code: {} (mapped from: {}), skipping", 
                        mappedCode, originalCode);
                continue;
            }
            
            ComponentMaster componentMaster = componentMasterOpt.get();
            
            WrongRemittanceCalculationComponent component;
            
            // Check if ID is present and > 0 -> Update existing
            if (request.getId() != null && request.getId() > 0) {
                // Try to find existing component
                Optional<WrongRemittanceCalculationComponent> existingOpt = 
                        wrongRemitanceComponentRepository.findById(request.getId());
                
                if (existingOpt.isPresent()) {
                    component = existingOpt.get();
                    // Update fields
                    component.setComponentMaster(componentMaster);
                    component.setAmount(request.getAmount());
                    component.setUpdatedBy(SYSTEM_USER);
                    component.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    updatedCount++;
                    log.debug("Updated existing component ID: {} with code: {}", 
                            component.getId(), mappedCode);
                } else {
                    // ID provided but not found - create new
                    component = WrongRemittanceCalculationComponent.builder()
                            .componentMaster(componentMaster)
                            .amount(request.getAmount())
                            .wrongRemitance(wrongRemitance)
                            .createdBy(SYSTEM_USER)
                            .updatedBy(SYSTEM_USER)
                            .build();
                    createdCount++;
                    log.debug("Creating new component (ID {} not found) with code: {}", 
                            request.getId(), mappedCode);
                }
            } else {
                // Create new entity
                component = WrongRemittanceCalculationComponent.builder()
                        .componentMaster(componentMaster)
                        .amount(request.getAmount())
                        .wrongRemitance(wrongRemitance)
                        .createdBy(SYSTEM_USER)
                        .updatedBy(SYSTEM_USER)
                        .build();
                createdCount++;
                log.debug("Creating new component with code: {}", mappedCode);
            }
            
            components.add(component);
        }
        
        if (!missingComponents.isEmpty()) {
            log.warn("⚠️ Missing component masters for codes: {}", String.join(", ", missingComponents));
        }
        
        log.info("Processed {} calculation components ({} updated, {} created, {} skipped zero/null, {} missing)", 
                components.size(), updatedCount, createdCount, skippedZeroAmount, missingComponents.size());
        
        return components;
    }

    /**
     * Process WrongRemittanceForfeited entities (UPSERT)
     * If ID is present and > 0, update existing; otherwise create new
     * Skips forfeited with null or zero amounts
     */
    private List<WrongRemittanceForfeited> processForfeitedComponents(
            WrongRemitance wrongRemitance,
            List<WrongRemittanceForfeitedRequestDTO> requests) {
        
        List<WrongRemittanceForfeited> forfeitedComponents = new ArrayList<>();
        
        if (requests == null || requests.isEmpty()) {
            return forfeitedComponents;
        }
        
        int skippedZeroAmount = 0;
        int updatedCount = 0;
        int createdCount = 0;
        
        for (WrongRemittanceForfeitedRequestDTO request : requests) {
            // Skip if amount is null or zero
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                skippedZeroAmount++;
                log.debug("Skipping forfeited component with zero/null amount");
                continue;
            }
            
            WrongRemittanceForfeited entity;
            
            // Check if ID is present and > 0 -> Update existing
            if (request.getId() != null && request.getId() > 0) {
                Optional<WrongRemittanceForfeited> existingOpt = 
                        forfeitedRepository.findById(request.getId());
                
                if (existingOpt.isPresent()) {
                    entity = existingOpt.get();
                    entity.setComponentCode(request.getComponentCode());
                    entity.setComponentName(request.getComponentName());
                    entity.setAmount(request.getAmount());
                    entity.setUpdatedBy(SYSTEM_USER);
                    entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    updatedCount++;
                    log.debug("Updated existing forfeited ID: {}", entity.getId());
                } else {
                    // ID provided but not found - create new
                    entity = WrongRemittanceForfeited.builder()
                            .componentCode(request.getComponentCode())
                            .componentName(request.getComponentName())
                            .amount(request.getAmount())
                            .wrongRemitance(wrongRemitance)
                            .createdBy(SYSTEM_USER)
                            .updatedBy(SYSTEM_USER)
                            .build();
                    createdCount++;
                    log.debug("Creating new forfeited (ID {} not found)", request.getId());
                }
            } else {
                // Create new entity
                entity = WrongRemittanceForfeited.builder()
                        .componentCode(request.getComponentCode())
                        .componentName(request.getComponentName())
                        .amount(request.getAmount())
                        .wrongRemitance(wrongRemitance)
                        .createdBy(SYSTEM_USER)
                        .updatedBy(SYSTEM_USER)
                        .build();
                createdCount++;
                log.debug("Creating new forfeited component");
            }
            
            forfeitedComponents.add(entity);
        }
        
        log.info("Processed {} forfeited components ({} updated, {} created, {} skipped zero/null)", 
                forfeitedComponents.size(), updatedCount, createdCount, skippedZeroAmount);
        
        return forfeitedComponents;
    }

    /**
     * Process WrongRemittanceRecalculatedMonth entities (UPSERT)
     * If ID is present and > 0, update existing; otherwise create new
     * Skips months with null or zero total amount
     */
    private List<WrongRemittanceRecalculatedMonth> processRecalculatedMonths(
            WrongRemitance wrongRemitance, 
            List<WrongRemittanceRecalculatedMonthRequestDTO> requests) {

        List<WrongRemittanceRecalculatedMonth> months = new ArrayList<>();
        
        if (requests == null || requests.isEmpty()) {
            return months;
        }

        int skippedZeroAmount = 0;
        int updatedCount = 0;
        int createdCount = 0;
        
        for (WrongRemittanceRecalculatedMonthRequestDTO request : requests) {
            // Skip if total amount is null or zero
            if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
                skippedZeroAmount++;
                log.debug("Skipping recalculated month with zero/null total amount");
                continue;
            }
            
            WrongRemittanceRecalculatedMonth entity;
            
            // Check if ID is present and > 0 -> Update existing
            if (request.getId() != null && request.getId() > 0) {
                Optional<WrongRemittanceRecalculatedMonth> existingOpt = 
                        recalculatedMonthRepository.findById(request.getId());
                
                if (existingOpt.isPresent()) {
                    entity = existingOpt.get();
                    // Update fields
                    entity.setMonth(request.getMonth());
                    entity.setMonthName(request.getMonthName());
                    entity.setInvoiceDate(request.getInvoiceDate() != null ? request.getInvoiceDate() : null);
                    entity.setDaysForInterest(request.getDaysForInterest());
                    entity.setInterestRate(request.getInterestRate());
                    entity.setPfMc(request.getPfMc());
                    entity.setPfEc(request.getPfEc());
                    entity.setPfImc(request.getPfImc());
                    entity.setPfIec(request.getPfIec());
                    entity.setPMc(request.getPMc());
                    entity.setPEc(request.getPEc());
                    entity.setPImc(request.getPImc());
                    entity.setPIec(request.getPIec());
                    entity.setGc(request.getGc());
                    entity.setGic(request.getGic());
                    entity.setVc(request.getVc());
                    entity.setVic(request.getVic());
                    entity.setIvc(request.getIvc());
                    entity.setIgc(request.getIgc());
                    entity.setTotalContribution(request.getTotalContribution());
                    entity.setTotalInterest(request.getTotalInterest());
                    entity.setTotalAmount(request.getTotalAmount());
                    entity.setStatus(request.getStatus());
                    entity.setUpdatedBy(SYSTEM_USER);
                    entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    updatedCount++;
                    log.debug("Updated existing recalculated month ID: {}", entity.getId());
                } else {
                    // ID provided but not found - create new
                    entity = buildRecalculatedMonthEntity(wrongRemitance, request);
                    createdCount++;
                    log.debug("Creating new recalculated month (ID {} not found)", request.getId());
                }
            } else {
                // Create new entity
                entity = buildRecalculatedMonthEntity(wrongRemitance, request);
                createdCount++;
                log.debug("Creating new recalculated month");
            }
            
            months.add(entity);
        }
        
        log.info("Processed {} recalculated months ({} updated, {} created, {} skipped zero/null)", 
                months.size(), updatedCount, createdCount, skippedZeroAmount);
        
        return months;
    }

    /**
     * Helper method to build a new RecalculatedMonth entity
     */
    private WrongRemittanceRecalculatedMonth buildRecalculatedMonthEntity(
            WrongRemitance wrongRemitance,
            WrongRemittanceRecalculatedMonthRequestDTO request) {
        
        return WrongRemittanceRecalculatedMonth.builder()
                .month(request.getMonth())
                .monthName(request.getMonthName())
                .invoiceDate(request.getInvoiceDate() != null ? request.getInvoiceDate() : null)
                .daysForInterest(request.getDaysForInterest())
                .interestRate(request.getInterestRate())
                .pfMc(request.getPfMc())
                .pfEc(request.getPfEc())
                .pfImc(request.getPfImc())
                .pfIec(request.getPfIec())
                .pMc(request.getPMc())
                .pEc(request.getPEc())
                .pImc(request.getPImc())
                .pIec(request.getPIec())
                .gc(request.getGc())
                .gic(request.getGic())
                .vc(request.getVc())
                .vic(request.getVic())
                .ivc(request.getIvc())
                .igc(request.getIgc())
                .totalContribution(request.getTotalContribution())
                .totalInterest(request.getTotalInterest())
                .totalAmount(request.getTotalAmount())
                .status(request.getStatus())
                .wrongRemitance(wrongRemitance)
                .createdBy(SYSTEM_USER)
                .updatedBy(SYSTEM_USER)
                .build();
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

    private void buildEntityFromRequest(WrongRemitance entity, ClaimApplication claimApplication,
            WrongRemitanceRequestDTO request) {
        
        if (claimApplication != null) {
            entity.setClaimApplication(claimApplication);
        }
        
        entity.setNppfNumber(request.getNppfNumber());
        entity.setMemberName(request.getMemberName());
        entity.setTargetYear(request.getTargetYear());
        entity.setFromYear(request.getFromYear());
        entity.setToYear(request.getToYear());
        entity.setSelectedMonthCount(request.getSelectedMonthCount());
        entity.setSelectedMonths(request.getSelectedMonths() != null 
                ? String.join(", ", request.getSelectedMonths()) 
                : null);
        entity.setYearsProcessed(request.getYearsProcessed());
        
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
        entity.setCalculationDate(request.getCalculationDate() != null ? request.getCalculationDate() : null);
        
        // Status and message
        entity.setStatus(request.getStatus());
        entity.setMessage(request.getMessage());
    }

    private void updateEntityFromRequest(WrongRemitance entity, ClaimApplication claimApplication,
            WrongRemitanceRequestDTO request) {
        buildEntityFromRequest(entity, claimApplication, request);
    }
}