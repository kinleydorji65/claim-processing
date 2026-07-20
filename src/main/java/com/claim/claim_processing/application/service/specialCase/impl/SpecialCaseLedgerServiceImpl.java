package com.claim.claim_processing.application.service.specialCase.impl;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO;
import com.claim.claim_processing.application.entity.claimDetail.ClaimAccountingEvent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerEntry;
import com.claim.claim_processing.application.repository.claimDetail.ClaimAccountingEventRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimLedgerEntryRepository;
import com.claim.claim_processing.application.service.specialCase.SpecialCaseLedgerService;
import com.claim.claim_processing.common.entities.common.CoaAccountMapping;
import com.claim.claim_processing.common.entities.common.CoaSubAccount;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.common.CoaAccountMappingRepository;
import com.claim.claim_processing.common.repository.common.CoaMainAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaSubAccountRepository;
import com.claim.claim_processing.common.repository.others.MemberDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialCaseLedgerServiceImpl implements SpecialCaseLedgerService {

    private final ClaimAccountingEventRepository accountingEventRepository;
    private final ClaimLedgerEntryRepository ledgerEntryRepository;
    private final CoaAccountMappingRepository coaAccountMappingRepository;
    private final CoaMainAccountRepository coaMainAccountRepository;
    private final CoaSubAccountRepository coaSubAccountRepository;
    private final MemberDetailRepository memberDetailRepository;

    private static final String EVENT_TYPE_SPECIAL_CASE = "SPECIAL_CASE";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_POSTED = "POSTED";

    // Component codes for special case conversion
    private static final String COMPONENT_PENSION_CONVERSION = "PENSION_CONVERSION";
    private static final String COMPONENT_LAPSED_CONVERSION = "LAPSED_CONVERSION";
    private static final String COMPONENT_BANK = "BANK";

    // Case Types
    private static final String CASE_TYPE_PENSION_TO_LUMPSUM = "CONVERSION_FROM_PENSION_TO_LUMSUM";
    private static final String CASE_TYPE_CLAIM_FORFEITED = "CLAIM_FORFEITED_COMPONENT";
    private static final String CASE_TYPE_SPECIAL_NORMAL_CLAIM = "SPECIAL_NORMAL_CLAIM";

    @Override
    @Transactional
    public AccountingEventResponseDto createSpecialCaseLedgerEntries(
            GeneralSpecialCaseResponse specialCaseResponse, 
            String createdBy) {
        
        log.info("========== START: createSpecialCaseLedgerEntries ==========");
        log.info("Special Case ID: {}, Status: {}, Agency: {}",
                specialCaseResponse.getId(),
                specialCaseResponse.getStatusName(),
                specialCaseResponse.getMemberCategoryId());

        String specialCaseReference = "SPC-" + specialCaseResponse.getId();

        // 1. Validate claim is approved
        if (!"Approved".equalsIgnoreCase(specialCaseResponse.getStatusName())) {
            throw new RuntimeException("Special Case must be Approved to create ledger entries. Current status: " +
                    specialCaseResponse.getStatusName());
        }

        // 2. Check if entries already exist
        if (hasLedgerEntries(specialCaseResponse.getId())) {
            throw new RuntimeException("Ledger entries already exist for special case: " + 
                    specialCaseResponse.getId());
        }

        String agencyCategoryId = specialCaseResponse.getMemberCategoryId();
        String caseType = specialCaseResponse.getSpecialCaseDetail() != null 
                ? specialCaseResponse.getSpecialCaseDetail().getCaseType() 
                : null;

        // 3. Build component amounts based on case type
        Map<String, BigDecimal> componentAmounts = buildComponentAmountsByCaseType(specialCaseResponse);
        log.info("Special Case Components for case type '{}': {}", caseType, componentAmounts);

        if (componentAmounts.isEmpty()) {
            throw new RuntimeException("No component amounts found for special case: " + 
                    specialCaseResponse.getId());
        }

        // 4. Calculate totals
        BigDecimal totalEligible = calculateTotalAmount(componentAmounts);
        log.info("Total Eligible Amount: {}", totalEligible);

        // 5. Get final payable amount based on case type
        BigDecimal finalPayable = getFinalPayableByCaseType(specialCaseResponse);
        log.info("Final Payable Amount: {}", finalPayable);

        // 6. Get COA Mappings for SPECIAL_CASE
        List<CoaAccountMapping> specialCaseMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_SPECIAL_CASE, agencyCategoryId);

        if (specialCaseMappings.isEmpty()) {
            log.error("No SPECIAL_CASE mappings found for agency category: {}", agencyCategoryId);
            throw new RuntimeException("No COA mappings found for SPECIAL_CASE with agency category: " + agencyCategoryId);
        }

        log.info("SPECIAL_CASE Mappings found: {}", specialCaseMappings.size());
        for (CoaAccountMapping mapping : specialCaseMappings) {
            log.info("Mapping: Component={}, DRCR={}, Main={}, Sub={}", 
                    mapping.getComponentCode(), mapping.getDrcr(), 
                    mapping.getMainAccountCode(), mapping.getSubAccountCode());
        }

        // 7. Create and SAVE Accounting Event
        ClaimAccountingEvent event = createSpecialCaseAccountingEvent(
                specialCaseResponse, createdBy);
        event = accountingEventRepository.save(event);
        log.info("Accounting Event created with ID: {}", event.getId());

        // 8. Generate Ledger Entries
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // 8a. Process DEBIT mappings for each component
        for (CoaAccountMapping mapping : specialCaseMappings) {
            String componentCode = mapping.getComponentCode();

            // Skip BANK - handled separately as CREDIT
            if (COMPONENT_BANK.equals(componentCode)) {
                continue;
            }

            BigDecimal amount = componentAmounts.getOrDefault(componentCode, BigDecimal.ZERO);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("Skipping zero amount for component: {}", componentCode);
                continue;
            }

            CoaSubAccount subAccount = coaSubAccountRepository.findBySubAccountCode(mapping.getSubAccountCode())
                .orElseThrow(() -> new RuntimeException("SubAccount not found: " + mapping.getSubAccountCode()));
            seqNo++;
            ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                    .accountingEventId(event.getId())
                    .seqNo(seqNo)
                    .mainAccountCode(mapping.getMainAccountCode())
                    .subAccountCode(mapping.getSubAccountCode())
                    .drcr(mapping.getDrcr()) // DEBIT for special case conversion
                    .amount(amount)
                    .entryRole(mapping.getEntryRole())
                    .componentCode(componentCode)
                    .narration(subAccount.getSubAccountName())
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entry);
            log.info("DEBIT Entry: SEQ={}, Component={}, Amount={}, Account={}/{}",
                    seqNo, componentCode, amount, 
                    mapping.getMainAccountCode(), mapping.getSubAccountCode());
        }

        // 8b. BANK Entry (CREDIT) - Net payment to member
        if (finalPayable.compareTo(BigDecimal.ZERO) > 0) {
            CoaAccountMapping bankMapping = findMappingByComponent(specialCaseMappings, COMPONENT_BANK);
            if (bankMapping != null) {
                CoaSubAccount subAccount = coaSubAccountRepository.findBySubAccountCode(bankMapping.getSubAccountCode())
                .orElseThrow(() -> new RuntimeException("SubAccount not found: " + bankMapping.getSubAccountCode()));
                seqNo++;
                ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(bankMapping.getMainAccountCode())
                        .subAccountCode(bankMapping.getSubAccountCode())
                        .drcr(bankMapping.getDrcr())
                        .amount(finalPayable)
                        .entryRole("BANK")
                        .componentCode(COMPONENT_BANK)
                        .narration(subAccount.getSubAccountName())
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(entry);
                log.info("CREDIT Entry: SEQ={}, BANK, Amount={}", seqNo, finalPayable);
            } else {
                log.warn("BANK mapping not found for special case: {}", agencyCategoryId);
            }
        }

        if (ledgerEntries.isEmpty()) {
            throw new RuntimeException("No ledger entries were created for special case: " + 
                    specialCaseResponse.getId());
        }

        log.info("Total ledger entries to save: {}", ledgerEntries.size());

        // 9. Save all ledger entries
        List<ClaimLedgerEntry> savedEntries = ledgerEntryRepository.saveAll(ledgerEntries);
        log.info("{} ledger entries saved for special case: {}", savedEntries.size(), specialCaseReference);

        // 10. Calculate totals
        BigDecimal totalDr = calculateTotal(savedEntries, "D");
        BigDecimal totalCr = calculateTotal(savedEntries, "C");
        log.info("Total DR: {}, Total CR: {}", totalDr, totalCr);

        // 11. Validate balance
        if (totalDr.compareTo(totalCr) != 0) {
            log.error("LEDGER NOT BALANCED! DR: {}, CR: {}, Difference: {}",
                    totalDr, totalCr, totalDr.subtract(totalCr));
            
            log.debug("Ledger entries:");
            for (ClaimLedgerEntry entry : savedEntries) {
                log.debug("SEQ: {}, DRCR: {}, Component: {}, Amount: {}", 
                        entry.getSeqNo(), entry.getDrcr(), entry.getComponentCode(), entry.getAmount());
            }
            
            throw new RuntimeException(
                    "Ledger entries do not balance! Total DR: " + totalDr +
                            ", Total CR: " + totalCr +
                            ", Difference: " + totalDr.subtract(totalCr));
        }

        event.setStatus(STATUS_POSTED);
        event.setPostedBy(createdBy);
        event.setPostedAt(LocalDateTime.now());
        event.setUpdatedBy(createdBy);
        event.setUpdatedAt(LocalDateTime.now());
        ClaimAccountingEvent updatedEvent = accountingEventRepository.save(event);

        log.info("========== END: createSpecialCaseLedgerEntries SUCCESS ==========");
        return buildResponse(updatedEvent, savedEntries);
    }

    // =============================================
    // BUILD COMPONENT AMOUNTS BY CASE TYPE
    // =============================================
    
    /**
     * Build component amounts based on case type
     */
    /**
 * Build component amounts based on case type
 */
private Map<String, BigDecimal> buildComponentAmountsByCaseType(
        GeneralSpecialCaseResponse specialCaseResponse) {
    
    ClaimSpecialCaseResponse specialCaseDetail = specialCaseResponse.getSpecialCaseDetail();
    Map<String, BigDecimal> componentMap = new HashMap<>();
    
    if (specialCaseDetail == null) {
        log.warn("Special case detail is null");
        return componentMap;
    }

    String caseType = specialCaseDetail.getCaseType();
    log.info("Building component amounts for case type: {}", caseType);

    // =============================================
    // CASE 1: PENSION TO LUMP SUM CONVERSION
    // =============================================
    if (CASE_TYPE_PENSION_TO_LUMPSUM.equals(caseType)) {
        log.info("Processing PENSION TO LUMP SUM CONVERSION");
        
        BigDecimal totalPensionAmount = specialCaseDetail.getTotalPensionAmount() != null 
                ? specialCaseDetail.getTotalPensionAmount() : BigDecimal.ZERO;
        BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount() != null 
                ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
        
        // Use eligible amount if available, otherwise use total pension amount
        BigDecimal amount = eligibleAmount.compareTo(BigDecimal.ZERO) > 0 
                ? eligibleAmount : totalPensionAmount;
        
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            componentMap.put(COMPONENT_PENSION_CONVERSION, amount);
            log.info("Added PENSION_CONVERSION: {}", amount);
        }
        
        return componentMap;
    }

    // =============================================
    // CASE 2: CLAIM FORFEITED COMPONENT
    // =============================================
    if (CASE_TYPE_CLAIM_FORFEITED.equals(caseType)) {
        log.info("Processing CLAIM FORFEITED COMPONENT");
        
        BigDecimal totalForfeitedAmount = specialCaseDetail.getTotalForfeitedAmount() != null 
                ? specialCaseDetail.getTotalForfeitedAmount() : BigDecimal.ZERO;
        BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount() != null 
                ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
        
        // Use eligible amount if available (80% of forfeited), otherwise use total forfeited
        BigDecimal amount = eligibleAmount.compareTo(BigDecimal.ZERO) > 0 
                ? eligibleAmount : totalForfeitedAmount;
        
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            componentMap.put(COMPONENT_LAPSED_CONVERSION, amount);
            log.info("Added LAPSED_CONVERSION: {}", amount);
        }
        
        return componentMap;
    }

    // =============================================
    // CASE 3: SPECIAL NORMAL CLAIM (WITH COMPONENTS)
    // =============================================
    if (CASE_TYPE_SPECIAL_NORMAL_CLAIM.equals(caseType)) {
        log.info("Processing SPECIAL NORMAL CLAIM with components");
        
        // Get components from specialCaseDetail
        List<SpecialCaseComponentBalanceResponseDTO> components = specialCaseDetail.getComponents();
        
        if (components != null && !components.isEmpty()) {
            log.info("Found {} components in special case detail", components.size());
            
            // ✅ Map each component individually using mapSpecialCaseComponentToLedgerCode
            for (SpecialCaseComponentBalanceResponseDTO component : components) {
                if (component != null && component.getAmount() != null 
                    && component.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    
                    // Map component code to ledger component code
                    String ledgerCode = mapSpecialCaseComponentToLedgerCode(
                            component.getCode(), 
                            component.getType()
                    );
                    
                    if (ledgerCode != null) {
                        componentMap.merge(ledgerCode, component.getAmount(), BigDecimal::add);
                        log.info("Mapped component: {} (type: {}) -> {} = {}", 
                                component.getCode(), 
                                component.getType(), 
                                ledgerCode, 
                                component.getAmount());
                    } else {
                        log.warn("No mapping found for component: {}, type: {}", 
                                component.getCode(), component.getType());
                        // Default to PENSION_CONVERSION
                        componentMap.merge(COMPONENT_PENSION_CONVERSION, component.getAmount(), BigDecimal::add);
                        log.info("Defaulted component: {} -> {} = {}", 
                                component.getCode(), COMPONENT_PENSION_CONVERSION, component.getAmount());
                    }
                }
            }
            
            // If no components were mapped, fallback to total
            if (componentMap.isEmpty()) {
                BigDecimal totalAmount = components.stream()
                        .filter(Objects::nonNull)
                        .map(SpecialCaseComponentBalanceResponseDTO::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    componentMap.put(COMPONENT_PENSION_CONVERSION, totalAmount);
                    log.info("Added total components as PENSION_CONVERSION: {}", totalAmount);
                }
            }
            
        } else {
            log.warn("No components found for SPECIAL_NORMAL_CLAIM, using fallback");
            
            // Fallback: Use eligible claim amount or requested amount
            BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount() != null 
                    ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
            BigDecimal requestedAmount = specialCaseDetail.getRequestedAmount() != null 
                    ? specialCaseDetail.getRequestedAmount() : BigDecimal.ZERO;
            
            BigDecimal amount = eligibleAmount.compareTo(BigDecimal.ZERO) > 0 
                    ? eligibleAmount : requestedAmount;
            
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                componentMap.put(COMPONENT_PENSION_CONVERSION, amount);
                log.info("Added fallback PENSION_CONVERSION: {}", amount);
            }
        }
        
        return componentMap;
    }

    // =============================================
    // DEFAULT / UNKNOWN CASE TYPE
    // =============================================
    log.warn("Unknown case type: {}, using default logic", caseType);
    
    // Try to get any available amount
    BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount() != null 
            ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
    BigDecimal pensionAmount = specialCaseDetail.getTotalPensionAmount() != null 
            ? specialCaseDetail.getTotalPensionAmount() : BigDecimal.ZERO;
    BigDecimal forfeitedAmount = specialCaseDetail.getTotalForfeitedAmount() != null 
            ? specialCaseDetail.getTotalForfeitedAmount() : BigDecimal.ZERO;
    
    BigDecimal amount = eligibleAmount.compareTo(BigDecimal.ZERO) > 0 
            ? eligibleAmount 
            : (pensionAmount.compareTo(BigDecimal.ZERO) > 0 ? pensionAmount : forfeitedAmount);
    
    if (amount.compareTo(BigDecimal.ZERO) > 0) {
        componentMap.put(COMPONENT_PENSION_CONVERSION, amount);
        log.info("Added default PENSION_CONVERSION: {}", amount);
    }
    
    return componentMap;
}

    /**
     * Get final payable amount based on case type
     */
    private BigDecimal getFinalPayableByCaseType(GeneralSpecialCaseResponse specialCaseResponse) {
        ClaimSpecialCaseResponse specialCaseDetail = specialCaseResponse.getSpecialCaseDetail();
        if (specialCaseDetail == null) {
            return BigDecimal.ZERO;
        }

        String caseType = specialCaseDetail.getCaseType();
        log.info("Getting final payable for case type: {}", caseType);

        // For PENSION_TO_LUMPSUM: use eligibleClaimAmount or totalPensionAmount
        if (CASE_TYPE_PENSION_TO_LUMPSUM.equals(caseType)) {
            BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount() != null 
                    ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
            BigDecimal pensionAmount = specialCaseDetail.getTotalPensionAmount() != null 
                    ? specialCaseDetail.getTotalPensionAmount() : BigDecimal.ZERO;
            return eligibleAmount.compareTo(BigDecimal.ZERO) > 0 ? eligibleAmount : pensionAmount;
        }

        // For CLAIM_FORFEITED: use eligibleClaimAmount or totalForfeitedAmount
        if (CASE_TYPE_CLAIM_FORFEITED.equals(caseType)) {
            BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount() != null 
                    ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
            BigDecimal forfeitedAmount = specialCaseDetail.getTotalForfeitedAmount() != null 
                    ? specialCaseDetail.getTotalForfeitedAmount() : BigDecimal.ZERO;
            return eligibleAmount.compareTo(BigDecimal.ZERO) > 0 ? eligibleAmount : forfeitedAmount;
        }

        // For SPECIAL_NORMAL_CLAIM: sum of all components
        if (CASE_TYPE_SPECIAL_NORMAL_CLAIM.equals(caseType)) {
            List<SpecialCaseComponentBalanceResponseDTO> components = specialCaseDetail.getComponents();
            if (components != null && !components.isEmpty()) {
                BigDecimal total = components.stream()
                        .filter(Objects::nonNull)
                        .map(SpecialCaseComponentBalanceResponseDTO::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    return total;
                }
            }
            
            // Fallback
            BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount() != null 
                    ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
            BigDecimal requestedAmount = specialCaseDetail.getRequestedAmount() != null 
                    ? specialCaseDetail.getRequestedAmount() : BigDecimal.ZERO;
            return eligibleAmount.compareTo(BigDecimal.ZERO) > 0 ? eligibleAmount : requestedAmount;
        }

        // Default: use eligibleClaimAmount
        return specialCaseDetail.getEligibleClaimAmount() != null 
                ? specialCaseDetail.getEligibleClaimAmount() : BigDecimal.ZERO;
    }

    /**
     * Map special case component code to ledger component code
     */
    private String mapSpecialCaseComponentToLedgerCode(String componentCode, String componentType) {
        if (componentCode == null) {
            return null;
        }
        
        String code = componentCode.toUpperCase().trim();
        String type = componentType != null ? componentType.toUpperCase().trim() : "";
        
        // PF components
        if (code.startsWith("PF_")) {
            if (code.contains("MC") || code.contains("EC") || code.contains("GC") || code.contains("VC")) {
                return COMPONENT_PENSION_CONVERSION;
            }
        }
        
        // Pension components
        if (code.startsWith("P_") || code.startsWith("PC_")) {
            if (code.contains("MC") || code.contains("EC")) {
                return COMPONENT_PENSION_CONVERSION;
            }
        }
        
        // Interest components
        if (code.contains("IMC") || code.contains("IEC") || code.contains("IC")) {
            return COMPONENT_PENSION_CONVERSION;
        }
        
        // Lapsed/Forfeited components
        if (type.contains("FORFEITED") || type.contains("LAPSED")) {
            return COMPONENT_LAPSED_CONVERSION;
        }
        
        if (code.contains("LAPSE") || code.contains("FORFEIT")) {
            return COMPONENT_LAPSED_CONVERSION;
        }
        
        // Default to pension conversion
        return COMPONENT_PENSION_CONVERSION;
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    /**
     * Calculate total amount from a map
     */
    private BigDecimal calculateTotalAmount(Map<String, BigDecimal> amountMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : amountMap.values()) {
            if (amount != null) {
                total = total.add(amount);
            }
        }
        return total;
    }

    /**
     * Calculate total for DR or CR
     */
    private BigDecimal calculateTotal(List<ClaimLedgerEntry> entries, String drcr) {
        return entries.stream()
                .filter(e -> drcr.equals(e.getDrcr()))
                .map(ClaimLedgerEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Find mapping by component code
     */
    private CoaAccountMapping findMappingByComponent(
            List<CoaAccountMapping> mappings, 
            String componentCode) {
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }
        return mappings.stream()
                .filter(m -> componentCode.equals(m.getComponentCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Create Accounting Event for Special Case
     */
    private ClaimAccountingEvent createSpecialCaseAccountingEvent(
            GeneralSpecialCaseResponse specialCaseResponse, 
            String createdBy) {
        
        LocalDateTime now = LocalDateTime.now();
        String specialCaseReference = "SPC-" + specialCaseResponse.getId();

        MemberDetail memberDetail = memberDetailRepository.findByNppfNumber(
                specialCaseResponse.getNppfNumber())
                .orElseThrow(() -> new RuntimeException(
                        "Member details not found for NPPF number: " + 
                        specialCaseResponse.getNppfNumber()));

        String caseType = specialCaseResponse.getSpecialCaseDetail() != null 
                ? specialCaseResponse.getSpecialCaseDetail().getCaseType() 
                : "UNKNOWN";

        return ClaimAccountingEvent.builder()
                .eventType(EVENT_TYPE_SPECIAL_CASE)
                .nppfNumber(specialCaseResponse.getNppfNumber())
                .identityNumber(memberDetail.getIdentityNumber())
                .memberName(buildMemberName(memberDetail))
                .agencyCategoryId(specialCaseResponse.getMemberCategoryId())
                .agencyCode(specialCaseResponse.getAgencyCode())
                .agencyName(specialCaseResponse.getAgencyCode())
                .claimTypeId(specialCaseResponse.getClaimTypeId())
                .claimApplicationNumber(specialCaseReference)
                .monthName(now.getMonth().name())
                .year(String.valueOf(now.getYear()))
                .accountingYear(String.valueOf(now.getYear()))
                .status(STATUS_PENDING)
                .narration("Special Case (" + caseType + "): " + specialCaseReference + " — " + 
                          specialCaseResponse.getClaimTypeName())
                .createdBy(createdBy)
                .createdAt(now)
                .build();
    }

    private String buildMemberName(MemberDetail memberDetail) {
        StringBuilder nameBuilder = new StringBuilder();
        if (memberDetail.getFirstName() != null) {
            nameBuilder.append(memberDetail.getFirstName());
        }
        if (memberDetail.getMiddleName() != null) {
            if (nameBuilder.length() > 0) {
                nameBuilder.append(" ");
            }
            nameBuilder.append(memberDetail.getMiddleName());
        }
        if (memberDetail.getLastName() != null) {
            if (nameBuilder.length() > 0) {
                nameBuilder.append(" ");
            }
            nameBuilder.append(memberDetail.getLastName());
        }
        return nameBuilder.toString();
    }

    /**
     * Build response DTO
     */
    private AccountingEventResponseDto buildResponse(
            ClaimAccountingEvent event, 
            List<ClaimLedgerEntry> entries) {
        
        List<String> mainAccountCodes = entries.stream()
                .map(ClaimLedgerEntry::getMainAccountCode)
                .distinct()
                .collect(Collectors.toList());

        List<String> subAccountCodes = entries.stream()
                .map(ClaimLedgerEntry::getSubAccountCode)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> mainAccountNames = new HashMap<>();
        for (String code : mainAccountCodes) {
            try {
                coaMainAccountRepository.findByAccountCode(code)
                        .ifPresent(acc -> mainAccountNames.put(code, acc.getAccountName()));
            } catch (Exception e) {
                log.debug("Error fetching main account name for code: {}", code);
            }
        }

        Map<String, String> subAccountNames = new HashMap<>();
        for (String code : subAccountCodes) {
            try {
                coaSubAccountRepository.findBySubAccountCode(code)
                        .ifPresent(acc -> subAccountNames.put(code, acc.getSubAccountName()));
            } catch (Exception e) {
                log.debug("Error fetching sub account name for code: {}", code);
            }
        }

        List<LedgerEntryResponseDto> entryDtos = entries.stream()
                .map(entry -> LedgerEntryResponseDto.builder()
                        .id(entry.getId())
                        .seqNo(entry.getSeqNo())
                        .mainAccountCode(entry.getMainAccountCode())
                        .mainAccountName(mainAccountNames.get(entry.getMainAccountCode()))
                        .subAccountCode(entry.getSubAccountCode())
                        .subAccountName(subAccountNames.get(entry.getSubAccountCode()))
                        .drcr(entry.getDrcr())
                        .amount(entry.getAmount())
                        .entryRole(entry.getEntryRole())
                        .componentCode(entry.getComponentCode())
                        .narration(entry.getNarration())
                        .createdBy(entry.getCreatedBy())
                        .createdAt(entry.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return AccountingEventResponseDto.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .claimApplicationNumber(event.getClaimApplicationNumber())
                .nppfNumber(event.getNppfNumber())
                .identityNumber(event.getIdentityNumber())
                .memberName(event.getMemberName())
                .agencyCategoryId(event.getAgencyCategoryId())
                .agencyCode(event.getAgencyCode())
                .agencyName(event.getAgencyName())
                .status(event.getStatus())
                .narration(event.getNarration())
                .postedBy(event.getPostedBy())
                .postedAt(event.getPostedAt())
                .createdBy(event.getCreatedBy())
                .createdAt(event.getCreatedAt())
                .updatedBy(event.getUpdatedBy())
                .updatedAt(event.getUpdatedAt())
                .ledgerEntries(entryDtos)
                .build();
    }

    @Override
    public AccountingEventResponseDto getAccountingEventBySpecialCaseId(Long specialCaseId) {
        ClaimAccountingEvent event = accountingEventRepository.findByClaimDetail_Id(specialCaseId)
                .orElseThrow(() -> new RuntimeException(
                        "Accounting event not found for special case: " + specialCaseId));

        List<ClaimLedgerEntry> entries = ledgerEntryRepository
                .findByAccountingEventIdOrderBySeqNoAsc(event.getId());

        return buildResponse(event, entries);
    }

    @Override
    public boolean hasLedgerEntries(Long specialCaseId) {
        return accountingEventRepository.existsByClaimDetailId(specialCaseId);
    }
}