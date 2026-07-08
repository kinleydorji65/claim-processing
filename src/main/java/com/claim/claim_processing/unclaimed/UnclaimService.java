package com.claim.claim_processing.unclaimed;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;

public interface UnclaimService {
    List<ReserveAccountResponseDto> getUnclaimedReserveAccounts(String userCode);
    List<ReserveAccountResponseDto> activateUnclaimReserveAccount(String userCode, String nppfNumber);
}
