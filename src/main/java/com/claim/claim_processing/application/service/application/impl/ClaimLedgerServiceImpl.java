package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.ClaimLedgerAdditionalDetailResponseDTO;
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
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerAdditionalDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerEntry;
import com.claim.claim_processing.application.repository.claimDetail.ClaimAccountingEventRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimLedgerAdditionalDetailRepository;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private final ClaimLedgerAdditionalDetailRepository additionalDetailRepository;

    private static final String EVENT_TYPE_CLAIM = "CLAIM";
    private static final String EVENT_TYPE_REFUND = "REFUND";
    private static final String EVENT_TYPE_LAPSE = "LAPSE";
    private static final String EVENT_TYPE_DEDUCTION = "DEDUCTION";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_POSTED = "POSTING";
    private static final String EVENT_TYPE_PARTIAL_WITHDRAWAL = "PARTIAL_WITHDRAWAL";
    private static final String EVENT_TYPE_CONTRIBUTION_POSTING = "CONTRIBUTION_POSTING";

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
                claimResponse.getId(), claimResponse.getStatusName(), claimResponse.getMemberCategoryId());

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
        log.info("=== DETAILED COMPONENT AMOUNTS ===");
        componentAmounts.forEach((key, value) -> log.info("  {} = {}", key, value));
        log.info("Total Component Amounts: {}", calculateTotalAmount(componentAmounts));

        // 5. Build deduction amounts
        Map<String, BigDecimal> deductionAmounts = buildDeductionAmounts(claimResponse);
        log.info("=== DEDUCTION AMOUNTS ===");
        deductionAmounts.forEach((key, value) -> log.info("  {} = {}", key, value));
        log.info("Total Deductions: {}", calculateTotalAmount(deductionAmounts));

        // 6. Build forfeited amounts (These ARE the lapse amounts)
        Map<String, BigDecimal> forfeitedAmounts = buildForfeitedAmounts(claimResponse);
        log.info("=== FORFEITED AMOUNTS ===");
        forfeitedAmounts.forEach((key, value) -> log.info("  {} = {}", key, value));
        log.info("Total Forfeited: {}", calculateTotalAmount(forfeitedAmounts));

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

        // ================================================================
        // FIX: For Partial Withdrawal, adjust component amounts
        // ================================================================
        Map<String, BigDecimal> componentAmountsForLedger = new HashMap<>();

        if (isPartialWithdrawal) {
            log.info("Processing Partial Withdrawal - Scaling components individually");
            if (totalEligible.compareTo(BigDecimal.ZERO) > 0 && finalPayable.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = finalPayable.divide(totalEligible, 10, java.math.RoundingMode.HALF_UP);
                log.info("Partial Withdrawal Ratio: {}", ratio);

                // Scale each component individually
                for (Map.Entry<String, BigDecimal> entry : componentAmounts.entrySet()) {
                    BigDecimal scaledAmount = entry.getValue().multiply(ratio)
                            .setScale(2, java.math.RoundingMode.HALF_UP);
                    // Only add if amount > 0
                    if (scaledAmount.compareTo(BigDecimal.ZERO) > 0) {
                        componentAmountsForLedger.put(entry.getKey(), scaledAmount);
                        log.info("Component: {} -> Original: {}, Scaled: {}",
                                entry.getKey(), entry.getValue(), scaledAmount);
                    }
                }
            } else {
                log.info("No eligible amount or final payable, skipping components");
            }
        } else {
            log.info("Regular Claim - Using full amounts");
            componentAmountsForLedger = componentAmounts;
        }

        log.info("=== COMPONENT AMOUNTS FOR LEDGER ===");
        componentAmountsForLedger.forEach((key, value) -> log.info("  {} = {}", key, value));

        // 11. LAPSE AMOUNT = FORFEITED AMOUNT (only for regular claims)
        BigDecimal lapseAmount = BigDecimal.ZERO;
        if (!isPartialWithdrawal) {
            lapseAmount = totalForfeited;
            log.info("Lapse Amount (Forfeited): {}", lapseAmount);

            BigDecimal totalCredited = totalDeductions.add(finalPayable).add(totalForfeited);
            log.info("Balance check: Total Eligible ({}) vs Deductions + Final Payable + Forfeited ({})",
                    totalEligible, totalCredited);
            if (totalEligible.compareTo(totalCredited) != 0) {
                log.warn("⚠️ Balance check FAILED! Difference: {}", totalEligible.subtract(totalCredited));
            } else {
                log.info("✅ Balance check PASSED!");
            }
        }

        // 12. Get COA Mappings
        String eventType = EVENT_TYPE_REFUND;
        log.info("Event Type: {}", eventType);

        List<CoaAccountMapping> refundMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        eventType, agencyCategoryId);

        // If no partial withdrawal mappings found, fall back to REFUND
        if (refundMappings.isEmpty()) {
            log.info("No {} mappings found, falling back to REFUND", eventType);
            refundMappings = coaAccountMappingRepository
                    .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                            EVENT_TYPE_REFUND, agencyCategoryId);
        }

        List<CoaAccountMapping> lapseMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_LAPSE, agencyCategoryId);

        log.info("LAPSE Mappings found: {}", lapseMappings.size());
        if (!lapseMappings.isEmpty()) {
            log.info("First LAPSE Mapping: {}, MainAccount: {}",
                    lapseMappings.get(0).getComponentCode(),
                    lapseMappings.get(0).getMainAccountCode());
        }

        List<CoaAccountMapping> deductionMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_DEDUCTION, agencyCategoryId);

        log.info("REFUND Mappings found: {}, DEDUCTION Mappings found: {}",
                refundMappings.size(), deductionMappings.size());

        // 13. DEBUG - Show what we have
        debugComponentAmounts(componentAmountsForLedger, refundMappings, deductionAmounts, finalPayable, lapseAmount,
                forfeitedAmounts);

        ClaimDetail claimDetail = claimDetailRepository.findById(claimResponse.getId())
                .orElseThrow(() -> ClaimException.notFound("claim detail not found"));
        log.info("Claim Detail ID: {}", claimDetail.getId());

        // 14. Create and SAVE Accounting Event
        ClaimAccountingEvent event = createAccountingEvent(claimResponse, tranCode, createdBy);
        event.setClaimDetail(claimDetail);
        event = accountingEventRepository.saveAndFlush(event);
        log.info("Accounting Event created with ID: {}", event.getId());

        // 15. Generate Ledger Entries
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // 15a. Process REFUND mappings (DEBIT - All refund components)
        log.info("========== Processing DEBIT Entries ==========");
        for (CoaAccountMapping mapping : refundMappings) {
            String componentCode = mapping.getComponentCode();

            if ("BANK".equals(componentCode) || "LAPSE".equals(componentCode)) {
                log.info("Skipping BANK/LAPSE mapping: {}", componentCode);
                continue;
            }

            BigDecimal amount = componentAmountsForLedger.getOrDefault(componentCode, BigDecimal.ZERO);
            log.info("DEBIT - Component: {}, Amount: {}", componentCode, amount);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Skipping zero amount for component: {}", componentCode);
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
            log.info("✅ DEBIT Entry: SEQ={}, Component={}, Amount={}, MainAccount={}, SubAccount={}",
                    seqNo, componentCode, amount, mapping.getMainAccountCode(), mapping.getSubAccountCode());
        }

        // 15b. Process DEDUCTION mappings (CREDIT) - Only for regular claims
        log.info("========== Processing CREDIT Entries ==========");
        Map<String, BigDecimal> matchedDeductions = new HashMap<>();

        if (!isPartialWithdrawal) {
            log.info("Processing deductions for regular claim");
            for (CoaAccountMapping mapping : deductionMappings) {
                String componentCode = mapping.getComponentCode();

                if ("LAPSE".equals(componentCode)) {
                    log.info("Skipping LAPSE in deduction mappings");
                    continue;
                }

                BigDecimal amount = deductionAmounts.getOrDefault(componentCode, BigDecimal.ZERO);
                log.info("CREDIT - Deduction Component: {}, Amount: {}", componentCode, amount);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("No matching deduction for component: {}", componentCode);
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
                log.info("✅ CREDIT Entry: SEQ={}, Component={}, Amount={}, MainAccount={}, SubAccount={}",
                        seqNo, componentCode, amount, mapping.getMainAccountCode(), mapping.getSubAccountCode());
            }

            // 15c. Process any remaining deductions - Only for regular claims
            for (Map.Entry<String, BigDecimal> entry : deductionAmounts.entrySet()) {
                String deductionCode = entry.getKey();
                BigDecimal amount = entry.getValue();

                if (matchedDeductions.containsKey(deductionCode)) {
                    continue;
                }

                log.info("Remaining deduction - Code: {}, Amount: {}", deductionCode, amount);

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
                    log.info("✅ CREDIT Entry (Default): SEQ={}, Component={}, Amount={}",
                            seqNo, deductionCode, amount);
                } else {
                    log.warn("⚠️ No default mapping found for remaining deduction: {}", deductionCode);
                }
            }
        } else {
            log.info("Partial Withdrawal - Skipping deductions");
        }

        // 15d. Process FORFEITED components - ONLY for regular claims
        if (!isPartialWithdrawal) {
            log.info("Processing forfeited components for regular claim");
            log.info("forfeitedAmounts: {}", forfeitedAmounts);
            log.info("lapseMappings size: {}", lapseMappings != null ? lapseMappings.size() : 0);

            // Get the LAPSE mapping from lapseMappings (NOT refundMappings)
            final CoaAccountMapping lapseMapping;
            if (lapseMappings != null && !lapseMappings.isEmpty()) {
                lapseMapping = lapseMappings.get(0);
                log.info("Found LAPSE mapping: {}, MainAccount: {}, SubAccount: {}",
                        lapseMapping.getComponentCode(),
                        lapseMapping.getMainAccountCode(),
                        lapseMapping.getSubAccountCode());
            } else {
                log.warn("⚠️ No LAPSE mappings found in database!");
                lapseMapping = null;
            }

            for (Map.Entry<String, BigDecimal> entry : forfeitedAmounts.entrySet()) {
                String componentCode = entry.getKey();
                BigDecimal amount = entry.getValue();

                log.info("Forfeited - Component: {}, Amount: {}", componentCode, amount);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

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
                            .drcr(lapseMapping.getDrcr()) // CREDIT for LAPSE
                            .amount(amount)
                            .entryRole("LAPSE")
                            .componentCode(componentCode)
                            .narration("LAPSE - " + componentCode + " - " + subAccount.getSubAccountName())
                            .createdBy(createdBy)
                            .createdAt(LocalDateTime.now())
                            .build();
                    ledgerEntries.add(ledgerEntry);
                    log.info("✅ CREDIT Entry (LAPSE): SEQ={}, Component={}, Amount={}",
                            seqNo, componentCode, amount);
                } else {
                    log.warn("⚠️ No LAPSE mapping found for forfeited component: {}", componentCode);
                }
            }
        } else {
            log.info("Partial Withdrawal - Skipping forfeited components");
        }

        // 15e. Final Payment Entry - Net payment to member (for both partial and
        // regular)
        log.info("Processing final payment entry - Final Payable: {}", finalPayable);

        if (finalPayable.compareTo(BigDecimal.ZERO) > 0) {
            CoaAccountMapping bankMapping = findMappingByComponent(refundMappings, "BANK");

            // Check if bank details exist
            boolean hasBankDetails = claimResponse.getBankDetails() != null &&
                    !claimResponse.getBankDetails().isEmpty();

            if (bankMapping != null && hasBankDetails) {
                // ---------- BANK DETAILS EXIST - Normal BANK entry ----------
                CoaSubAccount subAccount = coaSubAccountRepository
                        .findBySubAccountCode(bankMapping.getSubAccountCode())
                        .orElseThrow(() -> new RuntimeException(
                                "SubAccount not found: " + bankMapping.getSubAccountCode()));
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
                log.info("✅ BANK CREDIT Entry: SEQ={}, Amount={}", seqNo, finalPayable);

            } else {
                // ---------- NO BANK DETAILS OR NO BANK MAPPING - Use CONTRIBUTION_POSTING
                // ----------
                log.warn("⚠️ No bank details found for claim: {}. Using CONTRIBUTION_POSTING entry.",
                        claimResponse.getId());

                // Call the new function to handle no bank details
                seqNo = createContributionPostingForNoBank(
                        claimResponse, event, ledgerEntries, finalPayable,
                        agencyCategoryId, seqNo, createdBy);
            }
        } else {
            log.info("Final Payable is zero, skipping payment entry");
        }

        log.info("Total ledger entries to save: {}", ledgerEntries.size());

        // 16. Save all ledger entries
        // 16. Save all ledger entries
        List<ClaimLedgerEntry> savedEntries = ledgerEntryRepository.saveAll(ledgerEntries);
        log.info("{} ledger entries saved for claim: {}", savedEntries.size(), claimReference);

        // ========== SAVE ADDITIONAL DETAILS ==========
        List<ClaimLedgerAdditionalDetail> additionalDetails = new ArrayList<>();

        // Get bank account number from bank details (only 1 record)
        String bankAccountNumber = null;
        if (claimResponse.getBankDetails() != null && !claimResponse.getBankDetails().isEmpty()) {
            bankAccountNumber = claimResponse.getBankDetails().get(0).getAccountNumber();
            log.info("Bank Account Number found: {}", bankAccountNumber);
        }

        for (ClaimLedgerEntry entry : savedEntries) {
            String accountNumber = null;

            // If this is a BANK entry, set the bank account number
            if ("BANK".equals(entry.getEntryRole()) || "BANK".equals(entry.getComponentCode())) {
                accountNumber = bankAccountNumber;
                log.info("Setting bank account number: {} for BANK entry SEQ: {}", accountNumber, entry.getSeqNo());
            } else {
                // For non-BANK entries, use the sub account code
                accountNumber = entry.getSubAccountCode();
                log.info("Setting sub account code: {} for entry SEQ: {}, Component: {}",
                        accountNumber, entry.getSeqNo(), entry.getComponentCode());
            }

            ClaimLedgerAdditionalDetail detail = ClaimLedgerAdditionalDetail.builder()
                    .accountNumber(accountNumber)
                    .drcr(entry.getDrcr())
                    .amount(entry.getAmount())
                    .ledgerEntry(entry)
                    .createdBy(createdBy)
                    .build();
            additionalDetails.add(detail);
        }
        additionalDetailRepository.saveAll(additionalDetails);
        log.info("{} additional details saved for claim: {}", additionalDetails.size(), claimReference);
        // ========== END SAVE ADDITIONAL DETAILS ==========

        BigDecimal totalDr = calculateTotal(savedEntries, "D");
        BigDecimal totalCr = calculateTotal(savedEntries, "C");
        // 18. Validate balance
        if (totalDr.compareTo(totalCr) != 0) {
            log.error("❌❌❌ LEDGER NOT BALANCED! DR: {}, CR: {}, Difference: {}",
                    totalDr, totalCr, totalDr.subtract(totalCr));

            // Log detailed breakdown of what's missing
            log.error("=== LEDGER IMBALANCE DIAGNOSIS ===");
            log.error("Component Amounts For Ledger: {}", componentAmountsForLedger);
            log.error("Deduction Amounts: {}", deductionAmounts);
            log.error("Final Payable: {}", finalPayable);
            log.error("Forfeited Amounts: {}", forfeitedAmounts);

            debugComponentAmounts(componentAmountsForLedger, refundMappings, deductionAmounts, finalPayable,
                    lapseAmount, forfeitedAmounts);
            throw new RuntimeException(
                    "Ledger entries do not balance! Total DR: " + totalDr +
                            ", Total CR: " + totalCr +
                            ", Difference: " + totalDr.subtract(totalCr));
        } else {
            log.info("✅✅✅ LEDGER BALANCED! DR: {}, CR: {}", totalDr, totalCr);
        }

        // 19. Update event with totals and status
        event.setStatus(STATUS_POSTED);
        event.setPostedBy(createdBy);
        event.setPostedAt(LocalDateTime.now());
        event.setUpdatedBy(createdBy);
        event.setUpdatedAt(LocalDateTime.now());
        ClaimAccountingEvent updatedEvent = accountingEventRepository.save(event);

        log.info("========== END: createLedgerEntries SUCCESS ==========");
        return buildResponse(updatedEvent, savedEntries);
    }

    /**
     * NEW FUNCTION: Create CONTRIBUTION_POSTING entries when bank details are not
     * available
     * This will credit the final payable amount to the appropriate component
     * accounts
     */
    private int createContributionPostingForNoBank(GeneralClaimDetailResponse claimResponse,
            ClaimAccountingEvent event,
            List<ClaimLedgerEntry> ledgerEntries,
            BigDecimal finalPayable,
            String agencyCategoryId,
            int seqNo,
            String createdBy) {

        log.info("========== Creating CONTRIBUTION_POSTING entries for No Bank ==========");
        log.info("Final Payable Amount: {}", finalPayable);
        log.info("Agency Category: {}", agencyCategoryId);

        // Get CONTRIBUTION_POSTING mappings for this agency
        List<CoaAccountMapping> contributionMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_CONTRIBUTION_POSTING, agencyCategoryId);

        log.info("CONTRIBUTION_POSTING mappings found: {}", contributionMappings.size());

        if (contributionMappings.isEmpty()) {
            log.warn("⚠️ No CONTRIBUTION_POSTING mappings found for agency: {}. Using fallback BANK entry.",
                    agencyCategoryId);
            return createFallbackBankEntry(event, ledgerEntries, finalPayable, seqNo, createdBy);
        }

        // Get the component amounts to distribute the final payable
        Map<String, BigDecimal> componentAmounts = buildDetailedComponentAmounts(claimResponse);
        BigDecimal totalComponents = calculateTotalAmount(componentAmounts);

        log.info("Total Component Amounts: {}, Final Payable: {}", totalComponents, finalPayable);

        // If total components is zero, use fallback
        if (totalComponents.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("⚠️ No component amounts found. Using fallback BANK entry.");
            return createFallbackBankEntry(event, ledgerEntries, finalPayable, seqNo, createdBy);
        }

        // Filter to only include components that have mappings and amounts
        List<Map.Entry<String, BigDecimal>> activeComponents = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : componentAmounts.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            // Check if mapping exists
            boolean hasMapping = contributionMappings.stream()
                    .anyMatch(m -> entry.getKey().equals(m.getComponentCode()));
            if (hasMapping) {
                activeComponents.add(entry);
            } else {
                log.warn("⚠️ No CONTRIBUTION_POSTING mapping found for component: {}", entry.getKey());
            }
        }

        if (activeComponents.isEmpty()) {
            log.warn("⚠️ No active components with mappings found. Using fallback BANK entry.");
            return createFallbackBankEntry(event, ledgerEntries, finalPayable, seqNo, createdBy);
        }

        // Calculate total of active component amounts
        BigDecimal totalActiveAmounts = activeComponents.stream()
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Total Active Component Amounts: {}", totalActiveAmounts);

        // Calculate ratio to distribute final payable proportionally
        BigDecimal ratio = finalPayable.divide(totalActiveAmounts, 10, java.math.RoundingMode.HALF_UP);
        log.info("Distribution Ratio: {}", ratio);

        // Track total distributed amount
        BigDecimal totalDistributed = BigDecimal.ZERO;
        int currentSeqNo = seqNo;
        List<ClaimLedgerEntry> createdEntries = new ArrayList<>();

        // Process each active component
        for (Map.Entry<String, BigDecimal> entry : activeComponents) {
            String componentCode = entry.getKey();
            BigDecimal componentAmount = entry.getValue();

            // Find mapping for this component
            CoaAccountMapping mapping = contributionMappings.stream()
                    .filter(m -> componentCode.equals(m.getComponentCode()))
                    .findFirst()
                    .orElse(null);

            if (mapping == null) {
                continue;
            }

            // Calculate proportional amount with 2 decimal places
            BigDecimal proportionalAmount = componentAmount.multiply(ratio)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            // Skip if amount is zero
            if (proportionalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Get sub-account
            CoaSubAccount subAccount = null;
            if (mapping.getSubAccountCode() != null) {
                subAccount = coaSubAccountRepository
                        .findBySubAccountCode(mapping.getSubAccountCode())
                        .orElse(null);
            }

            // Create CREDIT entry for this component
            currentSeqNo++;
            ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                    .accountingEventId(event.getId())
                    .seqNo(currentSeqNo)
                    .mainAccountCode(mapping.getMainAccountCode())
                    .subAccountCode(mapping.getSubAccountCode())
                    .drcr("C") // CREDIT to component
                    .amount(proportionalAmount)
                    .entryRole("CONTRIBUTION_POSTING")
                    .componentCode(componentCode)
                    .narration("CONTRIBUTION_POSTING - No Bank Details - " +
                            (subAccount != null ? subAccount.getSubAccountName() : componentCode) +
                            " - Amount: " + proportionalAmount)
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(ledgerEntry);
            createdEntries.add(ledgerEntry);

            log.info(
                    "✅ CONTRIBUTION_POSTING CREDIT Entry: SEQ={}, Component={}, Amount={}, MainAccount={}, SubAccount={}",
                    currentSeqNo, componentCode, proportionalAmount,
                    mapping.getMainAccountCode(), mapping.getSubAccountCode());

            totalDistributed = totalDistributed.add(proportionalAmount);
        }

        // ============================================================
        // FIX: Handle rounding adjustment properly
        // ============================================================
        BigDecimal difference = finalPayable.subtract(totalDistributed);
        log.info("Total Distributed: {}, Final Payable: {}, Difference: {}",
                totalDistributed, finalPayable, difference);

        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            log.info("Applying rounding adjustment: {}", difference);

            if (!createdEntries.isEmpty()) {
                // Get the last created entry
                ClaimLedgerEntry lastEntry = createdEntries.get(createdEntries.size() - 1);

                // Calculate adjusted amount
                BigDecimal adjustedAmount = lastEntry.getAmount().add(difference)
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                // Remove the old entry from ledgerEntries
                ledgerEntries.remove(lastEntry);

                // Get mapping for this component
                CoaAccountMapping mapping = contributionMappings.stream()
                        .filter(m -> lastEntry.getComponentCode().equals(m.getComponentCode()))
                        .findFirst()
                        .orElse(null);

                if (mapping != null) {
                    CoaSubAccount subAccount = null;
                    if (mapping.getSubAccountCode() != null) {
                        subAccount = coaSubAccountRepository
                                .findBySubAccountCode(mapping.getSubAccountCode())
                                .orElse(null);
                    }

                    // Create new entry with adjusted amount
                    ClaimLedgerEntry adjustedEntry = ClaimLedgerEntry.builder()
                            .accountingEventId(event.getId())
                            .seqNo(lastEntry.getSeqNo())
                            .mainAccountCode(mapping.getMainAccountCode())
                            .subAccountCode(mapping.getSubAccountCode())
                            .drcr("C")
                            .amount(adjustedAmount)
                            .entryRole("CONTRIBUTION_POSTING")
                            .componentCode(lastEntry.getComponentCode())
                            .narration("CONTRIBUTION_POSTING - No Bank Details - " +
                                    (subAccount != null ? subAccount.getSubAccountName() : lastEntry.getComponentCode())
                                    +
                                    " (Adjusted) - Amount: " + adjustedAmount)
                            .createdBy(createdBy)
                            .createdAt(LocalDateTime.now())
                            .build();
                    ledgerEntries.add(adjustedEntry);

                    log.info("✅ Adjusted last entry: SEQ={}, Component={}, Old Amount={}, New Amount={}, Difference={}",
                            lastEntry.getSeqNo(), lastEntry.getComponentCode(),
                            lastEntry.getAmount(), adjustedAmount, difference);

                    // Update totalDistributed
                    totalDistributed = totalDistributed.add(difference);
                }
            } else {
                // If no entries were created, add a single adjustment entry
                CoaAccountMapping firstMapping = contributionMappings.stream()
                        .filter(m -> !"BANK".equals(m.getComponentCode()) && !"EXCESS".equals(m.getComponentCode()))
                        .findFirst()
                        .orElse(null);

                if (firstMapping != null) {
                    currentSeqNo++;
                    ClaimLedgerEntry ledgerEntry = ClaimLedgerEntry.builder()
                            .accountingEventId(event.getId())
                            .seqNo(currentSeqNo)
                            .mainAccountCode(firstMapping.getMainAccountCode())
                            .subAccountCode(firstMapping.getSubAccountCode())
                            .drcr("C")
                            .amount(difference)
                            .entryRole("CONTRIBUTION_POSTING")
                            .componentCode(firstMapping.getComponentCode())
                            .narration("CONTRIBUTION_POSTING - Rounding Adjustment - Amount: " + difference)
                            .createdBy(createdBy)
                            .createdAt(LocalDateTime.now())
                            .build();
                    ledgerEntries.add(ledgerEntry);
                    log.info("✅ CONTRIBUTION_POSTING Rounding Entry: SEQ={}, Component={}, Amount={}",
                            currentSeqNo, firstMapping.getComponentCode(), difference);
                }
            }
        }

        log.info("Final Total Distributed: {}, Final Payable: {}, Difference: {}",
                totalDistributed, finalPayable, finalPayable.subtract(totalDistributed));

        log.info("CONTRIBUTION_POSTING entries created for No Bank scenario");
        return currentSeqNo;
    }

    /**
     * Fallback method to create a BANK entry when no CONTRIBUTION_POSTING mappings
     * are found
     */
    private int createFallbackBankEntry(ClaimAccountingEvent event,
            List<ClaimLedgerEntry> ledgerEntries,
            BigDecimal finalPayable,
            int seqNo,
            String createdBy) {

        log.warn("⚠️ Using fallback BANK entry for amount: {}", finalPayable);

        seqNo++;
        ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                .accountingEventId(event.getId())
                .seqNo(seqNo)
                .mainAccountCode("15020100")
                .subAccountCode("15020109")
                .drcr("C") // CREDIT to bank
                .amount(finalPayable)
                .entryRole("BANK")
                .componentCode("BANK")
                .narration(
                        "BANK - Fallback (No bank details / No CONTRIBUTION_POSTING mapping) - Amount: " + finalPayable)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
        ledgerEntries.add(entry);
        log.info("✅ BANK Fallback Entry: SEQ={}, Amount={}", seqNo, finalPayable);

        return seqNo;
    }

    // ============================================================
    // All existing methods remain unchanged below
    // ============================================================

    private void debugComponentAmounts(Map<String, BigDecimal> componentAmounts,
            List<CoaAccountMapping> refundMappings,
            Map<String, BigDecimal> deductionAmounts,
            BigDecimal finalPayable,
            BigDecimal lapseAmount,
            Map<String, BigDecimal> forfeitedAmounts) {

        log.info("========== DEBUG: COMPONENT AMOUNTS ==========");
        log.info("Component Amounts Map: {}", componentAmounts);
        log.info("Total Component Amounts: {}", calculateTotalAmount(componentAmounts));

        log.info("========== DEBUG: FORFEITED AMOUNTS ==========");
        log.info("Forfeited Amounts Map: {}", forfeitedAmounts);
        log.info("Total Forfeited: {}", calculateTotalAmount(forfeitedAmounts));
        log.info("Lapse Amount: {}", lapseAmount);
        log.info("NOTE: Forfeited amounts are INCLUDED in DEBIT, NOT added to CREDIT");

        log.info("========== DEBUG: REFUND MAPPINGS ==========");
        for (CoaAccountMapping mapping : refundMappings) {
            if (!"BANK".equals(mapping.getComponentCode()) && !"LAPSE".equals(mapping.getComponentCode())) {
                BigDecimal amount = componentAmounts.getOrDefault(mapping.getComponentCode(), BigDecimal.ZERO);
                log.info("Mapping: {} -> Amount: {}", mapping.getComponentCode(), amount);
            }
        }

        log.info("========== DEBUG: DEDUCTION AMOUNTS ==========");
        log.info("Deduction Amounts Map: {}", deductionAmounts);
        log.info("Total Deductions: {}", calculateTotalAmount(deductionAmounts));

        log.info("========== DEBUG: TOTALS ==========");
        log.info("Final Payable: {}", finalPayable);

        BigDecimal totalDebitExpected = calculateTotalAmount(componentAmounts);
        BigDecimal totalCreditExpected = calculateTotalAmount(deductionAmounts).add(finalPayable);

        log.info("Expected Total DR: {}", totalDebitExpected);
        log.info("Expected Total CR: {}", totalCreditExpected);
        log.info("Difference: {}", totalDebitExpected.subtract(totalCreditExpected));
        log.info("==============================================");
    }

    private Map<String, BigDecimal> buildDetailedComponentAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> componentMap = new HashMap<>();

        ClaimCalculationSummaryResponseDto summary = claimResponse.getCalculationSummary();
        boolean isPartialWithdrawal = isPartialWithdrawalClaim(claimResponse);

        log.info("========== buildDetailedComponentAmounts ==========");
        log.info("isPartialWithdrawal: {}", isPartialWithdrawal);

        // 1. Add eligible components from rule evaluations
        if (summary != null && summary.getRuleEvaluations() != null) {
            log.info("Processing rule evaluations...");
            for (ClaimRuleEvaluationListDto ruleEvaluation : summary.getRuleEvaluations()) {
                if (ruleEvaluation.getComponents() == null) {
                    continue;
                }

                for (ClaimCalculationComponentDto component : ruleEvaluation.getComponents()) {
                    String code = component.getComponentCode();
                    BigDecimal amount = component.getAmount() != null ? component.getAmount() : BigDecimal.ZERO;

                    log.info("Component from rule: {} = {}", code, amount);

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    // Map each component individually
                    String mappedCode = mapComponentToCoaCode(code);
                    if (mappedCode != null) {
                        componentMap.merge(mappedCode, amount, BigDecimal::add);
                        log.info("  ✅ Component: {} -> {}, Amount: {}", code, mappedCode, amount);
                    } else {
                        log.warn("  ⚠️ No mapping found for component: {}", code);
                    }
                }
            }
        } else {
            log.info("No summary or rule evaluations found!");
        }

        // 2. ADD FORFEITED COMPONENTS TO DEBIT FOR NORMAL CLAIMS ONLY
        if (!isPartialWithdrawal) {
            List<ClaimForfeitedComponentResponseDto> forfeitedComponents = claimResponse.getForfeitedComponents();
            log.info("Processing forfeited components, count: {}",
                    forfeitedComponents != null ? forfeitedComponents.size() : 0);

            if (forfeitedComponents != null) {
                for (ClaimForfeitedComponentResponseDto forfeited : forfeitedComponents) {
                    String code = forfeited.getComponentCode();
                    BigDecimal amount = forfeited.getAmount() != null ? forfeited.getAmount() : BigDecimal.ZERO;

                    log.info("Forfeited component: {} = {}", code, amount);

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    String mappedCode = mapComponentToCoaCode(code);
                    if (mappedCode != null) {
                        componentMap.merge(mappedCode, amount, BigDecimal::add);
                        log.info("  ✅ Forfeited Component: {} -> {}, Amount: {}", code, mappedCode, amount);
                    } else {
                        log.warn("  ⚠️ No mapping found for forfeited component: {}", code);
                    }
                }
            }
        } else {
            log.info("Partial Withdrawal - Skipping forfeited components");
        }

        log.info("Final Component Map: {}", componentMap);
        return componentMap;
    }

    private String mapComponentToCoaCode(String componentCode) {
        if (componentCode == null)
            return null;

        String cleanCode = componentCode.toUpperCase().trim();
        cleanCode = cleanCode.replace("_CUMULATIVE", "")
                .replace("_CUM", "")
                .replace("_YEAR", "");

        log.debug("Mapping component: {}", cleanCode);

        // PF Components - Map to themselves (they exist in COA_ACCOUNT_MAPPING)
        switch (cleanCode) {
            case "PF_MC":
            case "PF_IMC":
            case "PF_EC":
            case "PF_IEC":
            case "PF_GC":
            case "PF_IGC":
                log.debug("  ✅ PF Component mapped to: {}", cleanCode);
                return cleanCode;

            // =============================================
            // PENSION COMPONENTS - Map to COA component codes
            // =============================================
            case "P_EC":
            case "PC_EC":
                log.debug("  ✅ Pension Employer mapped to: P_EC");
                return "P_EC";

            // Employer Pension Interest (P_IEC)
            case "P_IEC":
            case "PC_IEC":
                log.debug("  ✅ Pension Employer Interest mapped to: P_IEC");
                return "P_IEC";

            // Member Pension (P_MC)
            case "P_MC":
            case "PC_MC":
                log.debug("  ✅ Pension Member mapped to: P_MC");
                return "P_MC";

            // Member Pension Interest (P_IMC)
            case "P_IMC":
            case "PC_IMC":
                log.debug("  ✅ Pension Member Interest mapped to: P_IMC");
                return "P_IMC";

            default:
                log.warn("  ⚠️ No mapping found for: {}", componentCode);
                return null;
        }
    }

    private Map<String, BigDecimal> buildDeductionAmounts(GeneralClaimDetailResponse claimResponse) {
        Map<String, BigDecimal> deductionMap = new HashMap<>();

        ClaimDeductionResponseDto deductionDetail = claimResponse.getDeductionDetail();

        if (deductionDetail == null) {
            log.warn("No deduction detail found in claim response");
            return deductionMap;
        }

        log.info("Deduction Detail, Deducted Amount: {}", deductionDetail.getDeductedAmount());

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

        BigDecimal totalForfeited = BigDecimal.ZERO;

        log.info("========== BUILDING FORFEITED AMOUNTS ==========");
        for (ClaimForfeitedComponentResponseDto forfeited : forfeitedComponents) {
            String componentCode = forfeited.getComponentCode();
            BigDecimal amount = forfeited.getAmount() != null ? forfeited.getAmount() : BigDecimal.ZERO;

            log.info("Forfeited component: {} = {}", componentCode, amount);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            totalForfeited = totalForfeited.add(amount);
            log.info("  Total so far: {}", totalForfeited);
        }

        // Add single LAPSE entry with total amount
        if (totalForfeited.compareTo(BigDecimal.ZERO) > 0) {
            forfeitedMap.put("LAPSE", totalForfeited);
            log.info("  ✅ Added LAPSE total: {}", totalForfeited);
        }

        log.info("Final forfeited map: {}", forfeitedMap);
        return forfeitedMap;
    }

    private String mapDeductionItemToComponentCode(ClaimDeductionItemResponseDto item) {
        String deductionCategory = item.getDeductionCategory();
        String referenceName = item.getDeductionCategory() != null ? item.getDeductionCategory().toUpperCase() : "";

        String componentCode = mapDeductionCategoryToComponentCode(deductionCategory);
        if (componentCode != null) {
            return componentCode;
        }

        componentCode = mapDeductionReferenceNameToComponentCode(referenceName);
        if (componentCode != null) {
            return componentCode;
        }

        return null;
    }

    private String mapDeductionCategoryToComponentCode(String category) {
        if (category == null)
            return null;

        String upperCategory = category.toUpperCase();

        if (upperCategory.contains("LOAN")) {
            log.debug("Category '{}' mapped to LOAN_ADJUSTMENT", upperCategory);
            return "LOAN_ADJUSTMENT";
        }

        if (upperCategory.contains("RENT") || upperCategory.contains("RESIDENTIAL")) {
            log.debug("Category '{}' mapped to RENTAL_ADJUSTMENT", upperCategory);
            return "RENTAL_ADJUSTMENT";
        }

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

    private String mapDeductionReferenceNameToComponentCode(String referenceName) {
        if (referenceName == null)
            return null;

        String upperName = referenceName.toUpperCase();

        if (upperName.contains("LOAN")) {
            log.debug("Reference '{}' mapped to LOAN_ADJUSTMENT", upperName);
            return "LOAN_ADJUSTMENT";
        }

        if (upperName.contains("RENT") || upperName.contains("RESIDENTIAL")) {
            log.debug("Reference '{}' mapped to RENTAL_ADJUSTMENT", upperName);
            return "RENTAL_ADJUSTMENT";
        }

        Map<String, String> referenceMapping = new HashMap<>();
        referenceMapping.put("HOUSING LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("VEHICLE LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("PF LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("PENSION LOAN", "LOAN_ADJUSTMENT");
        referenceMapping.put("RENTAL", "RENTAL_ADJUSTMENT");
        referenceMapping.put("RESIDENTIAL", "RENTAL_ADJUSTMENT");

        return referenceMapping.get(upperName);
    }

    private CoaAccountMapping findMappingByComponent(List<CoaAccountMapping> mappings, String componentCode) {
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }
        return mappings.stream()
                .filter(m -> componentCode.equals(m.getComponentCode()))
                .findFirst()
                .orElse(null);
    }

    private BigDecimal calculateTotalAmount(Map<String, BigDecimal> amountMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : amountMap.values()) {
            total = total.add(amount);
        }
        return total;
    }

    private BigDecimal calculateTotal(List<ClaimLedgerEntry> entries, String drcr) {
        return entries.stream()
                .filter(e -> drcr.equals(e.getDrcr()))
                .map(ClaimLedgerEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

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
                        .claimLedgerAdditional(mapClaimLedgerAddition(entry.getAdditionalDetail()))
                        .build())
                .collect(Collectors.toList());
    }

    private ClaimLedgerAdditionalDetailResponseDTO mapClaimLedgerAddition(ClaimLedgerAdditionalDetail entity) {
        if (entity == null) {
            return null;
        }
        return ClaimLedgerAdditionalDetailResponseDTO
            .builder()
            .accountNumber(entity.getAccountNumber())
            .amount(entity.getAmount())
            .ledgerId(entity.getLedgerEntry().getId())
            .drcr(entity.getDrcr())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedBy(entity.getUpdatedBy())
            .updatedAt(entity.getUpdatedAt())
            .build();
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