package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.update.claim.AccountTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.request.claim.AccountTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.AccountTypeResponseDto;

import java.util.List;

public interface AccountTypeService {

    List<AccountTypeResponseDto> getAllActive();

    AccountTypeResponseDto getById(Long id);

    AccountTypeResponseDto create(AccountTypeCreateRequestDto requestDto);

    AccountTypeResponseDto update(Long id, AccountTypeUpdateRequestDto requestDto);

    void deactivate(Long id);
}
