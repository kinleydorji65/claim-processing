package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse;
import com.claim.claim_processing.application.service.application.SpecialCaseLedgerService;
import com.claim.claim_processing.common.entities.claim.ClaimAccountingEvent;
import com.claim.claim_processing.common.entities.claim.ClaimLedgerEntry;
import com.claim.claim_processing.common.entities.common.CoaAccountMapping;
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
        String tranCode = "RPFC"; // Default, can be mapped based on agency

        // 3. Build component amounts for special case
        Map<String, BigDecimal> componentAmounts = buildSpecialCaseComponentAmounts(specialCaseResponse);
        log.info("Special Case Components: {}", componentAmounts);

        // 4. Calculate totals
        BigDecimal totalEligible = calculateTotalAmount(componentAmounts);
        log.info("Total Eligible Amount: {}", totalEligible);

        // 5. Get final payable amount
        BigDecimal finalPayable = totalEligible; // In special case, all eligible goes to member
        log.info("Final Payable Amount: {}", finalPayable);

        // 6. Get COA Mappings for SPECIAL_CASE
        String eventType = EVENT_TYPE_SPECIAL_CASE;
        List<CoaAccountMapping> specialCaseMappings = coaAccountMappingRepository
                .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                        eventType, agencyCategoryId);

        if (specialCaseMappings.isEmpty()) {
            log.warn("No SPECIAL_CASE mappings found, falling back to REFUND");
            specialCaseMappings = coaAccountMappingRepository
                    .findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
                            "REFUND", agencyCategoryId);
        }

        log.info("SPECIAL_CASE Mappings found: {}", specialCaseMappings.size());

        // 7. Create and SAVE Accounting Event
        ClaimAccountingEvent event = createSpecialCaseAccountingEvent(
                specialCaseResponse, tranCode, createdBy);
        event = accountingEventRepository.save(event);
        log.info("Accounting Event created with ID: {}", event.getId());

        // 8. Generate Ledger Entries
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // 8a. Process DEBIT mappings (All refund components)
        for (CoaAccountMapping mapping : specialCaseMappings) {
            String componentCode = mapping.getComponentCode();

            // Skip BANK - handled separately
            if ("BANK".equals(componentCode)) {
                continue;
            }

            BigDecimal amount = componentAmounts.getOrDefault(componentCode, BigDecimal.ZERO);
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
                    .drcr("D") // DEBIT for special case refund
                    .amount(amount)
                    .entryRole(mapping.getEntryRole())
                    .componentCode(componentCode)
                    .narration("Special Case: " + specialCaseReference + " — " + componentCode)
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
            CoaAccountMapping bankMapping = findMappingByComponent(specialCaseMappings, "BANK");
            if (bankMapping != null) {
                seqNo++;
                ClaimLedgerEntry entry = ClaimLedgerEntry.builder()
                        .accountingEventId(event.getId())
                        .seqNo(seqNo)
                        .mainAccountCode(bankMapping.getMainAccountCode())
                        .subAccountCode(bankMapping.getSubAccountCode())
                        .drcr("C")
                        .amount(finalPayable)
                        .entryRole("BANK")
                        .componentCode("BANK")
                        .narration("Special Case: " + specialCaseReference + " — Net Payment to Member")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(entry);
                log.info("CREDIT Entry: SEQ={}, BANK, Amount={}", seqNo, finalPayable);
            } else {
                log.warn("BANK mapping not found for special case: {}", agencyCategoryId);
            }
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
     * Build component amounts for special case
     */
    private Map<String, BigDecimal> buildSpecialCaseComponentAmounts(
            GeneralSpecialCaseResponse specialCaseResponse) {
        
        Map<String, BigDecimal> componentMap = new HashMap<>();
        
        ClaimSpecialCaseResponse specialCaseDetail = specialCaseResponse.getSpecialCaseDetail();
        if (specialCaseDetail == null) {
            log.warn("No special case detail found");
            return componentMap;
        }

        // 1. Forfeited Amount Refund
        if (specialCaseDetail.getTotalForfeitedAmount() != null && 
            specialCaseDetail.getTotalForfeitedAmount().compareTo(BigDecimal.ZERO) > 0) {
            
            componentMap.put("LAPSE_REFUND", specialCaseDetail.getTotalForfeitedAmount());
            log.info("Lapse Refund Amount: {}", specialCaseDetail.getTotalForfeitedAmount());
        }

        // 2. PF Components (if part of special case)
        // These would come from the special case details or reserve accounts
        if (specialCaseDetail.getEligibleClaimAmount() != null) {
            // For simplicity, if there's an eligible claim amount, treat as PF refund
            // In real scenario, you'd have more granular data
            BigDecimal pfAmount = specialCaseDetail.getEligibleClaimAmount();
            componentMap.put("PF_MC_REFUND", pfAmount);
            log.info("PF Refund Amount: {}", pfAmount);
        }

        // 3. Pension as Lump Sum
        if (specialCaseDetail.getTotalPensionAmount() != null && 
            specialCaseDetail.getTotalPensionAmount().compareTo(BigDecimal.ZERO) > 0) {
            
            componentMap.put("PENSION_LUMP_SUM", specialCaseDetail.getTotalPensionAmount());
            log.info("Pension Lump Sum Amount: {}", specialCaseDetail.getTotalPensionAmount());
        }

        log.info("Special Case Component Map: {}", componentMap);
        return componentMap;
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
                .narration("Special Case: " + specialCaseReference + " — " + 
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
