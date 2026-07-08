package com.claim.claim_processing.application.service.application;


import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;

public interface SpecialCaseLedgerService {
    
    AccountingEventResponseDto createSpecialCaseLedgerEntries(
        GeneralSpecialCaseResponse specialCaseResponse, 
        String createdBy
    );
    
    AccountingEventResponseDto getAccountingEventBySpecialCaseId(Long specialCaseId);
    
    boolean hasLedgerEntries(Long specialCaseId);
}
