package com.claim.claim_processing.application.service.specialCase.impl;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse;
import com.claim.claim_processing.application.service.specialCase.SpecialCaseLedgerService;
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
        String tranCode = "SPC"; // Special Case tran code

        // 3. Build component amounts for special case conversion
        Map<String, BigDecimal> componentAmounts = buildSpecialCaseComponentAmounts(specialCaseResponse);
        log.info("Special Case Components: {}", componentAmounts);

        if (componentAmounts.isEmpty()) {
            throw new RuntimeException("No component amounts found for special case: " + 
                    specialCaseResponse.getId());
        }

        // 4. Calculate totals
        BigDecimal totalEligible = calculateTotalAmount(componentAmounts);
        log.info("Total Eligible Amount: {}", totalEligible);

        // 5. Get final payable amount
        BigDecimal finalPayable = totalEligible;
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
                specialCaseResponse, tranCode, createdBy);
        event = accountingEventRepository.save(event);
        log.info("Accounting Event created with ID: {}", event.getId());

        // 8. Generate Ledger Entries
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // 8a. Process DEBIT mappings (PENSION_CONVERSION and LAPSED_CONVERSION)
        for (CoaAccountMapping mapping : specialCaseMappings) {
            String componentCode = mapping.getComponentCode();

            // Skip BANK - handled separately as CREDIT
            if (COMPONENT_BANK.equals(componentCode)) {
                continue;
            }

            BigDecimal amount = componentAmounts.getOrDefault(componentCode, BigDecimal.ZERO);
            
            // If amount is zero, try to find using alternative component codes
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                amount = findAmountByAlternativeCode(componentCode, componentAmounts);
            }

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
                    .drcr("D") // DEBIT for special case conversion
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
                        .drcr("C")
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
            
            // Log all entries for debugging
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

        // 12. Update event with totals and status
        event.setTotalDr(totalDr);
        event.setTotalCr(totalCr);
        event.setStatus(STATUS_POSTED);
        event.setPostedBy(createdBy);
        event.setPostedAt(LocalDateTime.now());
        event.setUpdatedBy(createdBy);
        event.setUpdatedAt(LocalDateTime.now());
        ClaimAccountingEvent updatedEvent = accountingEventRepository.save(event);

        log.info("========== END: createSpecialCaseLedgerEntries SUCCESS ==========");
        return buildResponse(updatedEvent, savedEntries);
    }

    /**
     * Build component amounts for special case conversion
     * Special case has only TWO components: PENSION_CONVERSION and LAPSED_CONVERSION
     */
    private Map<String, BigDecimal> buildSpecialCaseComponentAmounts(
            GeneralSpecialCaseResponse specialCaseResponse) {
        
        Map<String, BigDecimal> componentMap = new HashMap<>();
        
        log.info("Building component amounts for special case conversion");
        
        // Get the special case detail
        ClaimSpecialCaseResponse specialCaseDetail = specialCaseResponse.getSpecialCaseDetail();
        
        // Initialize amounts
        BigDecimal totalPensionAmount = BigDecimal.ZERO;
        BigDecimal totalLapsedAmount = BigDecimal.ZERO;
        
        // Get Total Pension Amount from ClaimSpecialCaseResponse
        if (specialCaseDetail != null) {
            // Get total pension amount
            if (specialCaseDetail.getTotalPensionAmount() != null) {
                totalPensionAmount = specialCaseDetail.getTotalPensionAmount();
                log.info("Total Pension Amount from specialCaseDetail: {}", totalPensionAmount);
            }
            
            // Get total forfeited/lapsed amount
            if (specialCaseDetail.getTotalForfeitedAmount() != null) {
                totalLapsedAmount = specialCaseDetail.getTotalForfeitedAmount();
                log.info("Total Forfeited Amount from specialCaseDetail: {}", totalLapsedAmount);
            }
        }
        
        // Also check if there's a separate pension amount field in the main response
        if (specialCaseResponse.getSpecialCaseDetail().getTotalPensionAmount() != null && 
            totalPensionAmount.compareTo(BigDecimal.ZERO) == 0) {
            totalPensionAmount = specialCaseResponse.getSpecialCaseDetail().getTotalPensionAmount();
            log.info("Using Total Pension Amount from main response: {}", totalPensionAmount);
        }
        
        // Check for eligible claim amount in specialCaseDetail
        if (specialCaseDetail != null && specialCaseDetail.getEligibleClaimAmount() != null) {
            BigDecimal eligibleAmount = specialCaseDetail.getEligibleClaimAmount();
            if (eligibleAmount.compareTo(BigDecimal.ZERO) > 0) {
                // If eligible amount is set, use it as the total amount
                log.info("Using Eligible Claim Amount: {}", eligibleAmount);
                // Determine which component this belongs to based on case type
                String caseType = specialCaseDetail.getCaseType();
                if (caseType != null) {
                    if (caseType.contains("PENSION") || caseType.contains("CONVERSION")) {
                        totalPensionAmount = eligibleAmount;
                    } else if (caseType.contains("LAPSE") || caseType.contains("FORFEIT")) {
                        totalLapsedAmount = eligibleAmount;
                    } else {
                        // Default to pension if case type unknown
                        totalPensionAmount = eligibleAmount;
                    }
                }
            }
        }
        
        // Check requested amount in specialCaseDetail
        if (specialCaseDetail != null && specialCaseDetail.getRequestedAmount() != null) {
            BigDecimal requestedAmount = specialCaseDetail.getRequestedAmount();
            if (requestedAmount.compareTo(BigDecimal.ZERO) > 0) {
                log.info("Using Requested Amount: {}", requestedAmount);
                // If no other amounts are set, use requested amount
                if (totalPensionAmount.compareTo(BigDecimal.ZERO) == 0 && 
                    totalLapsedAmount.compareTo(BigDecimal.ZERO) == 0) {
                    // Default to pension component
                    totalPensionAmount = requestedAmount;
                }
            }
        }
        
        log.info("Final amounts - Pension: {}, Lapsed: {}", totalPensionAmount, totalLapsedAmount);
        
        // Add Pension Conversion amount
        if (totalPensionAmount.compareTo(BigDecimal.ZERO) > 0) {
            componentMap.put(COMPONENT_PENSION_CONVERSION, totalPensionAmount);
            log.info("Added {}: {}", COMPONENT_PENSION_CONVERSION, totalPensionAmount);
        }
        
        // Add Lapsed Conversion amount
        if (totalLapsedAmount.compareTo(BigDecimal.ZERO) > 0) {
            componentMap.put(COMPONENT_LAPSED_CONVERSION, totalLapsedAmount);
            log.info("Added {}: {}", COMPONENT_LAPSED_CONVERSION, totalLapsedAmount);
        }
        
        // If no amounts found, log warning with all available fields
        if (componentMap.isEmpty()) {
            log.warn("No component amounts found for special case conversion: {}", 
                    specialCaseResponse.getId());
            log.warn("Available fields:");
            log.warn("  totalPensionAmount: {}", specialCaseResponse.getSpecialCaseDetail().getTotalPensionAmount());
            if (specialCaseDetail != null) {
                log.warn("  specialCaseDetail.totalPensionAmount: {}", 
                        specialCaseDetail.getTotalPensionAmount());
                log.warn("  specialCaseDetail.totalForfeitedAmount: {}", 
                        specialCaseDetail.getTotalForfeitedAmount());
                log.warn("  specialCaseDetail.eligibleClaimAmount: {}", 
                        specialCaseDetail.getEligibleClaimAmount());
                log.warn("  specialCaseDetail.requestedAmount: {}", 
                        specialCaseDetail.getRequestedAmount());
                log.warn("  specialCaseDetail.caseType: {}", 
                        specialCaseDetail.getCaseType());
            }
        }
        
        log.info("Final Component Map: {}", componentMap);
        return componentMap;
    }

    /**
     * Find amount by alternative component code
     */
    private BigDecimal findAmountByAlternativeCode(String componentCode, Map<String, BigDecimal> componentAmounts) {
        // Map of alternative codes
        Map<String, String> alternativeMap = new HashMap<>();
        alternativeMap.put(COMPONENT_PENSION_CONVERSION, "PENSION_LUMP_SUM");
        alternativeMap.put(COMPONENT_PENSION_CONVERSION, "PENSION_REFUND");
        alternativeMap.put(COMPONENT_PENSION_CONVERSION, "PF_MC_REFUND");
        alternativeMap.put(COMPONENT_LAPSED_CONVERSION, "LAPSE_REFUND");
        alternativeMap.put(COMPONENT_LAPSED_CONVERSION, "FORFEITED_REFUND");
        alternativeMap.put(COMPONENT_LAPSED_CONVERSION, "LAPSE");
        
        for (Map.Entry<String, String> entry : alternativeMap.entrySet()) {
            if (componentCode.equals(entry.getKey()) && componentAmounts.containsKey(entry.getValue())) {
                BigDecimal amount = componentAmounts.get(entry.getValue());
                log.debug("Found amount {} for {} using alternative code {}", amount, componentCode, entry.getValue());
                return amount;
            }
        }
        
        // Check if component code exists directly
        if (componentAmounts.containsKey(componentCode)) {
            return componentAmounts.get(componentCode);
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Create Accounting Event for Special Case
     */
    private ClaimAccountingEvent createSpecialCaseAccountingEvent(
            GeneralSpecialCaseResponse specialCaseResponse, 
            String tranCode,
            String createdBy) {
        
        LocalDateTime now = LocalDateTime.now();
        String specialCaseReference = "SPC-" + specialCaseResponse.getId();

        MemberDetail memberDetail = memberDetailRepository.findByNppfNumber(
                specialCaseResponse.getNppfNumber())
                .orElseThrow(() -> new RuntimeException(
                        "Member details not found for NPPF number: " + 
                        specialCaseResponse.getNppfNumber()));

        return ClaimAccountingEvent.builder()
                .eventType(EVENT_TYPE_SPECIAL_CASE)
                .claimDetailId(specialCaseResponse.getId())
                .nppfNumber(specialCaseResponse.getNppfNumber())
                .identityNumber(memberDetail.getIdentityNumber())
                .memberName(buildMemberName(memberDetail))
                .agencyCategoryId(specialCaseResponse.getMemberCategoryId())
                .agencyCode(specialCaseResponse.getAgencyCode())
                .agencyName(specialCaseResponse.getAgencyCode())
                .claimTypeId(specialCaseResponse.getClaimTypeId())
                .claimTypeName(specialCaseResponse.getClaimTypeName())
                .claimApplicationNumber(specialCaseReference)
                .monthName(now.getMonth().name())
                .year(String.valueOf(now.getYear()))
                .accountingYear(String.valueOf(now.getYear()))
                .tranCode(tranCode)
                .status(STATUS_PENDING)
                .totalDr(BigDecimal.ZERO)
                .totalCr(BigDecimal.ZERO)
                .narration("Special Case Conversion: " + specialCaseReference + " — " + 
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
    public AccountingEventResponseDto getAccountingEventBySpecialCaseId(Long specialCaseId) {
        ClaimAccountingEvent event = accountingEventRepository.findByClaimDetailId(specialCaseId)
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