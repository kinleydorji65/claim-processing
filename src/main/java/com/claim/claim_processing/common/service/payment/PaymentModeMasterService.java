package com.claim.claim_processing.common.service.payment;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.DTO.request.payment.PaymentModeRequestDto;
import java.util.List;

public interface PaymentModeMasterService {

    ApiResponseDTO<PaymentModeResponseDto> create(PaymentModeRequestDto requestDto);

    ApiResponseDTO<PaymentModeResponseDto> update(Long id, PaymentModeRequestDto requestDto);

    ApiResponseDTO<PaymentModeResponseDto> patch(Long id, PaymentModeRequestDto requestDto);

    ApiResponseDTO<PaymentModeResponseDto> getById(Long id);

    ApiResponseDTO<PaymentModeResponseDto> getByCode(String code);

    ApiResponseDTO<List<PaymentModeResponseDto>> getAll();

    ApiResponseDTO<List<PaymentModeResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}