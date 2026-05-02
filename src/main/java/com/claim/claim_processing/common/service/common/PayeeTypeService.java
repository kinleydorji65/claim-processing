package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.PayeeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.PayeeTypeResponseDto;

import java.util.List;

public interface PayeeTypeService {

    PayeeTypeResponseDto create(PayeeTypeRequestDto dto);

    PayeeTypeResponseDto update(Long id, PayeeTypeRequestDto dto);

    PayeeTypeResponseDto getById(Long id);

    PayeeTypeResponseDto getByCode(String code);

    List<PayeeTypeResponseDto> getAll();

    List<PayeeTypeResponseDto> getAllActive();

    void delete(Long id);
}