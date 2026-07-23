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
import java.util.*;
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

        // 3. Build component amounts from special case components
        Map<String, BigDecimal> componentAmounts = buildComponentAmounts(specialCaseResponse);
        log.info("Component Amounts: {}", componentAmounts);

        if (componentAmounts.isEmpty()) {
            throw new RuntimeException("No component amounts found for special case: " + 
                    specialCaseResponse.getId());
        }

        // 4. Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(componentAmounts);
        log.info("Total Amount: {}", totalAmount);

        // 5. Get COA Mappings for SPECIAL_CASE
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

        // 6. Create and SAVE Accounting Event
        ClaimAccountingEvent event = createSpecialCaseAccountingEvent(
                specialCaseResponse, createdBy);
        event = accountingEventRepository.save(event);
        log.info("Accounting Event created with ID: {}", event.getId());

        // 7. Generate Ledger Entries
        List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();
        int seqNo = 0;

        // 7a. Process DEBIT mappings for each component
        log.info("========== Processing DEBIT Entries ==========");
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
                    .drcr(mapping.getDrcr())
                    .amount(amount)
                    .entryRole(mapping.getEntryRole())
                    .componentCode(componentCode)
                    .narration(subAccount.getSubAccountName())
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .build();
            ledgerEntries.add(entry);
            log.info("✅ DEBIT Entry: SEQ={}, Component={}, Amount={}, Account={}/{}",
                    seqNo, componentCode, amount, 
                    mapping.getMainAccountCode(), mapping.getSubAccountCode());
        }

        // 7b. BANK Entry (CREDIT) - Total amount
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.info("========== Processing BANK Entry ==========");
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
                        .amount(totalAmount)
                        .entryRole("BANK")
                        .componentCode(COMPONENT_BANK)
                        .narration(subAccount.getSubAccountName())
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();
                ledgerEntries.add(entry);
                log.info("✅ CREDIT Entry: SEQ={}, BANK, Amount={}", seqNo, totalAmount);
            } else {
                log.warn("BANK mapping not found for special case: {}", agencyCategoryId);
            }
        } else {
            log.warn("Total amount is zero, skipping BANK entry");
        }

        if (ledgerEntries.isEmpty()) {
            throw new RuntimeException("No ledger entries were created for special case: " + 
                    specialCaseResponse.getId());
        }

        log.info("Total ledger entries to save: {}", ledgerEntries.size());

        // 8. Save all ledger entries
        List<ClaimLedgerEntry> savedEntries = ledgerEntryRepository.saveAll(ledgerEntries);
        log.info("{} ledger entries saved for special case: {}", savedEntries.size(), specialCaseReference);

        // 9. Calculate totals
        BigDecimal totalDr = calculateTotal(savedEntries, "D");
        BigDecimal totalCr = calculateTotal(savedEntries, "C");
        log.info("Total DR: {}, Total CR: {}", totalDr, totalCr);

        // 10. Validate balance
        if (totalDr.compareTo(totalCr) != 0) {
            log.error("❌ LEDGER NOT BALANCED! DR: {}, CR: {}, Difference: {}",
                    totalDr, totalCr, totalDr.subtract(totalCr));
            
            throw new RuntimeException(
                    "Ledger entries do not balance! Total DR: " + totalDr +
                            ", Total CR: " + totalCr +
                            ", Difference: " + totalDr.subtract(totalCr));
        } else {
            log.info("✅ LEDGER BALANCED! DR: {}, CR: {}", totalDr, totalCr);
        }

        // 11. Update event status
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
    // BUILD COMPONENT AMOUNTS
    // =============================================
    
    /**
     * Build component amounts from special case response
     * Each component is mapped individually with its own code
     */
    private Map<String, BigDecimal> buildComponentAmounts(GeneralSpecialCaseResponse specialCaseResponse) {
        Map<String, BigDecimal> componentMap = new HashMap<>();
        
        ClaimSpecialCaseResponse specialCaseDetail = specialCaseResponse.getSpecialCaseDetail();
        if (specialCaseDetail == null) {
            log.warn("Special case detail is null");
            return componentMap;
        }

        // Get components from specialCaseDetail
        List<SpecialCaseComponentBalanceResponseDTO> components = specialCaseDetail.getComponents();
        
        if (components != null && !components.isEmpty()) {
            log.info("Found {} components in special case detail", components.size());
            
            // ✅ Process each component individually with its own code
            for (SpecialCaseComponentBalanceResponseDTO component : components) {
                if (component == null || component.getAmount() == null 
                    || component.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                
                String componentCode = component.getCode();
                if (componentCode == null || componentCode.isEmpty()) {
                    log.warn("Component has null or empty code, skipping");
                    continue;
                }
                
                // Use the component code directly as the ledger component code
                componentMap.merge(componentCode, component.getAmount(), BigDecimal::add);
                log.info("✅ Added component: {} = {}", componentCode, component.getAmount());
            }
        } else {
            log.warn("No components found in special case detail");
        }
        
        // If no components were found, try to get amount from special case detail
        if (componentMap.isEmpty()) {
            BigDecimal totalAmount = specialCaseDetail.getTotalAmount() != null 
                    ? specialCaseDetail.getTotalAmount() : BigDecimal.ZERO;
            
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Use a default component code if no specific components
                componentMap.put("SPECIAL_CASE_AMOUNT", totalAmount);
                log.info("Added fallback SPECIAL_CASE_AMOUNT: {}", totalAmount);
            }
        }

        log.info("Final component map: {}", componentMap);
        return componentMap;
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