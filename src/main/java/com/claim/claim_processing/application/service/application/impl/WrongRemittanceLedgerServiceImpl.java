package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO.WrongRemittanceCalculationComponentResponseDTO;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO.WrongRemittanceForfeitedResponseDTO;
import com.claim.claim_processing.application.entity.claimDetail.ClaimAccountingEvent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerEntry;
import com.claim.claim_processing.application.repository.claimDetail.ClaimAccountingEventRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimLedgerEntryRepository;
import com.claim.claim_processing.application.service.application.WrongRemittanceLedgerService;
import com.claim.claim_processing.common.entities.common.CoaAccountMapping;
import com.claim.claim_processing.common.entities.common.CoaSubAccount;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.common.CoaAccountMappingRepository;
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
public class WrongRemittanceLedgerServiceImpl implements WrongRemittanceLedgerService {

    private final ClaimAccountingEventRepository accountingEventRepository;
    private final ClaimLedgerEntryRepository ledgerEntryRepository;
    private final CoaAccountMappingRepository coaAccountMappingRepository;
    private final CoaSubAccountRepository coaSubAccountRepository;
    private final MemberDetailRepository memberDetailRepository;

    private static final String EVENT_TYPE_REFUND = "REFUND";
    private static final String EVENT_TYPE_LAPSE = "LAPSE";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_POSTED = "POSTED";

    @Override
    @Transactional
    public AccountingEventResponseDto createLedgerEntriesForWrongRemittance(
            GeneralClaimDetailResponse claimDetailResponse, 
            String createdBy) {
        
        log.info("========== START: createLedgerEntriesForWrongRemittance ==========");
        log.info("Claim ID: {}, Application Number: {}", 
                claimDetailResponse.getId(), claimDetailResponse.getApplicationNumber());

        // Get list of wrong remittances from the response
        List<WrongRemitanceResponseDTO> wrongRemittances = claimDetailResponse.getWrongRemitances();
        
        if (wrongRemittances == null || wrongRemittances.isEmpty()) {
            log.warn("No wrong remittances found for claim: {}", claimDetailResponse.getId());
            throw new RuntimeException("No wrong remittances found for claim: " + claimDetailResponse.getId());
        }

        log.info("Processing {} wrong remittance records", wrongRemittances.size());

        // ============================================================
        // STEP 1: AGGREGATE ALL COMPONENTS AND FORFEITED AMOUNTS
        // FROM ALL WRONG REMITTANCES
        // ============================================================
        Map<String, BigDecimal> totalComponentAmounts = new HashMap<>();
        Map<String, BigDecimal> totalForfeitedAmounts = new HashMap<>();
        BigDecimal totalEligible = BigDecimal.ZERO;
        BigDecimal totalForfeited = BigDecimal.ZERO;
        
        // Get agency category from claim detail response
        String agencyCategoryId = claimDetailResponse.getMemberCategoryId() != null 
                ? claimDetailResponse.getMemberCategoryId() 
                : "01";
        
        log.info("Agency Category: {}", agencyCategoryId);

        // Process each wrong remittance
        for (WrongRemitanceResponseDTO wrongRemittance : wrongRemittances) {
            log.info("Processing wrong remittance ID: {}, NPPF: {}, Member: {}", 
                    wrongRemittance.getId(), 
                    wrongRemittance.getNppfNumber(), 
                    wrongRemittance.getMemberName());

            // ===== BUILD COMPONENT AMOUNTS FOR THIS WRONG REMITTANCE =====
            Map<String, BigDecimal> componentAmounts = buildGroupedComponentAmounts(wrongRemittance);
            log.info("  Components for WR {}: {}", wrongRemittance.getId(), componentAmounts);
            
            // ===== BUILD FORFEITED AMOUNTS FOR THIS WRONG REMITTANCE =====
            Map<String, BigDecimal> forfeitedAmounts = buildForfeitedAmounts(wrongRemittance);
            log.info("  Forfeited for WR {}: {}", wrongRemittance.getId(), forfeitedAmounts);
            
            // ===== AGGREGATE COMPONENT AMOUNTS =====
            // Sum PF_MC from all members into one total
            for (Map.Entry<String, BigDecimal> entry : componentAmounts.entrySet()) {
                String componentCode = entry.getKey();
                BigDecimal amount = entry.getValue();
                BigDecimal newTotal = totalComponentAmounts.getOrDefault(componentCode, BigDecimal.ZERO).add(amount);
                totalComponentAmounts.put(componentCode, newTotal);
                log.info("  Aggregated {}: {} + {} = {}", 
                        componentCode, 
                        totalComponentAmounts.getOrDefault(componentCode, BigDecimal.ZERO).subtract(amount), 
                        amount, 
                        newTotal);
            }
            
            // ===== AGGREGATE FORFEITED AMOUNTS =====
            for (Map.Entry<String, BigDecimal> entry : forfeitedAmounts.entrySet()) {
                String componentCode = entry.getKey();
                BigDecimal amount = entry.getValue();
                BigDecimal newTotal = totalForfeitedAmounts.getOrDefault(componentCode, BigDecimal.ZERO).add(amount);
                totalForfeitedAmounts.put(componentCode, newTotal);
                log.info("  Aggregated Forfeited {}: {} + {} = {}", 
                        componentCode, 
                        totalForfeitedAmounts.getOrDefault(componentCode, BigDecimal.ZERO).subtract(amount), 
                        amount, 
                        newTotal);
            }
            
            totalEligible = totalEligible.add(calculateTotalAmount(componentAmounts));
            totalForfeited = totalForfeited.add(calculateTotalAmount(forfeitedAmounts));
        }

        log.info("=== AGGREGATED TOTALS ===");
        log.info("Total Component Amounts: {}", totalComponentAmounts);
        log.info("Total Forfeited Amounts: {}", totalForfeitedAmounts);
        log.info("Total Eligible: {}, Total Forfeited: {}", totalEligible, totalForfeited);

        // ============================================================
        // STEP 2: CALCULATE FINAL PAYABLE
        // ============================================================
        BigDecimal finalPayable = totalEligible.subtract(totalForfeited).max(BigDecimal.ZERO);
        log.info("Final Payable (Aggregated): {}", finalPayable);

        // ============================================================
        // STEP 3: GET COA MAPPINGS
        // ============================================================
        List<CoaAccountMapping> refundMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_REFUND, agencyCategoryId);

        if (refundMappings.isEmpty()) {
            log.warn("No REFUND mappings found for agency: {}", agencyCategoryId);
            throw new RuntimeException("No REFUND mappings found for agency: " + agencyCategoryId);
        }

        List<CoaAccountMapping> lapseMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        EVENT_TYPE_LAPSE, agencyCategoryId);

        log.info("REFUND Mappings: {}, LAPSE Mappings: {}", 
                refundMappings.size(), lapseMappings != null ? lapseMappings.size() : 0);

        // ============================================================
        // STEP 4: CREATE SINGLE ACCOUNTING EVENT
        // ============================================================
        ClaimAccountingEvent event = createAccountingEvent(claimDetailResponse, agencyCategoryId, createdBy);
        event = accountingEventRepository.saveAndFlush(event);
        log.info("Accounting Event created with ID: {}", event.getId());

        // ============================================================
        // STEP 5: GENERATE LEDGER ENTRIES (USING AGGREGATED AMOUNTS)
        // ============================================================
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // ===== 5a. DEBIT Entries - Components (Aggregated) =====
        log.info("========== Processing DEBIT Entries (Aggregated) ==========");
        for (CoaAccountMapping mapping : refundMappings) {
            String componentCode = mapping.getComponentCode();
            
            if ("BANK".equals(componentCode) || "LAPSE".equals(componentCode)) {
                log.info("Skipping BANK/LAPSE mapping: {}", componentCode);
                continue;
            }

            // Get the aggregated amount for this component from ALL wrong remittances
            BigDecimal amount = totalComponentAmounts.getOrDefault(componentCode, BigDecimal.ZERO);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Skipping zero amount for: {}", componentCode);
                continue;
            }

            CoaSubAccount subAccount = coaSubAccountRepository
                    .findBySubAccountCode(mapping.getSubAccountCode())
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
                    .narration(subAccount.getSubAccountName() + " - " + componentCode + 
                              " (Aggregated from " + wrongRemittances.size() + " wrong remittances)")
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entry);
            log.info("✅ DEBIT: SEQ={}, Component={}, Amount={} (Aggregated from {} records)", 
                    seqNo, componentCode, amount, wrongRemittances.size());
        }

        // ===== 5b. CREDIT - LAPSE Entries (Aggregated) =====
        log.info("========== Processing LAPSE Entries (Aggregated) ==========");
        CoaAccountMapping lapseMapping = (lapseMappings != null && !lapseMappings.isEmpty()) 
                ? lapseMappings.get(0) : null;

        for (Map.Entry<String, BigDecimal> entry : totalForfeitedAmounts.entrySet()) {
            String componentCode = entry.getKey();
            BigDecimal amount = entry.getValue();

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
                        .drcr(lapseMapping.getDrcr())
                        .amount(amount)
                        .entryRole("LAPSE")
                        .componentCode(componentCode)
                        .narration("LAPSE - " + componentCode + " - " + subAccount.getSubAccountName() + 
                                  " (Aggregated from " + wrongRemittances.size() + " wrong remittances)")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(ledgerEntry);
                log.info("✅ LAPSE CREDIT: SEQ={}, Component={}, Amount={} (Aggregated)", 
                        seqNo, componentCode, amount);
            } else {
                log.warn("⚠️ No LAPSE mapping for: {}", componentCode);
            }
        }

        // ===== 5c. CREDIT - CONTRIBUTION_POSTING Entries (Aggregated) =====
        if (finalPayable.compareTo(BigDecimal.ZERO) > 0) {
            log.info("Processing CONTRIBUTION_POSTING entries for aggregated final payable: {}", finalPayable);
            seqNo = createContributionPostingEntries(
                    event, ledgerEntries, finalPayable, 
                    agencyCategoryId, totalComponentAmounts, seqNo, createdBy, 
                    wrongRemittances.size());
        }

        // ============================================================
        // STEP 6: SAVE ALL ENTRIES
        // ============================================================
        log.info("Total ledger entries to save: {}", ledgerEntries.size());
        List<ClaimLedgerEntry> savedEntries = ledgerEntryRepository.saveAll(ledgerEntries);

        // ============================================================
        // STEP 7: CALCULATE AND VALIDATE TOTALS
        // ============================================================
        BigDecimal totalDr = calculateTotal(savedEntries, "D");
        BigDecimal totalCr = calculateTotal(savedEntries, "C");
        log.info("=== LEDGER TOTALS ===");
        log.info("Total DR: {}", totalDr);
        log.info("Total CR: {}", totalCr);

        log.info("=== LEDGER ENTRIES SUMMARY ===");
        for (ClaimLedgerEntry entry : savedEntries) {
            log.info("SEQ: {}, Component: {}, DR/CR: {}, Amount: {}", 
                    entry.getSeqNo(), entry.getComponentCode(), entry.getDrcr(), entry.getAmount());
        }

        if (totalDr.compareTo(totalCr) != 0) {
            log.error("❌ LEDGER NOT BALANCED! DR: {}, CR: {}, Diff: {}", 
                    totalDr, totalCr, totalDr.subtract(totalCr));
            throw new RuntimeException("Ledger entries do not balance! DR: " + totalDr + ", CR: " + totalCr);
        }
        log.info("✅ LEDGER BALANCED! DR: {}, CR: {}", totalDr, totalCr);

        // ============================================================
        // STEP 8: UPDATE EVENT
        // ============================================================
        event.setStatus(STATUS_POSTED);
        event.setPostedBy(createdBy);
        event.setPostedAt(LocalDateTime.now());
        ClaimAccountingEvent updatedEvent = accountingEventRepository.save(event);

        log.info("========== END: SUCCESS ==========");
        return buildResponse(updatedEvent, savedEntries);
    }

    /**
     * Build GROUPED component amounts from a single wrong remittance
     */
    private Map<String, BigDecimal> buildGroupedComponentAmounts(WrongRemitanceResponseDTO response) {
        Map<String, BigDecimal> componentMap = new HashMap<>();

        List<WrongRemittanceCalculationComponentResponseDTO> components = response.getComponents();

        if (components == null || components.isEmpty()) {
            return componentMap;
        }

        // Group by original component code
        Map<String, BigDecimal> rawGrouped = new HashMap<>();
        for (WrongRemittanceCalculationComponentResponseDTO component : components) {
            String code = component.getComponentCode();
            BigDecimal amount = component.getAmount() != null ? component.getAmount() : BigDecimal.ZERO;

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            rawGrouped.merge(code, amount, BigDecimal::add);
        }

        // Map to COA codes
        for (Map.Entry<String, BigDecimal> entry : rawGrouped.entrySet()) {
            String code = entry.getKey();
            BigDecimal amount = entry.getValue();

            String mappedCode = mapComponentToCoaCode(code);
            if (mappedCode != null) {
                componentMap.merge(mappedCode, amount, BigDecimal::add);
            }
        }

        return componentMap;
    }

    /**
     * Build forfeited amounts from a single wrong remittance
     */
    private Map<String, BigDecimal> buildForfeitedAmounts(WrongRemitanceResponseDTO response) {
        Map<String, BigDecimal> forfeitedMap = new HashMap<>();

        List<WrongRemittanceForfeitedResponseDTO> forfeitedComponents = 
                response.getWrongRemitanceForfeiteds();

        if (forfeitedComponents == null || forfeitedComponents.isEmpty()) {
            return forfeitedMap;
        }

        BigDecimal totalForfeited = BigDecimal.ZERO;
        
        for (WrongRemittanceForfeitedResponseDTO forfeited : forfeitedComponents) {
            BigDecimal amount = forfeited.getAmount() != null ? forfeited.getAmount() : BigDecimal.ZERO;
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            totalForfeited = totalForfeited.add(amount);
        }

        if (totalForfeited.compareTo(BigDecimal.ZERO) > 0) {
            forfeitedMap.put("LAPSE", totalForfeited);
        }

        return forfeitedMap;
    }

    /**
     * Create CONTRIBUTION_POSTING entries with proportional distribution
     */
    private int createContributionPostingEntries(
            ClaimAccountingEvent event,
            List<ClaimLedgerEntry> ledgerEntries,
            BigDecimal finalPayable,
            String agencyCategoryId,
            Map<String, BigDecimal> componentAmounts,
            int seqNo,
            String createdBy,
            int recordCount) {
        
        log.info("========== Creating CONTRIBUTION_POSTING entries ==========");
        log.info("Final Payable: {}", finalPayable);

        // Get CONTRIBUTION_POSTING mappings
        List<CoaAccountMapping> contributionMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        "CONTRIBUTION_POSTING", agencyCategoryId);

        if (contributionMappings.isEmpty()) {
            log.warn("No CONTRIBUTION_POSTING mappings, using REFUND as fallback");
            contributionMappings = coaAccountMappingRepository
                    .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                            EVENT_TYPE_REFUND, agencyCategoryId);
        }

        if (contributionMappings.isEmpty()) {
            log.warn("No mappings found, using fallback entry");
            return createFallbackEntry(event, ledgerEntries, finalPayable, seqNo, createdBy);
        }

        // Get active components with amounts and mappings
        List<Map.Entry<String, BigDecimal>> activeComponents = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : componentAmounts.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            boolean hasMapping = contributionMappings.stream()
                    .anyMatch(m -> entry.getKey().equals(m.getComponentCode()));
            if (hasMapping) {
                activeComponents.add(entry);
            } else {
                log.warn("No mapping for component: {}", entry.getKey());
            }
        }

        if (activeComponents.isEmpty()) {
            log.warn("No active components, using fallback");
            return createFallbackEntry(event, ledgerEntries, finalPayable, seqNo, createdBy);
        }

        // Calculate total active amounts and ratio
        BigDecimal totalActiveAmounts = activeComponents.stream()
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalActiveAmounts.compareTo(BigDecimal.ZERO) <= 0) {
            return createFallbackEntry(event, ledgerEntries, finalPayable, seqNo, createdBy);
        }

        BigDecimal ratio = finalPayable.divide(totalActiveAmounts, 10, java.math.RoundingMode.HALF_UP);
        log.info("Total Active: {}, Ratio: {}", totalActiveAmounts, ratio);

        BigDecimal totalDistributed = BigDecimal.ZERO;
        List<ClaimLedgerEntry> createdEntries = new ArrayList<>();

        // Distribute proportionally
        for (Map.Entry<String, BigDecimal> entry : activeComponents) {
            String componentCode = entry.getKey();
            BigDecimal componentAmount = entry.getValue();

            CoaAccountMapping mapping = contributionMappings.stream()
                    .filter(m -> componentCode.equals(m.getComponentCode()))
                    .findFirst()
                    .orElse(null);

            if (mapping == null) {
                continue;
            }

            BigDecimal proportionalAmount = componentAmount.multiply(ratio)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            if (proportionalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            CoaSubAccount subAccount = coaSubAccountRepository
                    .findBySubAccountCode(mapping.getSubAccountCode())
                    .orElse(null);

            seqNo++;
            ClaimLedgerEntry entryToAdd = ClaimLedgerEntry.builder()
                    .accountingEventId(event.getId())
                    .seqNo(seqNo)
                    .mainAccountCode(mapping.getMainAccountCode())
                    .subAccountCode(mapping.getSubAccountCode())
                    .drcr("C")
                    .amount(proportionalAmount)
                    .entryRole("CONTRIBUTION_POSTING")
                    .componentCode(componentCode)
                    .narration("CONTRIBUTION_POSTING - " + componentCode + 
                               " - " + (subAccount != null ? subAccount.getSubAccountName() : "") +
                               " - Amount: " + proportionalAmount + 
                               " (Aggregated from " + recordCount + " wrong remittances)")
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entryToAdd);
            createdEntries.add(entryToAdd);

            log.info("✅ CREDIT: SEQ={}, Component={}, Amount={} (Aggregated)", 
                    seqNo, componentCode, proportionalAmount);
            totalDistributed = totalDistributed.add(proportionalAmount);
        }

        // Handle rounding adjustment
        BigDecimal difference = finalPayable.subtract(totalDistributed);
        log.info("Distributed: {}, Final: {}, Difference: {}", totalDistributed, finalPayable, difference);

        if (difference.compareTo(BigDecimal.ZERO) != 0 && !createdEntries.isEmpty()) {
            // Adjust the last entry
            ClaimLedgerEntry lastEntry = createdEntries.get(createdEntries.size() - 1);
            BigDecimal adjustedAmount = lastEntry.getAmount().add(difference)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            if (adjustedAmount.compareTo(BigDecimal.ZERO) > 0) {
                ledgerEntries.remove(lastEntry);
                
                CoaAccountMapping mapping = contributionMappings.stream()
                        .filter(m -> lastEntry.getComponentCode().equals(m.getComponentCode()))
                        .findFirst()
                        .orElse(null);

                if (mapping != null) {
                    CoaSubAccount subAccount = coaSubAccountRepository
                            .findBySubAccountCode(mapping.getSubAccountCode())
                            .orElse(null);

                    ClaimLedgerEntry adjustedEntry = ClaimLedgerEntry.builder()
                            .accountingEventId(event.getId())
                            .seqNo(lastEntry.getSeqNo())
                            .mainAccountCode(mapping.getMainAccountCode())
                            .subAccountCode(mapping.getSubAccountCode())
                            .drcr("C")
                            .amount(adjustedAmount)
                            .entryRole("CONTRIBUTION_POSTING")
                            .componentCode(lastEntry.getComponentCode())
                            .narration("CONTRIBUTION_POSTING - " + lastEntry.getComponentCode() + 
                                       " (Adjusted) - " + 
                                       (subAccount != null ? subAccount.getSubAccountName() : "") +
                                       " - Amount: " + adjustedAmount + 
                                       " (Aggregated from " + recordCount + " wrong remittances)")
                            .createdBy(createdBy)
                            .createdAt(LocalDateTime.now())
                            .build();
                    ledgerEntries.add(adjustedEntry);
                    log.info("✅ Adjusted entry: SEQ={}, Old={}, New={}", 
                            lastEntry.getSeqNo(), lastEntry.getAmount(), adjustedAmount);
                }
            }
        }

        log.info("CONTRIBUTION_POSTING entries created");
        return seqNo;
    }

    /**
     * Fallback entry creation
     */
    private int createFallbackEntry(ClaimAccountingEvent event,
                                     List<ClaimLedgerEntry> ledgerEntries,
                                     BigDecimal finalPayable,
                                     int seqNo,
                                     String createdBy) {
        
        log.warn("Using fallback entry for: {}", finalPayable);
        
        seqNo++;
        ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                .accountingEventId(event.getId())
                .seqNo(seqNo)
                .mainAccountCode("15020100")
                .subAccountCode("15020109")
                .drcr("C")
                .amount(finalPayable)
                .entryRole("CONTRIBUTION_POSTING")
                .componentCode("WRONG_REMITTANCE")
                .narration("WRONG_REMITTANCE - Fallback - Amount: " + finalPayable + " (Aggregated)")
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
        ledgerEntries.add(entry);
        log.info("Fallback Entry: SEQ={}, Amount={}", seqNo, finalPayable);
        
        return seqNo;
    }

    /**
     * Map component code to COA component code
     */
    private String mapComponentToCoaCode(String componentCode) {
        if (componentCode == null) return null;

        String cleanCode = componentCode.toUpperCase().trim();
        cleanCode = cleanCode.replace("_CUMULATIVE", "")
                .replace("_CUM", "")
                .replace("_YEAR", "")
                .replace("_Y", "");

        // PF Components
        if (cleanCode.startsWith("PF_")) {
            return cleanCode;
        }

        // Pension Components
        if (cleanCode.startsWith("P_") || cleanCode.startsWith("PC_")) {
            return "P_" + cleanCode.substring(cleanCode.indexOf("_") + 1);
        }

        // Gratuity Components
        if (cleanCode.startsWith("G_") || cleanCode.startsWith("GRAT_")) {
            return "G_" + cleanCode.substring(cleanCode.indexOf("_") + 1);
        }

        // Voluntary Components
        if (cleanCode.startsWith("V_") || cleanCode.startsWith("VOL_")) {
            return "V_" + cleanCode.substring(cleanCode.indexOf("_") + 1);
        }

        log.warn("No mapping for: {}", componentCode);
        return null;
    }

    private BigDecimal calculateTotalAmount(Map<String, BigDecimal> amountMap) {
        return amountMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotal(List<ClaimLedgerEntry> entries, String drcr) {
        return entries.stream()
                .filter(e -> drcr.equals(e.getDrcr()))
                .map(ClaimLedgerEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ClaimAccountingEvent createAccountingEvent(
            GeneralClaimDetailResponse claimDetailResponse,
            String agencyCategoryId,
            String createdBy) {
        
        LocalDateTime now = LocalDateTime.now();

        // Get member details
        MemberDetail memberDetail = memberDetailRepository
                .findByNppfNumber(claimDetailResponse.getNppfNumber())
                .orElse(null);
        
        String memberName = null;
        String identityNumber = claimDetailResponse.getIdentityNumber();
        
        if (memberDetail != null) {
            identityNumber = memberDetail.getIdentityNumber();
            memberName = buildMemberName(memberDetail);
        }

        return ClaimAccountingEvent.builder()
                .eventType(EVENT_TYPE_REFUND)
                .nppfNumber(claimDetailResponse.getNppfNumber())
                .identityNumber(identityNumber)
                .memberName(memberName != null ? memberName : claimDetailResponse.getNppfNumber())
                .agencyCategoryId(agencyCategoryId)
                .agencyCode(claimDetailResponse.getAgencyCode())
                .agencyName(claimDetailResponse.getAgencyCode())
                .claimApplicationNumber(claimDetailResponse.getApplicationNumber())
                .monthName(now.getMonth().name())
                .year(String.valueOf(now.getYear()))
                .accountingYear(String.valueOf(now.getYear()))
                .status(STATUS_PENDING)
                .createdBy(createdBy)
                .createdAt(now)
                .build();
    }

    private String buildMemberName(MemberDetail memberDetail) {
        StringBuilder name = new StringBuilder();
        if (memberDetail.getFirstName() != null) {
            name.append(memberDetail.getFirstName());
        }
        if (memberDetail.getMiddleName() != null) {
            if (name.length() > 0) name.append(" ");
            name.append(memberDetail.getMiddleName());
        }
        if (memberDetail.getLastName() != null) {
            if (name.length() > 0) name.append(" ");
            name.append(memberDetail.getLastName());
        }
        return name.toString();
    }

    private AccountingEventResponseDto buildResponse(ClaimAccountingEvent event, List<ClaimLedgerEntry> entries) {
        Map<String, String> subAccountNames = new HashMap<>();
        for (ClaimLedgerEntry entry : entries) {
            if (entry.getSubAccountCode() != null) {
                coaSubAccountRepository.findBySubAccountCode(entry.getSubAccountCode())
                        .ifPresent(acc -> subAccountNames.put(entry.getSubAccountCode(), acc.getSubAccountName()));
            }
        }

        List<LedgerEntryResponseDto> entryDtos = entries.stream()
                .map(entry -> LedgerEntryResponseDto.builder()
                        .id(entry.getId())
                        .seqNo(entry.getSeqNo())
                        .mainAccountCode(entry.getMainAccountCode())
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
}