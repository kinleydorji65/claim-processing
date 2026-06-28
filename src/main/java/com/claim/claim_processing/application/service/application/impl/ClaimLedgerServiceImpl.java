package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimRuleEvaluationListDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.service.application.ClaimLedgerService;
import com.claim.claim_processing.common.entities.claim.ClaimAccountingEvent;
import com.claim.claim_processing.common.entities.claim.ClaimLedgerEntry;
import com.claim.claim_processing.common.entities.common.CoaAccountMapping;
import com.claim.claim_processing.common.repository.claim.ClaimAccountingEventRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLedgerEntryRepository;
import com.claim.claim_processing.common.repository.common.CoaAccountMappingRepository;
import com.claim.claim_processing.common.repository.common.CoaMainAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaSubAccountRepository;

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
public class ClaimLedgerServiceImpl implements ClaimLedgerService {

    private final ClaimAccountingEventRepository accountingEventRepository;
    private final ClaimLedgerEntryRepository ledgerEntryRepository;
    private final CoaAccountMappingRepository coaAccountMappingRepository;
    private final CoaMainAccountRepository coaMainAccountRepository;
    private final CoaSubAccountRepository coaSubAccountRepository;

    private static final String EVENT_TYPE_CLAIM = "CLAIM";
    private static final String EVENT_TYPE_REFUND = "REFUND";
    private static final String EVENT_TYPE_DEDUCTION = "DEDUCTION";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_POSTED = "POSTED";
    private static final String STATUS_REVERSED = "REVERSED";

    private static final Map<String, String> TRAN_CODE_MAP = Map.of(
            "01", "RPFC",
            "03", "RPFA",
            "04", "RPFP"
    );

    @Override
    @Transactional
    public AccountingEventResponseDto createLedgerEntries(GeneralClaimDetailResponse claimResponse, String createdBy) {
        log.info("========== START: createLedgerEntries ==========");
        log.info("Claim ID: {}, Status: {}, Agency: {}",
                claimResponse.getId(),
                claimResponse.getStatusName(),
                claimResponse.getMemberCategoryId());

        String claimReference = "CLM-" + claimResponse.getId();

        // 1. Validate claim is approved
        if (!"Approved".equalsIgnoreCase(claimResponse.getStatusName())) {
            throw new RuntimeException("Claim must be Approved to create ledger entries. Current status: " +
                    claimResponse.getStatusName());
        }

        // 2. Check if entries already exist
        if (hasLedgerEntries(claimResponse.getId())) {
            throw new RuntimeException("Ledger entries already exist for claim: " + claimResponse.getId());
        }

        // 3. Determine TRAN_CODE based on Agency Category
        String agencyCategoryId = claimResponse.getMemberCategoryId();
        String tranCode = TRAN_CODE_MAP.getOrDefault(agencyCategoryId, "RPFC");
        log.info("Agency Category: {}, TRAN_CODE: {}", agencyCategoryId, tranCode);

        // 4. Build grouped component amounts (PF and Pension)
        Map<String, BigDecimal> groupedComponentAmounts = buildGroupedComponentAmounts(claimResponse);
        log.info("Grouped Components: {}", groupedComponentAmounts);

        // 5. Build deduction amounts
        Map<String, BigDecimal> deductionAmounts = buildDeductionAmounts(claimResponse);
        log.info("Deduction Amounts: {}", deductionAmounts);

        // 6. Build forfeited/lapse amounts
        Map<String, BigDecimal> forfeitedAmounts = buildForfeitedAmounts(claimResponse);
        log.info("Forfeited Amounts: {}", forfeitedAmounts);

        // 7. Calculate total eligible amount
        BigDecimal totalEligible = calculateTotalAmount(groupedComponentAmounts);
        log.info("Total Eligible Amount: {}", totalEligible);

        // 8. Calculate total deductions
        BigDecimal totalDeductions = calculateTotalAmount(deductionAmounts);
        log.info("Total Deductions: {}", totalDeductions);

        // 9. Calculate total forfeited/lapse
        BigDecimal totalForfeited = calculateTotalAmount(forfeitedAmounts);
        log.info("Total Forfeited: {}", totalForfeited);

        // 10. Get final payable amount (NET amount after deductions)
        BigDecimal finalPayable = claimResponse.getCalculationSummary() != null
                ? claimResponse.getCalculationSummary().getFinalPayableAmount()
                : BigDecimal.ZERO;
        log.info("Final Payable (Net) Amount: {}", finalPayable);

        // 11. Calculate Lapse amount = Total Eligible - Total Deductions - Net Payable - Total Forfeited
        BigDecimal lapseAmount = totalEligible.subtract(totalDeductions).subtract(finalPayable).subtract(totalForfeited);
        log.info("Lapse Amount: {}", lapseAmount);

        // 12. Create and SAVE Accounting Event FIRST
        ClaimAccountingEvent event = createAccountingEvent(claimResponse, tranCode, createdBy);
        event = accountingEventRepository.save(event);
        log.info("Accounting Event created with ID: {}", event.getId());

        // 13. Get COA Mappings for REFUND and DEDUCTION
        List<CoaAccountMapping> refundMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_REFUND, agencyCategoryId);
        
        List<CoaAccountMapping> deductionMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_DEDUCTION, agencyCategoryId);
        
        log.info("REFUND Mappings found: {}, DEDUCTION Mappings found: {}", 
                refundMappings.size(), deductionMappings.size());

        // Log mapping details for debugging
        for (CoaAccountMapping mapping : deductionMappings) {
            log.info("DEDUCTION Mapping: component={}, mainAccount={}, subAccount={}, drcr={}", 
                    mapping.getComponentCode(), mapping.getMainAccountCode(), 
                    mapping.getSubAccountCode(), mapping.getDrcr());
        }

        // 14. Generate Ledger Entries
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // 14a. Process REFUND mappings (DEBIT - PF_REFUND & PENSION_REFUND)
        for (CoaAccountMapping mapping : refundMappings) {
            String componentCode = mapping.getComponentCode();

            // Skip BANK and LAPSE - handled separately
            if ("BANK".equals(componentCode) || "LAPSE".equals(componentCode)) {
                continue;
            }

            BigDecimal amount = groupedComponentAmounts.getOrDefault(componentCode, BigDecimal.ZERO);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("Skipping zero amount for component: {}", componentCode);
                continue;
            }

            seqNo++;
            ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                    .accountingEventId(event.getId())
                    .seqNo(seqNo)
                    .mainAccountCode(mapping.getMainAccountCode())
                    .subAccountCode(mapping.getSubAccountCode())
                    .drcr("D") // DEBIT for refunds
                    .amount(amount)
                    .entryRole(mapping.getEntryRole())
                    .componentCode(componentCode)
                    .narration("Claim: " + claimReference + " — " + componentCode + " posting")
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entry);
            log.info("DEBIT Entry: SEQ={}, Component={}, Amount={}, Account={}/{}",
                    seqNo, componentCode, amount, mapping.getMainAccountCode(), mapping.getSubAccountCode());
        }

        // 14b. Process DEDUCTION mappings (CREDIT - LOAN, RENTAL, etc.)
        Map<String, BigDecimal> matchedDeductions = new HashMap<>();
        
        for (CoaAccountMapping mapping : deductionMappings) {
            String componentCode = mapping.getComponentCode();
            
            // Skip LAPSE - handled separately
            if ("LAPSE".equals(componentCode)) {
                continue;
            }
            
            // Try to find matching deduction amount by various methods
            BigDecimal amount = findMatchingAmount(componentCode, deductionAmounts);
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("No matching deduction for component: {}", componentCode);
                continue;
            }

            matchedDeductions.put(componentCode, amount);
            
            seqNo++;
            ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                    .accountingEventId(event.getId())
                    .seqNo(seqNo)
                    .mainAccountCode(mapping.getMainAccountCode())
                    .subAccountCode(mapping.getSubAccountCode())
                    .drcr("C") // CREDIT for deductions
                    .amount(amount)
                    .entryRole(mapping.getEntryRole())
                    .componentCode(componentCode)
                    .narration("Claim: " + claimReference + " — " + componentCode + " deduction")
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entry);
            log.info("CREDIT Entry: SEQ={}, Component={}, Amount={}, Account={}/{}",
                    seqNo, componentCode, amount, mapping.getMainAccountCode(), mapping.getSubAccountCode());
        }

        // 14c. Process any remaining deductions that weren't matched to COA mappings
        for (Map.Entry<String, BigDecimal> entry : deductionAmounts.entrySet()) {
            String deductionCode = entry.getKey();
            BigDecimal amount = entry.getValue();
            
            // Skip if already matched
            if (matchedDeductions.containsKey(deductionCode) || 
                matchedDeductions.containsValue(amount)) {
                continue;
            }
            
            // Find a default deduction mapping
            CoaAccountMapping defaultMapping = deductionMappings.stream()
                    .filter(m -> !"LAPSE".equals(m.getComponentCode()))
                    .findFirst()
                    .orElse(null);
            
            if (defaultMapping != null) {
                seqNo++;
                ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(defaultMapping.getMainAccountCode())
                        .subAccountCode(defaultMapping.getSubAccountCode())
                        .drcr("C") // CREDIT for deductions
                        .amount(amount)
                        .entryRole(defaultMapping.getEntryRole())
                        .componentCode(deductionCode)
                        .narration("Claim: " + claimReference + " — " + deductionCode + " deduction")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(ledgerEntry);
                log.info("CREDIT Entry (Default): SEQ={}, Component={}, Amount={}, Account={}/{}",
                        seqNo, deductionCode, amount, 
                        defaultMapping.getMainAccountCode(), defaultMapping.getSubAccountCode());
            }
        }

        // 14d. Process FORFEITED components (CREDIT)
        for (Map.Entry<String, BigDecimal> entry : forfeitedAmounts.entrySet()) {
            String componentCode = entry.getKey();
            BigDecimal amount = entry.getValue();
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            // Try to find mapping for forfeited component in deduction mappings
            CoaAccountMapping forfeitedMapping = findMappingByComponent(deductionMappings, componentCode);
            if (forfeitedMapping == null) {
                // Try to find in refund mappings
                forfeitedMapping = findMappingByComponent(refundMappings, componentCode);
            }
            
            if (forfeitedMapping != null) {
                seqNo++;
                ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(forfeitedMapping.getMainAccountCode())
                        .subAccountCode(forfeitedMapping.getSubAccountCode())
                        .drcr("C") // CREDIT for forfeited
                        .amount(amount)
                        .entryRole(forfeitedMapping.getEntryRole())
                        .componentCode(componentCode)
                        .narration("Claim: " + claimReference + " — " + componentCode + " forfeited")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(ledgerEntry);
                log.info("CREDIT Entry: SEQ={}, Component={}, Amount={}, Account={}/{}",
                        seqNo, componentCode, amount, 
                        forfeitedMapping.getMainAccountCode(), forfeitedMapping.getSubAccountCode());
            } else {
                // If no mapping found, use LAPSE account as fallback
                String lapseAccountCode = getLapseAccountCode(agencyCategoryId);
                String lapseSubAccountCode = lapseAccountCode + "02";
                
                seqNo++;
                ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(lapseAccountCode)
                        .subAccountCode(lapseSubAccountCode)
                        .drcr("C")
                        .amount(amount)
                        .entryRole("FORFEITED")
                        .componentCode(componentCode)
                        .narration("Claim: " + claimReference + " — " + componentCode + " forfeited (LAPSE)")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(ledgerEntry);
                log.info("CREDIT Entry (LAPSE Fallback): SEQ={}, Component={}, Amount={}, Account={}/{}",
                        seqNo, componentCode, amount, lapseAccountCode, lapseSubAccountCode);
            }
        }

        // 14e. BANK Entry (CREDIT) - Net payment to member
        if (finalPayable.compareTo(BigDecimal.ZERO) > 0) {
            CoaAccountMapping bankMapping = findMappingByComponent(refundMappings, "BANK");
            if (bankMapping != null) {
                seqNo++;
                ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(bankMapping.getMainAccountCode())
                        .subAccountCode(bankMapping.getSubAccountCode())
                        .drcr("C") // CREDIT for bank payment
                        .amount(finalPayable)
                        .entryRole(bankMapping.getEntryRole())
                        .componentCode("BANK")
                        .narration("Claim: " + claimReference + " — BANK (Net Payment)")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(entry);
                log.info("CREDIT Entry: SEQ={}, BANK, Amount={}, Account={}/{}",
                        seqNo, finalPayable, bankMapping.getMainAccountCode(), bankMapping.getSubAccountCode());
            } else {
                log.warn("BANK mapping not found for agency: {}", agencyCategoryId);
            }
        }

        // 14f. LAPSE Entry (CREDIT) - Difference if positive (not already covered by forfeited)
        if (lapseAmount.compareTo(BigDecimal.ZERO) > 0) {
            CoaAccountMapping lapseMapping = findLapseMapping(deductionMappings, agencyCategoryId);
            if (lapseMapping != null) {
                seqNo++;
                ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(lapseMapping.getMainAccountCode())
                        .subAccountCode(lapseMapping.getSubAccountCode())
                        .drcr("C") // CREDIT for lapse
                        .amount(lapseAmount)
                        .entryRole(lapseMapping.getEntryRole())
                        .componentCode("LAPSE")
                        .narration("Claim: " + claimReference + " — LAPSE (Difference)")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(entry);
                log.info("CREDIT Entry: SEQ={}, LAPSE, Amount={}, Account={}/{}",
                        seqNo, lapseAmount, lapseMapping.getMainAccountCode(), lapseMapping.getSubAccountCode());
            } else {
                // Fallback if no LAPSE mapping found in COA
                String lapseAccountCode = getLapseAccountCode(agencyCategoryId);
                String lapseSubAccountCode = lapseAccountCode + "02";
                
                seqNo++;
                ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(lapseAccountCode)
                        .subAccountCode(lapseSubAccountCode)
                        .drcr("C")
                        .amount(lapseAmount)
                        .entryRole("LAPSE")
                        .componentCode("LAPSE")
                        .narration("Claim: " + claimReference + " — LAPSE (Difference) [FALLBACK]")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(entry);
                log.info("CREDIT Entry (FALLBACK): SEQ={}, LAPSE, Amount={}, Account={}/{}",
                        seqNo, lapseAmount, lapseAccountCode, lapseSubAccountCode);
            }
        }

        log.info("Total ledger entries to save: {}", ledgerEntries.size());

        // 15. Save all ledger entries
        List<ClaimLedgerEntry> savedEntries = ledgerEntryRepository.saveAll(ledgerEntries);
        log.info("{} ledger entries saved for claim: {}", savedEntries.size(), claimReference);

        // 16. Calculate totals
        BigDecimal totalDr = calculateTotal(savedEntries, "D");
        BigDecimal totalCr = calculateTotal(savedEntries, "C");
        log.info("Total DR: {}, Total CR: {}", totalDr, totalCr);

        // 17. Validate balance
        if (totalDr.compareTo(totalCr) != 0) {
            log.error("LEDGER NOT BALANCED! DR: {}, CR: {}, Difference: {}", 
                    totalDr, totalCr, totalDr.subtract(totalCr));
            throw new RuntimeException(
                    "Ledger entries do not balance! Total DR: " + totalDr +
                            ", Total CR: " + totalCr +
                            ", Difference: " + totalDr.subtract(totalCr));
        }

        // 18. Update event with totals and status
        event.setTotalDr(totalDr);
        event.setTotalCr(totalCr);
        event.setStatus(STATUS_POSTED);
        event.setPostedBy(createdBy);
        event.setPostedAt(LocalDateTime.now());
        event.setUpdatedBy(createdBy);
        event.setUpdatedAt(LocalDateTime.now());
        ClaimAccountingEvent updatedEvent = accountingEventRepository.save(event);
        log.info("Accounting Event updated with status POSTED");

        log.info("========== END: createLedgerEntries SUCCESS ==========");
        return buildResponse(updatedEvent, savedEntries);
    }

    /**
     * Find matching amount for component code from deduction amounts
     */
    private BigDecimal findMatchingAmount(String componentCode, Map<String, BigDecimal> deductionAmounts) {
        // Try exact match
        BigDecimal amount = deductionAmounts.get(componentCode);
        if (amount != null) {
            return amount;
        }
        
        // Try case-insensitive match
        for (Map.Entry<String, BigDecimal> entry : deductionAmounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(componentCode)) {
                return entry.getValue();
            }
        }
        
        // Try partial match (component code contains deduction key or vice versa)
        for (Map.Entry<String, BigDecimal> entry : deductionAmounts.entrySet()) {
            String key = entry.getKey().toUpperCase();
            String compCode = componentCode.toUpperCase();
            if (key.contains(compCode) || compCode.contains(key)) {
                log.debug("Found partial match: {} -> {}", entry.getKey(), componentCode);
                return entry.getValue();
            }
        }
        
        return null;
    }

    /**
     * Build grouped component amounts by PF and Pension
     */
    private Map<String, BigDecimal> buildGroupedComponentAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> groupedMap = new HashMap<>();
        BigDecimal pfTotal = BigDecimal.ZERO;
        BigDecimal pensionTotal = BigDecimal.ZERO;

        ClaimCalculationSummaryResponseDto summary = claimResponse.getCalculationSummary();

        if (summary == null || summary.getRuleEvaluations() == null) {
            log.warn("No calculation summary or rule evaluations found");
            return groupedMap;
        }

        for (ClaimRuleEvaluationListDto ruleEvaluation : summary.getRuleEvaluations()) {
            if (ruleEvaluation.getComponents() == null) {
                continue;
            }

            for (ClaimCalculationComponentDto component : ruleEvaluation.getComponents()) {
                String code = component.getComponentCode();
                BigDecimal amount = component.getAmount() != null ? component.getAmount() : BigDecimal.ZERO;

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                if (code.startsWith("PF_")) {
                    pfTotal = pfTotal.add(amount);
                    log.debug("PF Component: {} = {}", code, amount);
                } else if (code.startsWith("P_") || code.startsWith("PC_")) {
                    pensionTotal = pensionTotal.add(amount);
                    log.debug("Pension Component: {} = {}", code, amount);
                }
            }
        }

        if (pfTotal.compareTo(BigDecimal.ZERO) > 0) {
            groupedMap.put("PF_REFUND", pfTotal);
        }
        if (pensionTotal.compareTo(BigDecimal.ZERO) > 0) {
            groupedMap.put("PENSION_REFUND", pensionTotal);
        }

        log.info("Grouped Components: PF={}, Pension={}", pfTotal, pensionTotal);
        return groupedMap;
    }

    /**
     * Build deduction amounts from claim response
     */
    private Map<String, BigDecimal> buildDeductionAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> deductionMap = new HashMap<>();

        ClaimDeductionResponseDto deductionDetail = claimResponse.getDeductionDetail();
        
        if (deductionDetail == null) {
            log.warn("No deduction detail found in claim response");
            return deductionMap;
        }

        List<ClaimDeductionItemResponseDto> deductionItems = deductionDetail.getDeductionItems();
        
        if (deductionItems == null || deductionItems.isEmpty()) {
            BigDecimal deductedAmount = deductionDetail.getDeductedAmount() != null 
                    ? deductionDetail.getDeductedAmount() 
                    : BigDecimal.ZERO;
            
            if (deductedAmount.compareTo(BigDecimal.ZERO) > 0) {
                deductionMap.put("OTHER_DEDUCTION", deductedAmount);
                log.debug("Using OTHER_DEDUCTION for amount: {}", deductedAmount);
            }
            return deductionMap;
        }

        for (ClaimDeductionItemResponseDto item : deductionItems) {
            BigDecimal deductedAmount = item.getDeductedAmount() != null 
                    ? item.getDeductedAmount() 
                    : BigDecimal.ZERO;

            if (deductedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            String componentCode = mapDeductionItemToComponentCode(item);
            
            if (componentCode != null) {
                BigDecimal existingAmount = deductionMap.getOrDefault(componentCode, BigDecimal.ZERO);
                deductionMap.put(componentCode, existingAmount.add(deductedAmount));
                log.debug("Deduction Item: {} -> Component: {}, Amount: {}", 
                        item.getReferenceName(), componentCode, deductedAmount);
            } else {
                // Use reference name as component code
                String refName = item.getReferenceName();
                if (refName != null && !refName.isEmpty()) {
                    String mappedCode = refName.toUpperCase().replace(" ", "_");
                    BigDecimal existingAmount = deductionMap.getOrDefault(mappedCode, BigDecimal.ZERO);
                    deductionMap.put(mappedCode, existingAmount.add(deductedAmount));
                } else {
                    BigDecimal existingAmount = deductionMap.getOrDefault("OTHER_DEDUCTION", BigDecimal.ZERO);
                    deductionMap.put("OTHER_DEDUCTION", existingAmount.add(deductedAmount));
                }
            }
        }

        log.info("Total deduction map: {}", deductionMap);
        return deductionMap;
    }

    /**
     * Build forfeited amounts from claim response
     */
    private Map<String, BigDecimal> buildForfeitedAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> forfeitedMap = new HashMap<>();

        List<ClaimForfeitedComponentResponseDto> forfeitedComponents = claimResponse.getForfeitedComponents();
        
        if (forfeitedComponents == null || forfeitedComponents.isEmpty()) {
            log.debug("No forfeited components found");
            return forfeitedMap;
        }

        for (ClaimForfeitedComponentResponseDto forfeited : forfeitedComponents) {
            String componentCode = forfeited.getComponentCode();
            BigDecimal amount = forfeited.getAmount() != null ? forfeited.getAmount() : BigDecimal.ZERO;
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            String mappedCode = mapForfeitedComponentCode(componentCode);
            if (mappedCode != null) {
                BigDecimal existingAmount = forfeitedMap.getOrDefault(mappedCode, BigDecimal.ZERO);
                forfeitedMap.put(mappedCode, existingAmount.add(amount));
                log.debug("Forfeited Component: {} -> {}, Amount: {}", componentCode, mappedCode, amount);
            } else {
                forfeitedMap.put(componentCode, amount);
                log.debug("Forfeited Component: {}, Amount: {}", componentCode, amount);
            }
        }

        log.info("Total forfeited map: {}", forfeitedMap);
        return forfeitedMap;
    }

    /**
     * Map forfeited component code to standard code
     */
    private String mapForfeitedComponentCode(String componentCode) {
        if (componentCode == null) return null;
        
        Map<String, String> forfeitedMapping = new HashMap<>();
        forfeitedMapping.put("PF_MC", "PF_FORFEITED");
        forfeitedMapping.put("PF_EC", "PF_FORFEITED");
        forfeitedMapping.put("PF_IMC", "PF_FORFEITED");
        forfeitedMapping.put("PF_IEC", "PF_FORFEITED");
        forfeitedMapping.put("P_MC", "PENSION_FORFEITED");
        forfeitedMapping.put("P_EC", "PENSION_FORFEITED");
        forfeitedMapping.put("P_IMC", "PENSION_FORFEITED");
        forfeitedMapping.put("P_IEC", "PENSION_FORFEITED");
        
        return forfeitedMapping.getOrDefault(componentCode, componentCode);
    }

    /**
     * Map deduction item to component code
     */
    private String mapDeductionItemToComponentCode(ClaimDeductionItemResponseDto item) {
        String deductionCategory = item.getDeductionCategory();
        String referenceName = item.getReferenceName() != null ? item.getReferenceName().toUpperCase() : "";
        
        // Map by category
        String componentCode = mapDeductionCategoryToComponentCode(deductionCategory);
        if (componentCode != null) {
            return componentCode;
        }
        
        // Map by reference name
        componentCode = mapDeductionReferenceNameToComponentCode(referenceName);
        if (componentCode != null) {
            return componentCode;
        }
        
        // Return reference name if not empty
        if (referenceName != null && !referenceName.isEmpty()) {
            return referenceName.replace(" ", "_");
        }
        
        return null;
    }

    /**
     * Map deduction category to component code
     */
    private String mapDeductionCategoryToComponentCode(String category) {
        if (category == null) return null;
        
        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("LOAN", "LOAN");
        categoryMapping.put("PF_LOAN", "LOAN");
        categoryMapping.put("PENSION_LOAN", "PENSION_LOAN");
        categoryMapping.put("RENTAL", "RENTAL");
        categoryMapping.put("RESIDENTIAL", "RENTAL");
        categoryMapping.put("ADVANCE", "ADVANCE");
        categoryMapping.put("TAX", "TAX");
        categoryMapping.put("RECOVERY", "RECOVERY");
        
        return categoryMapping.get(category.toUpperCase());
    }

    /**
     * Map deduction reference name to component code
     */
    private String mapDeductionReferenceNameToComponentCode(String referenceName) {
        if (referenceName == null) return null;
        
        Map<String, String> referenceMapping = new HashMap<>();
        referenceMapping.put("LOAN", "LOAN");
        referenceMapping.put("PF LOAN", "LOAN");
        referenceMapping.put("PENSION LOAN", "PENSION_LOAN");
        referenceMapping.put("RENTAL", "RENTAL");
        referenceMapping.put("RESIDENTIAL", "RENTAL");
        referenceMapping.put("ADVANCE", "ADVANCE");
        referenceMapping.put("TAX", "TAX");
        referenceMapping.put("RECOVERY", "RECOVERY");
        
        return referenceMapping.get(referenceName);
    }

    /**
     * Find LAPSE mapping
     */
    private CoaAccountMapping findLapseMapping(List<CoaAccountMapping> deductionMappings, String agencyCategoryId) {
        CoaAccountMapping lapseMapping = findMappingByComponent(deductionMappings, "LAPSE");
        
        if (lapseMapping == null) {
            List<CoaAccountMapping> refundMappings = coaAccountMappingRepository
                    .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                            EVENT_TYPE_REFUND, agencyCategoryId);
            lapseMapping = findMappingByComponent(refundMappings, "LAPSE");
        }
        
        return lapseMapping;
    }

    /**
     * Calculate total amount from a map
     */
    private BigDecimal calculateTotalAmount(Map<String, BigDecimal> amountMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : amountMap.values()) {
            total = total.add(amount);
        }
        return total;
    }

    /**
     * Get Lapse Fund account code based on agency category (Fallback)
     */
    private String getLapseAccountCode(String agencyCategoryId) {
        return switch (agencyCategoryId) {
            case "01" -> "39000100";
            case "03" -> "39000200";
            case "04" -> "39000300";
            default -> "39000100";
        };
    }

    /**
     * Find mapping by component code
     */
    private CoaAccountMapping findMappingByComponent(List<CoaAccountMapping> mappings, String componentCode) {
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }
        return mappings.stream()
                .filter(m -> componentCode.equals(m.getComponentCode()))
                .findFirst()
                .orElse(null);
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
     * Create Accounting Event
     */
    private ClaimAccountingEvent createAccountingEvent(GeneralClaimDetailResponse claimResponse, String tranCode,
            String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        String claimReference = "CLM-" + claimResponse.getId();

        return ClaimAccountingEvent.builder()
                .eventType(EVENT_TYPE_CLAIM)
                .claimDetailId(claimResponse.getId())
                .nppfNumber(claimResponse.getNppfNumber())
                .identityNumber(null)
                .memberName(claimResponse.getMemberCode())
                .agencyCategoryId(claimResponse.getMemberCategoryId())
                .agencyCode(claimResponse.getAgencyCode())
                .agencyName(null)
                .claimTypeId(claimResponse.getClaimTypeId())
                .claimTypeName(claimResponse.getClaimTypeName())
                .claimApplicationNumber(claimReference)
                .monthName(now.getMonth().name())
                .year(String.valueOf(now.getYear()))
                .accountingYear(String.valueOf(now.getYear()))
                .tranCode(tranCode)
                .status(STATUS_PENDING)
                .totalDr(BigDecimal.ZERO)
                .totalCr(BigDecimal.ZERO)
                .narration("Claim: " + claimReference + " — " + claimResponse.getClaimTypeName())
                .createdBy(createdBy)
                .createdAt(now)
                .build();
    }

    /**
     * Build response DTO
     */
    private AccountingEventResponseDto buildResponse(ClaimAccountingEvent event, List<ClaimLedgerEntry> entries) {
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
            coaMainAccountRepository.findByAccountCode(code)
                    .ifPresent(acc -> mainAccountNames.put(code, acc.getAccountName()));
        }

        Map<String, String> subAccountNames = new HashMap<>();
        for (String code : subAccountCodes) {
            coaSubAccountRepository.findBySubAccountCode(code)
                    .ifPresent(acc -> subAccountNames.put(code, acc.getSubAccountName()));
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
                .claimDetailId(event.getClaimDetailId())
                .claimApplicationNumber(event.getClaimApplicationNumber())
                .nppfNumber(event.getNppfNumber())
                .identityNumber(event.getIdentityNumber())
                .memberName(event.getMemberName())
                .agencyCategoryId(event.getAgencyCategoryId())
                .agencyCode(event.getAgencyCode())
                .agencyName(event.getAgencyName())
                .tranCode(event.getTranCode())
                .status(event.getStatus())
                .totalDr(event.getTotalDr())
                .totalCr(event.getTotalCr())
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
    public AccountingEventResponseDto getAccountingEventByClaimId(Long claimId) {
        ClaimAccountingEvent event = accountingEventRepository.findByClaimDetailId(claimId)
                .orElseThrow(() -> new RuntimeException(
                        "Accounting event not found for claim: " + claimId));

        List<ClaimLedgerEntry> entries = ledgerEntryRepository
                .findByAccountingEventIdOrderBySeqNoAsc(event.getId());

        return buildResponse(event, entries);
    }

    @Override
    public List<LedgerEntryResponseDto> getLedgerEntriesByEventId(Long eventId) {
        List<ClaimLedgerEntry> entries = ledgerEntryRepository
                .findByAccountingEventIdOrderBySeqNoAsc(eventId);

        return entries.stream()
                .map(entry -> LedgerEntryResponseDto.builder()
                        .id(entry.getId())
                        .seqNo(entry.getSeqNo())
                        .mainAccountCode(entry.getMainAccountCode())
                        .subAccountCode(entry.getSubAccountCode())
                        .drcr(entry.getDrcr())
                        .amount(entry.getAmount())
                        .entryRole(entry.getEntryRole())
                        .componentCode(entry.getComponentCode())
                        .narration(entry.getNarration())
                        .createdBy(entry.getCreatedBy())
                        .createdAt(entry.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reverseLedgerEntries(Long claimId, String reversedBy, String reason) {
        log.info("Reversing ledger entries for claim: {}", claimId);

        ClaimAccountingEvent event = accountingEventRepository.findByClaimDetailId(claimId)
                .orElseThrow(() -> new RuntimeException(
                        "Accounting event not found for claim: " + claimId));

        if (STATUS_REVERSED.equals(event.getStatus())) {
            throw new RuntimeException("Accounting event already reversed");
        }

        ClaimAccountingEvent reversalEvent = ClaimAccountingEvent.builder()
                .eventType("REVERSAL")
                .claimDetailId(event.getClaimDetailId())
                .nppfNumber(event.getNppfNumber())
                .identityNumber(event.getIdentityNumber())
                .memberName(event.getMemberName())
                .agencyCategoryId(event.getAgencyCategoryId())
                .agencyCode(event.getAgencyCode())
                .agencyName(event.getAgencyName())
                .claimTypeId(event.getClaimTypeId())
                .claimTypeName(event.getClaimTypeName())
                .claimApplicationNumber(event.getClaimApplicationNumber())
                .monthName(event.getMonthName())
                .year(event.getYear())
                .accountingYear(event.getAccountingYear())
                .tranCode(event.getTranCode())
                .status(STATUS_POSTED)
                .totalDr(event.getTotalDr())
                .totalCr(event.getTotalCr())
                .reversalOfEventId(event.getId())
                .narration("Reversal of " + event.getClaimApplicationNumber() + " — " + reason)
                .postedBy(reversedBy)
                .postedAt(LocalDateTime.now())
                .createdBy(reversedBy)
                .createdAt(LocalDateTime.now())
                .build();

        reversalEvent = accountingEventRepository.save(reversalEvent);

        List<ClaimLedgerEntry> originalEntries = ledgerEntryRepository
                .findByAccountingEventIdOrderBySeqNoAsc(event.getId());

        int maxSeq = originalEntries.stream()
                .mapToInt(ClaimLedgerEntry::getSeqNo)
                .max()
                .orElse(0);

        List<ClaimLedgerEntry> reversalEntries = new ArrayList<>();
        for (ClaimLedgerEntry entry : originalEntries) {
            String reversedDrcr = "D".equals(entry.getDrcr()) ? "C" : "D";

            ClaimLedgerEntry reversalEntry = ClaimLedgerEntry.builder()
                    .accountingEventId(reversalEvent.getId())
                    .seqNo(entry.getSeqNo() + maxSeq + 1)
                    .mainAccountCode(entry.getMainAccountCode())
                    .subAccountCode(entry.getSubAccountCode())
                    .drcr(reversedDrcr)
                    .amount(entry.getAmount())
                    .entryRole(entry.getEntryRole())
                    .componentCode(entry.getComponentCode())
                    .narration("REVERSAL: " + reason + " — " + entry.getNarration())
                    .createdBy(reversedBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            reversalEntries.add(reversalEntry);
        }

        ledgerEntryRepository.saveAll(reversalEntries);

        event.setStatus(STATUS_REVERSED);
        event.setReversedBy(reversedBy);
        event.setReversedAt(LocalDateTime.now());
        event.setUpdatedBy(reversedBy);
        event.setUpdatedAt(LocalDateTime.now());
        accountingEventRepository.save(event);

        log.info("Ledger entries reversed for claim: {}", claimId);
    }

    @Override
    public boolean hasLedgerEntries(Long claimId) {
        return accountingEventRepository.existsByClaimDetailId(claimId);
    }
}