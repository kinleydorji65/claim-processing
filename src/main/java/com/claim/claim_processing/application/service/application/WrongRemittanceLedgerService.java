package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO;

public interface WrongRemittanceLedgerService {

    AccountingEventResponseDto createLedgerEntriesForWrongRemittance(
            GeneralClaimDetailResponse claimDetailResponse, 
            String createdBy);
}
