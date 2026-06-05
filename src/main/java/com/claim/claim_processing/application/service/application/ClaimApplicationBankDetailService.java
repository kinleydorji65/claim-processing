package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationBankDetailRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;

public interface ClaimApplicationBankDetailService {
    List<ClaimApplicationBankDetail> create(ClaimApplication claimApplication, List<ClaimApplicationBankDetailRequestDto> requestDto); 
    List<ClaimApplicationBankDetail> patch(ClaimApplication claimApplication, List<ClaimApplicationBankDetailRequestDto> requestDto); 
}
