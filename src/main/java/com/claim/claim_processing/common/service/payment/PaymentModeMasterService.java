package com.claim.claim_processing.common.service.payment;

import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.DTO.request.payment.PaymentModeRequestDto;
import java.util.List;

public interface PaymentModeMasterService {

    PaymentModeResponseDto create(PaymentModeRequestDto requestDto);

    PaymentModeResponseDto update(Long id, PaymentModeRequestDto requestDto);

    PaymentModeResponseDto patch(Long id, PaymentModeRequestDto requestDto);

    PaymentModeResponseDto getById(Long id);

    PaymentModeResponseDto getByCode(String code);

    List<PaymentModeResponseDto> getAll();

    List<PaymentModeResponseDto> getAllActive();

    void delete(Long id);
}