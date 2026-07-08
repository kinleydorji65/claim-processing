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
import com.claim.claim_processing.common.entities.common.CoaSubAccount;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.claim.ClaimAccountingEventRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLedgerEntryRepository;
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
public class ClaimLedgerServiceImpl implements ClaimLedgerService {

    private final ClaimAccountingEventRepository accountingEventRepository;
    private final ClaimLedgerEntryRepository ledgerEntryRepository;
    private final CoaAccountMappingRepository coaAccountMappingRepository;
    private final CoaMainAccountRepository coaMainAccountRepository;
    private final CoaSubAccountRepository coaSubAccountRepository;
    private final MemberDetailRepository memberDetailRepository;

    private static final String EVENT_TYPE_CLAIM = "CLAIM";
    private static final String EVENT_TYPE_REFUND = "REFUND";
    private static final String EVENT_TYPE_DEDUCTION = "DEDUCTION";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_POSTED = "POSTED";
    private static final String STATUS_REVERSED = "REVERSED";
    private static final String EVENT_TYPE_PARTIAL_WITHDRAWAL = "PARTIAL_WITHDRAWAL";

    private static final Map<String, String> TRAN_CODE_MAP = Map.of(
            "01", "RPFC",
            "03", "RPFA",
            "04", "RPFP");

    private static final Map<String, String> PARTIAL_TRAN_CODE_MAP = Map.of(
            "01", "PWPC",
            "03", "PWPA",
            "04", "PWPP");

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

    boolean isPartialWithdrawal = isPartialWithdrawalClaim(claimResponse);
    log.info("Is Partial Withdrawal: {}", isPartialWithdrawal);

    // 3. Determine TRAN_CODE based on Agency Category
    String agencyCategoryId = claimResponse.getMemberCategoryId();
    String tranCode = isPartialWithdrawal
            ? PARTIAL_TRAN_CODE_MAP.getOrDefault(agencyCategoryId, "PWP")
            : TRAN_CODE_MAP.getOrDefault(agencyCategoryId, "RPFC");
    log.info("Agency Category: {}, TRAN_CODE: {}, isPartialWithdrawal: {}",
            agencyCategoryId, tranCode, isPartialWithdrawal);

    // 4. Build detailed component amounts (NOT grouped!)
    Map<String, BigDecimal> componentAmounts = buildDetailedComponentAmounts(claimResponse);
    log.info("Detailed Components: {}", componentAmounts);

    // 5. Build deduction amounts
    Map<String, BigDecimal> deductionAmounts = buildDeductionAmounts(claimResponse);
    log.info("Deduction Amounts: {}", deductionAmounts);

    // 6. Build forfeited amounts (These ARE the lapse amounts)
    Map<String, BigDecimal> forfeitedAmounts = buildForfeitedAmounts(claimResponse);
    log.info("Forfeited Amounts: {}", forfeitedAmounts);

    // 7. Calculate total eligible amount
    BigDecimal totalEligible = calculateTotalAmount(componentAmounts);
    log.info("Total Eligible Amount: {}", totalEligible);

    // 8. Calculate total deductions
    BigDecimal totalDeductions = calculateTotalAmount(deductionAmounts);
    log.info("Total Deductions: {}", totalDeductions);

    // 9. Calculate total forfeited (this IS the lapse amount)
    BigDecimal totalForfeited = calculateTotalAmount(forfeitedAmounts);
    log.info("Total Forfeited: {}", totalForfeited);

    // 10. Get final payable amount (NET amount after deductions)
    BigDecimal finalPayable = claimResponse.getCalculationSummary() != null
            ? claimResponse.getCalculationSummary().getFinalPayableAmount()
            : BigDecimal.ZERO;
    log.info("Final Payable (Net) Amount: {}", finalPayable);

    // 11. LAPSE AMOUNT = FORFEITED AMOUNT (not a separate calculation)
    BigDecimal lapseAmount = BigDecimal.ZERO;
    if (!isPartialWithdrawal) {
        // Lapse amount is the total forfeited amount
        lapseAmount = totalForfeited;
        log.info("Lapse Amount (Forfeited): {}", lapseAmount);
        
        // Validation: Total Eligible should equal Deductions + Final Payable + Forfeited
        BigDecimal totalCredited = totalDeductions.add(finalPayable).add(totalForfeited);
        if (totalEligible.compareTo(totalCredited) != 0) {
            log.warn("Balance check: Total Eligible ({}) != Deductions + Final Payable + Forfeited ({})", 
                    totalEligible, totalCredited);
            log.warn("Difference: {}", totalEligible.subtract(totalCredited));
        }
    }

    // 12. Get COA Mappings
    String eventType = isPartialWithdrawal ? EVENT_TYPE_PARTIAL_WITHDRAWAL : EVENT_TYPE_REFUND;
    List<CoaAccountMapping> refundMappings = coaAccountMappingRepository
            .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                    eventType, agencyCategoryId);

    if (refundMappings.isEmpty() && isPartialWithdrawal) {
        log.warn("No PARTIAL_WITHDRAWAL mappings found, falling back to REFUND");
        refundMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_REFUND, agencyCategoryId);
    }

    List<CoaAccountMapping> deductionMappings = coaAccountMappingRepository
            .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                    EVENT_TYPE_DEDUCTION, agencyCategoryId);

    log.info("REFUND Mappings found: {}, DEDUCTION Mappings found: {}",
            refundMappings.size(), deductionMappings.size());

    // 13. DEBUG - Show what we have
    debugComponentAmounts(componentAmounts, refundMappings, deductionAmounts, finalPayable, lapseAmount, forfeitedAmounts);

    // 14. Create and SAVE Accounting Event
    ClaimAccountingEvent event = createAccountingEvent(claimResponse, tranCode, createdBy);
    event = accountingEventRepository.save(event);
    log.info("Accounting Event created with ID: {}", event.getId());

    // 15. Generate Ledger Entries
    List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
    int seqNo = 0;

    // 15a. Process REFUND mappings (DEBIT - All refund components)
    for (CoaAccountMapping mapping : refundMappings) {
        String componentCode = mapping.getComponentCode();

        // Skip BANK and LAPSE - handled separately
        if ("BANK".equals(componentCode) || "LAPSE".equals(componentCode)) {
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
                .drcr(mapping.getDrcr()) // DEBIT for refunds
                .amount(amount)
                .entryRole(mapping.getEntryRole())
                .componentCode(componentCode)
                .narration(subAccount.getSubAccountName())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
        ledgerEntries.add(entry);
        log.info("DEBIT Entry: SEQ={}, Component={}, Amount={}, Account={}/{}",
                seqNo, componentCode, amount, mapping.getMainAccountCode(), mapping.getSubAccountCode());
    }

    // 15b. Process DEDUCTION mappings (CREDIT)
    Map<String, BigDecimal> matchedDeductions = new HashMap<>();

    for (CoaAccountMapping mapping : deductionMappings) {
        String componentCode = mapping.getComponentCode();

        // Skip LAPSE - handled separately
        if ("LAPSE".equals(componentCode)) {
            continue;
        }

        BigDecimal amount = deductionAmounts.getOrDefault(componentCode, BigDecimal.ZERO);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("No matching deduction for component: {}", componentCode);
            continue;
        }

        matchedDeductions.put(componentCode, amount);
        CoaSubAccount subAccount = coaSubAccountRepository.findBySubAccountCode(mapping.getSubAccountCode())
                .orElseThrow(() -> new RuntimeException("SubAccount not found: " + mapping.getSubAccountCode()));
        seqNo++;
        ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                .accountingEventId(event.getId())
                .seqNo(seqNo)
                .mainAccountCode(mapping.getMainAccountCode())
                .subAccountCode(mapping.getSubAccountCode())
                .drcr(mapping.getDrcr()) // CREDIT for deductions
                .amount(amount)
                .entryRole(mapping.getEntryRole())
                .componentCode(componentCode)
                .narration(subAccount.getSubAccountName())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
        ledgerEntries.add(entry);
        log.info("CREDIT Entry: SEQ={}, Component={}, Amount={}, Account={}/{}",
                seqNo, componentCode, amount, mapping.getMainAccountCode(), mapping.getSubAccountCode());
    }

    // 15c. Process any remaining deductions
    for (Map.Entry<String, BigDecimal> entry : deductionAmounts.entrySet()) {
        String deductionCode = entry.getKey();
        BigDecimal amount = entry.getValue();

        if (matchedDeductions.containsKey(deductionCode)) {
            continue;
        }

        CoaAccountMapping defaultMapping = deductionMappings.stream()
                .filter(m -> !"LAPSE".equals(m.getComponentCode()))
                .findFirst()
                .orElse(null);

        if (defaultMapping != null) {
            CoaSubAccount subAccount = coaSubAccountRepository.findBySubAccountCode(defaultMapping.getSubAccountCode())
                .orElseThrow(() -> new RuntimeException("SubAccount not found: " + defaultMapping.getSubAccountCode()));
            seqNo++;
            ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                    .accountingEventId(event.getId())
                    .seqNo(seqNo)
                    .mainAccountCode(defaultMapping.getMainAccountCode())
                    .subAccountCode(defaultMapping.getSubAccountCode())
                    .drcr(defaultMapping.getDrcr())
                    .amount(amount)
                    .entryRole(defaultMapping.getEntryRole())
                    .componentCode(deductionCode)
                    .narration(subAccount.getSubAccountName())
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(ledgerEntry);
            log.info("CREDIT Entry (Default): SEQ={}, Component={}, Amount={}",
                    seqNo, deductionCode, amount);
        }
    }

    // 15d. Process FORFEITED components (These go to LAPSE fund)
    if (!isPartialWithdrawal) {
        for (Map.Entry<String, BigDecimal> entry : forfeitedAmounts.entrySet()) {
            String componentCode = entry.getKey();
            BigDecimal amount = entry.getValue();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Find LAPSE mapping
            CoaAccountMapping lapseMapping = findMappingByComponent(refundMappings, "LAPSE");

            if (lapseMapping != null) {
                CoaSubAccount subAccount = coaSubAccountRepository.findBySubAccountCode(lapseMapping.getSubAccountCode())
                .orElseThrow(() -> new RuntimeException("SubAccount not found: " + lapseMapping.getSubAccountCode()));
                seqNo++;
                ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(lapseMapping.getMainAccountCode())
                        .subAccountCode(lapseMapping.getSubAccountCode())
                        .drcr(lapseMapping.getDrcr()) // CREDIT for forfeited/lapse
                        .amount(amount)
                        .entryRole("LAPSE")
                        .componentCode(componentCode)
                        .narration(subAccount.getSubAccountName())
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(ledgerEntry);
                log.info("CREDIT Entry (LAPSE): SEQ={}, Component={}, Amount={}, Account={}/{}",
                        seqNo, componentCode, amount, 
                        lapseMapping.getMainAccountCode(), lapseMapping.getSubAccountCode());
            } else {
                log.warn("No LAPSE mapping found for forfeited component: {}", componentCode);
            }
        }
    }

    // 15e. BANK Entry (CREDIT) - Net payment to member
    if (finalPayable.compareTo(BigDecimal.ZERO) > 0) {
        CoaAccountMapping bankMapping = findMappingByComponent(refundMappings, "BANK");
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
                    .componentCode("BANK")
                    .narration(subAccount.getSubAccountName())
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entry);
            log.info("CREDIT Entry: SEQ={}, BANK, Amount={}", seqNo, finalPayable);
        }
    }

    // 15f. LAPSE Entry (CREDIT) - Only if there's a lapse amount not already covered by forfeited components
    // Skip this because forfeited components are already handled above
    // The lapse amount is the total forfeited, which is already posted as individual component credits

    log.info("Total ledger entries to save: {}", ledgerEntries.size());

    // 16. Save all ledger entries
    List<ClaimLedgerEntry> savedEntries = ledgerEntryRepository.saveAll(ledgerEntries);
    log.info("{} ledger entries saved for claim: {}", savedEntries.size(), claimReference);

    // 17. Calculate totals
    BigDecimal totalDr = calculateTotal(savedEntries, "D");
    BigDecimal totalCr = calculateTotal(savedEntries, "C");
    log.info("Total DR: {}, Total CR: {}", totalDr, totalCr);

    // 18. Validate balance
    if (totalDr.compareTo(totalCr) != 0) {
        log.error("LEDGER NOT BALANCED! DR: {}, CR: {}, Difference: {}",
                totalDr, totalCr, totalDr.subtract(totalCr));
        debugComponentAmounts(componentAmounts, refundMappings, deductionAmounts, finalPayable, lapseAmount, forfeitedAmounts);
        throw new RuntimeException(
                "Ledger entries do not balance! Total DR: " + totalDr +
                        ", Total CR: " + totalCr +
                        ", Difference: " + totalDr.subtract(totalCr));
    }

    // 19. Update event with totals and status
    event.setTotalDr(totalDr);
    event.setTotalCr(totalCr);
    event.setStatus(STATUS_POSTED);
    event.setPostedBy(createdBy);
    event.setPostedAt(LocalDateTime.now());
    event.setUpdatedBy(createdBy);
    event.setUpdatedAt(LocalDateTime.now());
    ClaimAccountingEvent updatedEvent = accountingEventRepository.save(event);

    log.info("========== END: createLedgerEntries SUCCESS ==========");
    return buildResponse(updatedEvent, savedEntries);
}

    private void debugComponentAmounts(Map<String, BigDecimal> componentAmounts, 
                                   List<CoaAccountMapping> refundMappings,
                                   Map<String, BigDecimal> deductionAmounts,
                                   BigDecimal finalPayable,
                                   BigDecimal lapseAmount,
                                   Map<String, BigDecimal> forfeitedAmounts) {
    System.out.println("========== DEBUG: COMPONENT AMOUNTS ==========");
    System.out.println("Component Amounts Map: " + componentAmounts);
    System.out.println("Total Component Amounts: " + calculateTotalAmount(componentAmounts));
    
    System.out.println("========== DEBUG: FORFEITED AMOUNTS (LAPSE) ==========");
    System.out.println("Forfeited Amounts Map: " + forfeitedAmounts);
    System.out.println("Total Forfeited: " + calculateTotalAmount(forfeitedAmounts));
    System.out.println("Lapse Amount: " + lapseAmount);
    
    System.out.println("========== DEBUG: REFUND MAPPINGS ==========");
    for (CoaAccountMapping mapping : refundMappings) {
        if (!"BANK".equals(mapping.getComponentCode()) && !"LAPSE".equals(mapping.getComponentCode())) {
            BigDecimal amount = componentAmounts.getOrDefault(mapping.getComponentCode(), BigDecimal.ZERO);
            System.out.println("Mapping: " + mapping.getComponentCode() + " -> Amount: " + amount);
        }
    }
    
    System.out.println("========== DEBUG: DEDUCTION AMOUNTS ==========");
    System.out.println("Deduction Amounts Map: " + deductionAmounts);
    System.out.println("Total Deductions: " + calculateTotalAmount(deductionAmounts));
    
    System.out.println("========== DEBUG: TOTALS ==========");
    System.out.println("Final Payable: " + finalPayable);
    
    // Calculate expected totals
    BigDecimal totalDebitExpected = calculateTotalAmount(componentAmounts);
    BigDecimal totalCreditExpected = calculateTotalAmount(deductionAmounts)
            .add(finalPayable)
            .add(calculateTotalAmount(forfeitedAmounts));
    
    System.out.println("Expected Total DR: " + totalDebitExpected);
    System.out.println("Expected Total CR: " + totalCreditExpected);
    System.out.println("Difference: " + totalDebitExpected.subtract(totalCreditExpected));
    System.out.println("==============================================");
}

    /**
     * Build DETAILED component amounts - EACH component gets its own entry
     * This is the key change - no more grouping!
     */
    private Map<String, BigDecimal> buildDetailedComponentAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> componentMap = new HashMap<>();

        ClaimCalculationSummaryResponseDto summary = claimResponse.getCalculationSummary();
        if (summary == null || summary.getRuleEvaluations() == null) {
            log.warn("No calculation summary or rule evaluations found");
            return componentMap;
        }

        boolean isPartialWithdrawal = isPartialWithdrawalClaim(claimResponse);

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

                if (isPartialWithdrawal) {
                    // For partial withdrawal, only PF components
                    if (code.startsWith("PF_")) {
                        componentMap.merge("PARTIAL_PF", amount, BigDecimal::add);
                        log.debug("Partial Withdrawal - PF Component: {} = {}", code, amount);
                    }
                } else {
                    // Map the component code to COA component code
                    String mappedCode = mapComponentToCoaCode(code);
                    if (mappedCode != null) {
                        componentMap.merge(mappedCode, amount, BigDecimal::add);
                        log.debug("Component: {} -> {}, Amount: {}", code, mappedCode, amount);
                    } else {
                        log.warn("No mapping found for component: {}", code);
                    }
                }
            }
        }

        log.info("Detailed Component Map: {}", componentMap);
        return componentMap;
    }

    /**
     * Map calculation component codes to COA component codes
     * This maps the raw calculation components to the COA mapping component codes
     */
    /**
 * Map calculation component codes to COA component codes
 * This handles various naming conventions from the calculation engine
 */
private String mapComponentToCoaCode(String componentCode) {
    if (componentCode == null) return null;
    
    String cleanCode = componentCode.toUpperCase().trim();
    System.out.println("Mapping component: " + cleanCode);
    
    // Clean up any remaining suffixes (just in case)
    cleanCode = cleanCode.replace("_CUMULATIVE", "")
                         .replace("_CUM", "")
                         .replace("_YEAR", "")
                         .replace("PENSION", "P");
    
    // Direct mapping - these match exactly what's in COA_ACCOUNT_MAPPING
    switch (cleanCode) {
        // PF Components
        case "PF_MC":
        case "PF_IMC":
        case "PF_EC":
        case "PF_IEC":
        case "PF_GC":
        case "PF_IGC":
        // Pension Components
        case "P_MC":
        case "P_IMC":
        case "P_EC":
        case "P_IEC":
        // Partial Withdrawal
        case "PARTIAL_PF":
            return cleanCode;
            
        // Handle PC_ prefix (if it ever comes)
        case "PC_MC": return "P_MC";
        case "PC_IMC": return "P_IMC";
        case "PC_EC": return "P_EC";
        case "PC_IEC": return "P_IEC";
            
        default:
            System.out.println("No mapping found for: " + componentCode);
            return null;
    }
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
            }
        }

        log.info("Total deduction map: {}", deductionMap);
        return deductionMap;
    }

    /**
     * Build forfeited amounts from claim response
     */
    /**
 * Build forfeited amounts from claim response
 */
private Map<String, BigDecimal> buildForfeitedAmounts(GeneralClaimDetailResponse claimResponse) {
    Map<String, BigDecimal> forfeitedMap = new HashMap<>();

    if (isPartialWithdrawalClaim(claimResponse)) {
        log.info("Partial withdrawal claim - no forfeited components");
        return forfeitedMap;
    }

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

        // IMPORTANT: Keep the original component code (with CUMULATIVE/YEAR if present)
        // This is needed to properly match with the component amounts
        String originalCode = componentCode;
        
        // For forfeited components, we need to map to the same code used in componentAmounts
        String mappedCode = mapForfeitedComponentCode(originalCode);
        if (mappedCode != null) {
            BigDecimal existingAmount = forfeitedMap.getOrDefault(mappedCode, BigDecimal.ZERO);
            forfeitedMap.put(mappedCode, existingAmount.add(amount));
            log.debug("Forfeited Component: {} -> {}, Amount: {}", componentCode, mappedCode, amount);
        }
    }

    log.info("Total forfeited map: {}", forfeitedMap);
    return forfeitedMap;
}

    /**
     * Map forfeited component code to standard code
     */
    /**
 * Map forfeited component code to standard code
 * Use the same mapping as regular components
 */
private String mapForfeitedComponentCode(String componentCode) {
    if (componentCode == null) return null;
    
    String cleanCode = componentCode.toUpperCase().trim();
    
    // Clean up suffixes (just like mapComponentToCoaCode)
    cleanCode = cleanCode.replace("_CUMULATIVE", "")
                         .replace("_CUM", "")
                         .replace("_YEAR", "")
                         .replace("PENSION", "P");
    
    // Direct mapping - these match exactly what's in COA_ACCOUNT_MAPPING
    switch (cleanCode) {
        case "PF_MC":
        case "PF_IMC":
        case "PF_EC":
        case "PF_IEC":
        case "PF_GC":
        case "PF_IGC":
        case "P_MC":
        case "P_IMC":
        case "P_EC":
        case "P_IEC":
            return cleanCode;  // Return the actual component code
            
        case "PC_MC": return "P_MC";
        case "PC_IMC": return "P_IMC";
        case "PC_EC": return "P_EC";
        case "PC_IEC": return "P_IEC";
            
        default:
            System.out.println("No forfeited mapping found for: " + componentCode);
            return null;
    }
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

        return null;
    }

    /**
     * Map deduction category to component code
     */
    private String mapDeductionCategoryToComponentCode(String category) {
        if (category == null) return null;

        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("LOAN", "LOAN_ADJUSTMENT");
        categoryMapping.put("PF_LOAN", "LOAN_ADJUSTMENT");
        categoryMapping.put("PENSION_LOAN", "LOAN_ADJUSTMENT");
        categoryMapping.put("RENTAL", "RENTAL_ADJUSTMENT");
        categoryMapping.put("RESIDENTIAL", "RENTAL_ADJUSTMENT");

        return categoryMapping.get(category.toUpperCase());
    }

    /**
     * Map deduction reference name to component code
     */
    private String mapDeductionReferenceNameToComponentCode(String referenceName) {
        if (referenceName == null) return null;

        Map<String, String> referenceMapping = new HashMap<>();
        referenceMapping.put("LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("PF LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("PENSION LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("RENTAL", "RENTAL_ADJUSTMENT");
        referenceMapping.put("RESIDENTIAL", "RENTAL_ADJUSTMENT");

        return referenceMapping.get(referenceName);
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

        boolean isPartialWithdrawal = isPartialWithdrawalClaim(claimResponse);
        String eventType = isPartialWithdrawal ? EVENT_TYPE_PARTIAL_WITHDRAWAL : EVENT_TYPE_CLAIM;
        String narration = isPartialWithdrawal
                ? "Partial Withdrawal: " + claimReference + " — " + claimResponse.getClaimTypeName()
                : "Claim: " + claimReference + " — " + claimResponse.getClaimTypeName();

        MemberDetail memberDetail = memberDetailRepository.findByNppfNumber(claimResponse.getNppfNumber())
                .orElseThrow(() -> new RuntimeException(
                        "Member details not found for NPPF number: " + claimResponse.getNppfNumber()));

        return ClaimAccountingEvent.builder()
                .eventType(eventType)
                .claimDetailId(claimResponse.getId())
                .nppfNumber(claimResponse.getNppfNumber())
                .identityNumber(memberDetail.getIdentityNumber())
                .memberName(buildMemberName(memberDetail))
                .agencyCategoryId(claimResponse.getMemberCategoryId())
                .agencyCode(claimResponse.getAgencyCode())
                .agencyName(claimResponse.getAgencyCode())
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
                .narration(narration)
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

    private boolean isPartialWithdrawalClaim(GeneralClaimDetailResponse claimResponse) {
        Long claimTypeId = claimResponse.getClaimTypeId();
        return claimTypeId != null && claimTypeId == 2L;
    }

    @Override
    public boolean hasLedgerEntries(Long claimId) {
        return accountingEventRepository.existsByClaimDetailId(claimId);
    }

    
}