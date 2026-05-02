package com.claim.claim_processing.common.service.payment;

import com.claim.claim_processing.common.DTO.request.payment.PaymentStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;

import java.util.List;

public interface PaymentStatusMasterService {

    PaymentStatusResponseDto create(PaymentStatusRequestDto requestDto);

    PaymentStatusResponseDto update(Long id, PaymentStatusRequestDto requestDto);

    PaymentStatusResponseDto getById(Long id);

    PaymentStatusResponseDto getByCode(String code);

    List<PaymentStatusResponseDto> getAll();

    List<PaymentStatusResponseDto> getAllActive();

    void delete(Long id);
}