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
import com.claim.claim_processing.application.entity.claimDetail.ClaimAccountingEvent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerEntry;
import com.claim.claim_processing.application.repository.claimDetail.ClaimAccountingEventRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimLedgerEntryRepository;
import com.claim.claim_processing.application.service.application.ClaimLedgerService;
import com.claim.claim_processing.common.entities.common.CoaAccountMapping;
import com.claim.claim_processing.common.entities.common.CoaSubAccount;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.common.CoaAccountMappingRepository;
import com.claim.claim_processing.common.repository.common.CoaMainAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaSubAccountRepository;
import com.claim.claim_processing.common.repository.others.MemberDetailRepository;
import com.claim.claim_processing.exceptions.ClaimException;

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
    private final ClaimDetailRepository claimDetailRepository;

    private static final String EVENT_TYPE_CLAIM = "CLAIM";
    private static final String EVENT_TYPE_REFUND = "REFUND";
    private static final String EVENT_TYPE_DEDUCTION = "DEDUCTION";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_POSTED = "POSTED";
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
        System.out.println("========== START: createLedgerEntries ==========");
        System.out.println("Claim ID: " + claimResponse.getId() + ", Status: " + claimResponse.getStatusName()
                + ", Agency: " + claimResponse.getMemberCategoryId());

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
        System.out.println("Is Partial Withdrawal: " + isPartialWithdrawal);

        // 3. Determine TRAN_CODE based on Agency Category
        String agencyCategoryId = claimResponse.getMemberCategoryId();
        String tranCode = isPartialWithdrawal
                ? PARTIAL_TRAN_CODE_MAP.getOrDefault(agencyCategoryId, "PWP")
                : TRAN_CODE_MAP.getOrDefault(agencyCategoryId, "RPFC");
        System.out.println("Agency Category: " + agencyCategoryId + ", TRAN_CODE: " + tranCode
                + ", isPartialWithdrawal: " + isPartialWithdrawal);

        // 4. Build detailed component amounts (NOT grouped!)
        Map<String, BigDecimal> componentAmounts = buildDetailedComponentAmounts(claimResponse);
        System.out.println("Detailed Components: " + componentAmounts);

        // 5. Build deduction amounts
        Map<String, BigDecimal> deductionAmounts = buildDeductionAmounts(claimResponse);
        System.out.println("Deduction Amounts: " + deductionAmounts);

        // 6. Build forfeited amounts (These ARE the lapse amounts)
        Map<String, BigDecimal> forfeitedAmounts = buildForfeitedAmounts(claimResponse);
        System.out.println("Forfeited Amounts: " + forfeitedAmounts);

        // 7. Calculate total eligible amount
        BigDecimal totalEligible = calculateTotalAmount(componentAmounts);
        System.out.println("Total Eligible Amount: " + totalEligible);

        // 8. Calculate total deductions
        BigDecimal totalDeductions = calculateTotalAmount(deductionAmounts);
        System.out.println("Total Deductions: " + totalDeductions);

        // 9. Calculate total forfeited (this IS the lapse amount)
        BigDecimal totalForfeited = calculateTotalAmount(forfeitedAmounts);
        System.out.println("Total Forfeited: " + totalForfeited);

        // 10. Get final payable amount (NET amount after deductions)
        BigDecimal finalPayable = claimResponse.getCalculationSummary() != null
                ? claimResponse.getCalculationSummary().getFinalPayableAmount()
                : BigDecimal.ZERO;
        System.out.println("Final Payable (Net) Amount: " + finalPayable);

        // ================================================================
        // FIX: For Partial Withdrawal, adjust component amounts
        // ================================================================
        Map<String, BigDecimal> componentAmountsForLedger = new HashMap<>();

        if (isPartialWithdrawal) {
            System.out.println("Processing Partial Withdrawal - Scaling components individually");
            if (totalEligible.compareTo(BigDecimal.ZERO) > 0 && finalPayable.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = finalPayable.divide(totalEligible, 10, java.math.RoundingMode.HALF_UP);
                System.out.println("Partial Withdrawal Ratio: " + ratio);

                // Scale each component individually
                for (Map.Entry<String, BigDecimal> entry : componentAmounts.entrySet()) {
                    BigDecimal scaledAmount = entry.getValue().multiply(ratio)
                            .setScale(2, java.math.RoundingMode.HALF_UP);
                    // Only add if amount > 0
                    if (scaledAmount.compareTo(BigDecimal.ZERO) > 0) {
                        componentAmountsForLedger.put(entry.getKey(), scaledAmount);
                        System.out.println("Component: " + entry.getKey() + " -> Original: " + entry.getValue()
                                + ", Scaled: " + scaledAmount);
                    }
                }
            } else {
                // If ratio is zero or invalid, don't add any components
                System.out.println("No eligible amount or final payable, skipping components");
            }
        } else {
            System.out.println("Regular Claim - Using full amounts");
            componentAmountsForLedger = componentAmounts;
        }

        System.out.println("Component Amounts For Ledger: " + componentAmountsForLedger);

        // 11. LAPSE AMOUNT = FORFEITED AMOUNT (only for regular claims)
        BigDecimal lapseAmount = BigDecimal.ZERO;
        if (!isPartialWithdrawal) {
            lapseAmount = totalForfeited;
            System.out.println("Lapse Amount (Forfeited): " + lapseAmount);

            BigDecimal totalCredited = totalDeductions.add(finalPayable).add(totalForfeited);
            System.out.println("Balance check: Total Eligible (" + totalEligible
                    + ") vs Deductions + Final Payable + Forfeited (" + totalCredited + ")");
            if (totalEligible.compareTo(totalCredited) != 0) {
                System.out.println("⚠️ Balance check FAILED! Difference: " + totalEligible.subtract(totalCredited));
            } else {
                System.out.println("✅ Balance check PASSED!");
            }
        }

        // 12. Get COA Mappings
        String eventType = EVENT_TYPE_REFUND;
        System.out.println("Event Type: " + eventType);

        List<CoaAccountMapping> refundMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        eventType, agencyCategoryId);

        // If no partial withdrawal mappings found, fall back to REFUND
        if (refundMappings.isEmpty()) {
            System.out.println("No " + eventType + " mappings found, falling back to REFUND");
            refundMappings = coaAccountMappingRepository
                    .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                            EVENT_TYPE_REFUND, agencyCategoryId);
        }

        List<CoaAccountMapping> deductionMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_DEDUCTION, agencyCategoryId);

        System.out.println("REFUND Mappings found: " + refundMappings.size() + ", DEDUCTION Mappings found: "
                + deductionMappings.size());

        // 13. DEBUG - Show what we have
        debugComponentAmounts(componentAmountsForLedger, refundMappings, deductionAmounts, finalPayable, lapseAmount,
                forfeitedAmounts);

        ClaimDetail claimDetail = claimDetailRepository.findById(claimResponse.getId())
                .orElseThrow(() -> ClaimException.notFound("claim detail not found"));
        System.out.println("i am claim detail id: " + claimDetail.getId());
        // 14. Create and SAVE Accounting Event
        ClaimAccountingEvent event = createAccountingEvent(claimResponse, tranCode, createdBy);
        event.setClaimDetail(claimDetail);
        event = accountingEventRepository.saveAndFlush(event);
        System.out.println("Accounting Event created with ID: " + event.getId());

        // 15. Generate Ledger Entries
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // 15a. Process REFUND mappings (DEBIT - All refund components)
        System.out.println("========== Processing DEBIT Entries ==========");
        for (CoaAccountMapping mapping : refundMappings) {
            String componentCode = mapping.getComponentCode();

            if ("BANK".equals(componentCode) || "LAPSE".equals(componentCode)) {
                continue;
            }

            BigDecimal amount = componentAmountsForLedger.getOrDefault(componentCode, BigDecimal.ZERO);
            System.out.println("DEBIT - Component: " + componentCode + ", Amount: " + amount);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Skipping zero amount for component: " + componentCode);
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
                    .drcr(mapping.getDrcr())
                    .amount(amount)
                    .entryRole(mapping.getEntryRole())
                    .componentCode(componentCode)
                    .narration(subAccount.getSubAccountName())
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entry);
            System.out.println("✅ DEBIT Entry: SEQ=" + seqNo + ", Component=" + componentCode + ", Amount=" + amount);
        }

        // 15b. Process DEDUCTION mappings (CREDIT) - Only for regular claims
        System.out.println("========== Processing CREDIT Entries ==========");
        Map<String, BigDecimal> matchedDeductions = new HashMap<>();

        if (!isPartialWithdrawal) {
            System.out.println("Processing deductions for regular claim");
            for (CoaAccountMapping mapping : deductionMappings) {
                String componentCode = mapping.getComponentCode();

                if ("LAPSE".equals(componentCode)) {
                    continue;
                }

                BigDecimal amount = deductionAmounts.getOrDefault(componentCode, BigDecimal.ZERO);
                System.out.println("CREDIT - Deduction Component: " + componentCode + ", Amount: " + amount);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("No matching deduction for component: " + componentCode);
                    continue;
                }

                matchedDeductions.put(componentCode, amount);
                CoaSubAccount subAccount = coaSubAccountRepository.findBySubAccountCode(mapping.getSubAccountCode())
                        .orElseThrow(
                                () -> new RuntimeException("SubAccount not found: " + mapping.getSubAccountCode()));
                seqNo++;
                ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(mapping.getMainAccountCode())
                        .subAccountCode(mapping.getSubAccountCode())
                        .drcr(mapping.getDrcr())
                        .amount(amount)
                        .entryRole(mapping.getEntryRole())
                        .componentCode(componentCode)
                        .narration(subAccount.getSubAccountName())
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(entry);
                System.out.println(
                        "✅ CREDIT Entry: SEQ=" + seqNo + ", Component=" + componentCode + ", Amount=" + amount);
            }

            // 15c. Process any remaining deductions - Only for regular claims
            for (Map.Entry<String, BigDecimal> entry : deductionAmounts.entrySet()) {
                String deductionCode = entry.getKey();
                BigDecimal amount = entry.getValue();

                if (matchedDeductions.containsKey(deductionCode)) {
                    continue;
                }

                System.out.println("Remaining deduction - Code: " + deductionCode + ", Amount: " + amount);

                CoaAccountMapping defaultMapping = deductionMappings.stream()
                        .filter(m -> !"LAPSE".equals(m.getComponentCode()))
                        .findFirst()
                        .orElse(null);

                if (defaultMapping != null) {
                    CoaSubAccount subAccount = coaSubAccountRepository
                            .findBySubAccountCode(defaultMapping.getSubAccountCode())
                            .orElseThrow(() -> new RuntimeException(
                                    "SubAccount not found: " + defaultMapping.getSubAccountCode()));
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
                    System.out.println("✅ CREDIT Entry (Default): SEQ=" + seqNo + ", Component=" + deductionCode
                            + ", Amount=" + amount);
                }
            }
        } else {
            System.out.println("Partial Withdrawal - Skipping deductions");
        }

        // 15d. Process FORFEITED components - ONLY for regular claims
        if (!isPartialWithdrawal) {
            System.out.println("Processing forfeited components for regular claim");
            for (Map.Entry<String, BigDecimal> entry : forfeitedAmounts.entrySet()) {
                String componentCode = entry.getKey();
                BigDecimal amount = entry.getValue();

                System.out.println("Forfeited - Component: " + componentCode + ", Amount: " + amount);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                CoaAccountMapping lapseMapping = findMappingByComponent(refundMappings, "LAPSE");

                if (lapseMapping != null) {
                    CoaSubAccount subAccount = coaSubAccountRepository
                            .findBySubAccountCode(lapseMapping.getSubAccountCode())
                            .orElseThrow(() -> new RuntimeException(
                                    "SubAccount not found: " + lapseMapping.getSubAccountCode()));
                    seqNo++;
                    ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                            .accountingEventId(event.getId())
                            .seqNo(seqNo)
                            .mainAccountCode(lapseMapping.getMainAccountCode())
                            .subAccountCode(lapseMapping.getSubAccountCode())
                            .drcr(lapseMapping.getDrcr())
                            .amount(amount)
                            .entryRole("LAPSE")
                            .componentCode(componentCode)
                            .narration(subAccount.getSubAccountName())
                            .createdBy(createdBy)
                            .createdAt(LocalDateTime.now())
                            .build();
                    ledgerEntries.add(ledgerEntry);
                    System.out.println("✅ CREDIT Entry (LAPSE): SEQ=" + seqNo + ", Component=" + componentCode
                            + ", Amount=" + amount);
                } else {
                    System.out.println("⚠️ No LAPSE mapping found for forfeited component: " + componentCode);
                }
            }
        } else {
            System.out.println("Partial Withdrawal - Skipping forfeited components");
        }

        // 15e. BANK Entry (CREDIT) - Net payment to member (for both partial and
        // regular)
        System.out.println("Processing BANK entry - Final Payable: " + finalPayable);
        if (finalPayable.compareTo(BigDecimal.ZERO) > 0) {
            CoaAccountMapping bankMapping = findMappingByComponent(refundMappings, "BANK");
            if (bankMapping != null) {
                CoaSubAccount subAccount = coaSubAccountRepository.findBySubAccountCode(bankMapping.getSubAccountCode())
                        .orElseThrow(
                                () -> new RuntimeException("SubAccount not found: " + bankMapping.getSubAccountCode()));
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
                System.out.println("✅ CREDIT Entry: SEQ=" + seqNo + ", BANK, Amount=" + finalPayable);
            } else {
                System.out.println("⚠️ No BANK mapping found!");
            }
        } else {
            System.out.println("Final Payable is zero, skipping BANK entry");
        }

        System.out.println("Total ledger entries to save: " + ledgerEntries.size());

        // 16. Save all ledger entries
        List<ClaimLedgerEntry> savedEntries = ledgerEntryRepository.saveAll(ledgerEntries);
        System.out.println(savedEntries.size() + " ledger entries saved for claim: " + claimReference);

        // 17. Calculate totals
        BigDecimal totalDr = calculateTotal(savedEntries, "D");
        BigDecimal totalCr = calculateTotal(savedEntries, "C");
        System.out.println("Total DR: " + totalDr + ", Total CR: " + totalCr);

        // 18. Validate balance
        if (totalDr.compareTo(totalCr) != 0) {
            System.out.println("❌❌❌ LEDGER NOT BALANCED! DR: " + totalDr + ", CR: " + totalCr + ", Difference: "
                    + totalDr.subtract(totalCr));
            debugComponentAmounts(componentAmountsForLedger, refundMappings, deductionAmounts, finalPayable,
                    lapseAmount, forfeitedAmounts);
            throw new RuntimeException(
                    "Ledger entries do not balance! Total DR: " + totalDr +
                            ", Total CR: " + totalCr +
                            ", Difference: " + totalDr.subtract(totalCr));
        } else {
            System.out.println("✅✅✅ LEDGER BALANCED! DR: " + totalDr + ", CR: " + totalCr);
        }

        // 19. Update event with totals and status
        event.setStatus(STATUS_POSTED);
        event.setPostedBy(createdBy);
        event.setPostedAt(LocalDateTime.now());
        event.setUpdatedBy(createdBy);
        event.setUpdatedAt(LocalDateTime.now());
        ClaimAccountingEvent updatedEvent = accountingEventRepository.save(event);

        System.out.println("========== END: createLedgerEntries SUCCESS ==========");
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

        System.out.println("========== DEBUG: FORFEITED AMOUNTS ==========");
        System.out.println("Forfeited Amounts Map: " + forfeitedAmounts);
        System.out.println("Total Forfeited: " + calculateTotalAmount(forfeitedAmounts));
        System.out.println("Lapse Amount: " + lapseAmount);
        System.out.println("NOTE: Forfeited amounts are INCLUDED in DEBIT, NOT added to CREDIT");

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

        BigDecimal totalDebitExpected = calculateTotalAmount(componentAmounts);
        BigDecimal totalCreditExpected = calculateTotalAmount(deductionAmounts).add(finalPayable);

        System.out.println("Expected Total DR: " + totalDebitExpected);
        System.out.println("Expected Total CR: " + totalCreditExpected);
        System.out.println("Difference: " + totalDebitExpected.subtract(totalCreditExpected));
        System.out.println("==============================================");
    }

    private Map<String, BigDecimal> buildDetailedComponentAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> componentMap = new HashMap<>();

        ClaimCalculationSummaryResponseDto summary = claimResponse.getCalculationSummary();
        boolean isPartialWithdrawal = isPartialWithdrawalClaim(claimResponse);

        System.out.println("========== buildDetailedComponentAmounts ==========");
        System.out.println("isPartialWithdrawal: " + isPartialWithdrawal);

        // 1. Add eligible components from rule evaluations
        if (summary != null && summary.getRuleEvaluations() != null) {
            System.out.println("Processing rule evaluations...");
            for (ClaimRuleEvaluationListDto ruleEvaluation : summary.getRuleEvaluations()) {
                if (ruleEvaluation.getComponents() == null) {
                    continue;
                }

                for (ClaimCalculationComponentDto component : ruleEvaluation.getComponents()) {
                    String code = component.getComponentCode();
                    BigDecimal amount = component.getAmount() != null ? component.getAmount() : BigDecimal.ZERO;

                    System.out.println("Component from rule: " + code + " = " + amount);

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    // ✅ Map each component individually - same for both normal and partial
                    String mappedCode = mapComponentToCoaCode(code);
                    if (mappedCode != null) {
                        componentMap.merge(mappedCode, amount, BigDecimal::add);
                        System.out.println("  ✅ Component: " + code + " -> " + mappedCode + ", Amount: " + amount);
                    } else {
                        System.out.println("  ⚠️ No mapping found for component: " + code);
                    }
                }
            }
        } else {
            System.out.println("No summary or rule evaluations found!");
        }

        // 2. ADD FORFEITED COMPONENTS TO DEBIT FOR NORMAL CLAIMS ONLY
        if (!isPartialWithdrawal) {
            List<ClaimForfeitedComponentResponseDto> forfeitedComponents = claimResponse.getForfeitedComponents();
            System.out.println("Processing forfeited components, count: "
                    + (forfeitedComponents != null ? forfeitedComponents.size() : 0));

            if (forfeitedComponents != null) {
                for (ClaimForfeitedComponentResponseDto forfeited : forfeitedComponents) {
                    String code = forfeited.getComponentCode();
                    BigDecimal amount = forfeited.getAmount() != null ? forfeited.getAmount() : BigDecimal.ZERO;

                    System.out.println("Forfeited component: " + code + " = " + amount);

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    String mappedCode = mapForfeitedComponentCode(code);
                    if (mappedCode != null) {
                        componentMap.merge(mappedCode, amount, BigDecimal::add);
                        System.out.println("  ✅ Forfeited Component added to DEBIT: " + code + " -> " + mappedCode
                                + ", Amount: " + amount);
                    } else {
                        System.out.println("  ⚠️ No mapping found for forfeited component: " + code);
                    }
                }
            }
        } else {
            System.out.println("Partial Withdrawal - Skipping forfeited components");
        }

        System.out.println("Final Component Map: " + componentMap);
        return componentMap;
    }

    private String mapComponentToCoaCode(String componentCode) {
    if (componentCode == null) return null;

    String cleanCode = componentCode.toUpperCase().trim();
    cleanCode = cleanCode.replace("_CUMULATIVE", "")
            .replace("_CUM", "")
            .replace("_YEAR", "");

    System.out.println("Mapping component: " + cleanCode);

    // PF Components - Map to themselves (they exist in COA_ACCOUNT_MAPPING)
    switch (cleanCode) {
        case "PF_MC":
        case "PF_IMC":
        case "PF_EC":
        case "PF_IEC":
        case "PF_GC":
        case "PF_IGC":
            System.out.println("  ✅ PF Component mapped to: " + cleanCode);
            return cleanCode;

        // =============================================
        // PENSION COMPONENTS - Map to COA component codes
        // =============================================
        
        // Employer Pension (P_EC) → PENSION_REFUND
        case "P_EC":
        case "PC_EC":
            System.out.println("  ✅ Pension Employer mapped to: PENSION_REFUND");
            return "P_EC";
            
        // Employer Pension Interest (P_IEC) → PENSION_INT_REFUND
        case "P_IEC":
        case "PC_IEC":
            System.out.println("  ✅ Pension Employer Interest mapped to: PENSION_INT_REFUND");
            return "P_IEC";
            
        // Member Pension (P_MC) → PENSION_PAYMENT
        case "P_MC":
        case "PC_MC":
            System.out.println("  ✅ Pension Member mapped to: PENSION_PAYMENT");
            return "P_MC";
            
        // Member Pension Interest (P_IMC) → PENSION_INT_PAYMENT
        case "P_IMC":
        case "PC_IMC":
            System.out.println("  ✅ Pension Member Interest mapped to: PENSION_INT_PAYMENT");
            return "P_IMC";

        // Partial Withdrawal
        case "PARTIAL_PF":
            System.out.println("  ✅ Partial PF mapped to: PARTIAL_PF");
            return "PARTIAL_PF";

        default:
            System.out.println("  ⚠️ No mapping found for: " + componentCode);
            return null;
    }
}

/**
 * Map forfeited component code to COA component code
 */
private String mapForfeitedComponentCode(String componentCode) {
    if (componentCode == null) return null;

    String cleanCode = componentCode.toUpperCase().trim();

    System.out.println("Mapping forfeited component: " + cleanCode);

    // PF Components - Map to themselves (they exist in COA_ACCOUNT_MAPPING)
    switch (cleanCode) {
        case "PF_MC":
        case "PF_IMC":
        case "PF_EC":
        case "PF_IEC":
        case "PF_GC":
        case "PF_IGC":
            System.out.println("  ✅ PF Forfeited mapped to: " + cleanCode);
            return cleanCode;

        // =============================================
        // PENSION COMPONENTS - All go to LAPSE_PENSION when forfeited
        // =============================================
        case "P_EC":
        case "PC_EC":
        case "P_IEC":
        case "PC_IEC":
        case "P_MC":
        case "PC_MC":
        case "P_IMC":
        case "PC_IMC":
            System.out.println("  ✅ Pension Forfeited mapped to: LAPSE_PENSION");
            return "LAPSE_PENSION";

        case "PARTIAL_PF":
            return "PARTIAL_PF";

        default:
            System.out.println("  ⚠️ No forfeited mapping found for: " + componentCode);
            return null;
    }
}

    /**
     * Build deduction amounts from claim response - FIXED VERSION
     */
    private Map<String, BigDecimal> buildDeductionAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> deductionMap = new HashMap<>();

        ClaimDeductionResponseDto deductionDetail = claimResponse.getDeductionDetail();

        if (deductionDetail == null) {
            log.warn("No deduction detail found in claim response");
            return deductionMap;
        }

        log.info("Deduction Detail , Deducted Amount: {}",
                deductionDetail.getDeductedAmount());

        List<ClaimDeductionItemResponseDto> deductionItems = deductionDetail.getDeductionItems();

        if (deductionItems == null || deductionItems.isEmpty()) {
            log.warn("Deduction items list is null or empty");
            BigDecimal deductedAmount = deductionDetail.getDeductedAmount() != null
                    ? deductionDetail.getDeductedAmount()
                    : BigDecimal.ZERO;

            if (deductedAmount.compareTo(BigDecimal.ZERO) > 0) {
                deductionMap.put("OTHER_DEDUCTION", deductedAmount);
                log.debug("Using OTHER_DEDUCTION for amount: {}", deductedAmount);
            }
            return deductionMap;
        }

        log.info("Processing {} deduction items", deductionItems.size());

        for (ClaimDeductionItemResponseDto item : deductionItems) {
            log.info("Processing item: Category='{}', Amount={}",
                    item.getDeductionCategory(),
                    item.getDeductedAmount());

            BigDecimal deductedAmount = item.getDeductedAmount() != null
                    ? item.getDeductedAmount()
                    : BigDecimal.ZERO;

            if (deductedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("Skipping zero amount for item: {}", item.getDeductionCategory());
                continue;
            }

            String componentCode = mapDeductionItemToComponentCode(item);
            log.info("Mapped '{}' to component code: {}", item.getDeductionCategory(), componentCode);

            if (componentCode != null) {
                BigDecimal existingAmount = deductionMap.getOrDefault(componentCode, BigDecimal.ZERO);
                deductionMap.put(componentCode, existingAmount.add(deductedAmount));
                log.info("Added {} to {}: total now {}", deductedAmount, componentCode,
                        deductionMap.get(componentCode));
            } else {
                log.warn("⚠️ No mapping found for deduction category: {}", item.getDeductionCategory());
            }
        }

        log.info("Final deduction map: {}", deductionMap);
        return deductionMap;
    }

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

            String mappedCode = mapForfeitedComponentCode(componentCode);
            if (mappedCode != null) {
                BigDecimal existingAmount = forfeitedMap.getOrDefault(mappedCode, BigDecimal.ZERO);
                forfeitedMap.put(mappedCode, existingAmount.add(amount));
                log.debug("Forfeited Component: {} -> {}, Amount: {}", componentCode, mappedCode, amount);
            }
        }

        log.info("Total forfeited map: {}", forfeitedMap);
        return forfeitedMap;
    }

    // private String mapForfeitedComponentCode(String componentCode) {
    //     if (componentCode == null)
    //         return null;

    //     String cleanCode = componentCode.toUpperCase().trim();
    //     System.out.println("Mapping forfeited component: " + cleanCode);

    //     // PF_EC and PF_IEC should map to themselves
    //     switch (cleanCode) {
    //         case "PF_MC":
    //         case "PF_IMC":
    //         case "PF_EC":
    //         case "PF_IEC":
    //         case "PF_GC":
    //         case "PF_IGC":
    //         case "P_MC":
    //         case "P_IMC":
    //         case "P_EC":
    //         case "P_IEC":
    //             System.out.println("  Mapped to: " + cleanCode);
    //             return cleanCode;
    //         case "PC_MC":
    //             return "P_MC";
    //         case "PC_IMC":
    //             return "P_IMC";
    //         case "PC_EC":
    //             return "P_EC";
    //         case "PC_IEC":
    //             return "P_IEC";
    //         default:
    //             System.out.println("  ⚠️ No forfeited mapping found for: " + componentCode);
    //             return null;
    //     }
    // }

    /**
     * Map deduction item to component code
     */
    private String mapDeductionItemToComponentCode(ClaimDeductionItemResponseDto item) {
        String deductionCategory = item.getDeductionCategory();
        String referenceName = item.getDeductionCategory() != null ? item.getDeductionCategory().toUpperCase() : "";

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
     * Map deduction category to component code - FIXED to handle "Housing Loan" and
     * "Vehicle Loan"
     */
    private String mapDeductionCategoryToComponentCode(String category) {
        if (category == null)
            return null;

        String upperCategory = category.toUpperCase();

        // Check for loan-related categories - using CONTAINS for flexibility
        if (upperCategory.contains("LOAN")) {
            log.debug("Category '{}' mapped to LOAN_ADJUSTMENT", upperCategory);
            return "LOAN_ADJUSTMENT";
        }

        // Check for rental-related categories
        if (upperCategory.contains("RENT") || upperCategory.contains("RESIDENTIAL")) {
            log.debug("Category '{}' mapped to RENTAL_ADJUSTMENT", upperCategory);
            return "RENTAL_ADJUSTMENT";
        }

        // Specific mappings for exact matches
        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("HOUSING LOAN", "LOAN_ADJUSTMENT");
        categoryMapping.put("VEHICLE LOAN", "LOAN_ADJUSTMENT");
        categoryMapping.put("PF_LOAN", "LOAN_ADJUSTMENT");
        categoryMapping.put("PENSION_LOAN", "LOAN_ADJUSTMENT");
        categoryMapping.put("RENTAL", "RENTAL_ADJUSTMENT");
        categoryMapping.put("RESIDENTIAL", "RENTAL_ADJUSTMENT");

        String mapped = categoryMapping.get(upperCategory);
        if (mapped != null) {
            log.debug("Category '{}' mapped to {} via exact match", upperCategory, mapped);
        } else {
            log.warn("No mapping found for category: {}", upperCategory);
        }

        return mapped;
    }

    /**
     * Map deduction reference name to component code - FIXED to handle "Housing
     * Loan" and "Vehicle Loan"
     */
    private String mapDeductionReferenceNameToComponentCode(String referenceName) {
        if (referenceName == null)
            return null;

        String upperName = referenceName.toUpperCase();

        // Check for loan-related references
        if (upperName.contains("LOAN")) {
            log.debug("Reference '{}' mapped to LOAN_ADJUSTMENT", upperName);
            return "LOAN_ADJUSTMENT";
        }

        // Check for rental-related references
        if (upperName.contains("RENT") || upperName.contains("RESIDENTIAL")) {
            log.debug("Reference '{}' mapped to RENTAL_ADJUSTMENT", upperName);
            return "RENTAL_ADJUSTMENT";
        }

        // Specific mappings
        Map<String, String> referenceMapping = new HashMap<>();
        referenceMapping.put("HOUSING LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("VEHICLE LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("PF LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("PENSION LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("RENTAL", "RENTAL_ADJUSTMENT");
        referenceMapping.put("RESIDENTIAL", "RENTAL_ADJUSTMENT");

        return referenceMapping.get(upperName);
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
        boolean isPartialWithdrawal = isPartialWithdrawalClaim(claimResponse);
        String eventType = isPartialWithdrawal ? EVENT_TYPE_PARTIAL_WITHDRAWAL : EVENT_TYPE_CLAIM;

        MemberDetail memberDetail = memberDetailRepository.findByNppfNumber(claimResponse.getNppfNumber())
                .orElseThrow(() -> new RuntimeException(
                        "Member details not found for NPPF number: " + claimResponse.getNppfNumber()));

        return ClaimAccountingEvent.builder()
                .eventType(eventType)
                .nppfNumber(claimResponse.getNppfNumber())
                .identityNumber(memberDetail.getIdentityNumber())
                .memberName(buildMemberName(memberDetail))
                .agencyCategoryId(claimResponse.getMemberCategoryId())
                .agencyCode(claimResponse.getAgencyCode())
                .agencyName(claimResponse.getAgencyCode())
                .claimTypeId(claimResponse.getClaimTypeId())
                .claimApplicationNumber(claimResponse.getApplicationNumber())
                .monthName(now.getMonth().name())
                .year(String.valueOf(now.getYear()))
                .accountingYear(String.valueOf(now.getYear()))
                .status(STATUS_PENDING)
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
                .claimApplicationNumber(event.getClaimApplicationNumber())
                .nppfNumber(event.getNppfNumber())
                .identityNumber(event.getIdentityNumber())
                .memberName(event.getMemberName())
                .agencyCategoryId(event.getAgencyCategoryId())
                .agencyCode(event.getAgencyCode())
                .agencyName(event.getAgencyName())
                .status(event.getStatus())
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
        ClaimAccountingEvent event = accountingEventRepository.findByClaimDetail_Id(claimId)
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

    private boolean isPartialWithdrawalClaim(GeneralClaimDetailResponse claimResponse) {
        Long claimTypeId = claimResponse.getClaimTypeId();
        return claimTypeId != null && claimTypeId == 2L;
    }

    @Override
    public boolean hasLedgerEntries(Long claimId) {
        return accountingEventRepository.existsByClaimDetailId(claimId);
    }
}