package com.claim.claim_processing.common.service.payment;

import com.claim.claim_processing.common.DTO.request.payment.PaymentStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;

import java.util.List;

public interface PaymentStatusMasterService {

    ApiResponseDTO<PaymentStatusResponseDto> create(PaymentStatusRequestDto requestDto);

    ApiResponseDTO<PaymentStatusResponseDto> update(Long id, PaymentStatusRequestDto requestDto);

    ApiResponseDTO<PaymentStatusResponseDto> getById(Long id);

    ApiResponseDTO<PaymentStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<PaymentStatusResponseDto>> getAll();

    ApiResponseDTO<List<PaymentStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}