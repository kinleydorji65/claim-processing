package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;

public interface ClaimLedgerService {
    AccountingEventResponseDto createLedgerEntries(GeneralClaimDetailResponse claimResponse, String createdBy);
    AccountingEventResponseDto getAccountingEventByClaimId(Long claimId);
    List<LedgerEntryResponseDto> getLedgerEntriesByEventId(Long eventId);
    void reverseLedgerEntries(Long claimId, String reversedBy, String reason);
    boolean hasLedgerEntries(Long claimId);
}
