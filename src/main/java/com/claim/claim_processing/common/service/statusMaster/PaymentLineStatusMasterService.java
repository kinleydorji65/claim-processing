package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PaymentLineStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PaymentLineStatusResponseDto;

import java.util.List;

public interface PaymentLineStatusMasterService {

    PaymentLineStatusResponseDto create(PaymentLineStatusRequestDto dto);

    PaymentLineStatusResponseDto update(Long id, PaymentLineStatusRequestDto dto);

    PaymentLineStatusResponseDto getById(Long id);

    PaymentLineStatusResponseDto getByCode(String code);

    List<PaymentLineStatusResponseDto> getAll();

    List<PaymentLineStatusResponseDto> getAllActive();

    void delete(Long id);
}