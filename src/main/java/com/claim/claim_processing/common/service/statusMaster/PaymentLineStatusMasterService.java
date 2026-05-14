package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PaymentLineStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.PaymentLineStatusResponseDto;

import java.util.List;

public interface PaymentLineStatusMasterService {

    ApiResponseDTO<PaymentLineStatusResponseDto> create(PaymentLineStatusRequestDto dto);

    ApiResponseDTO<PaymentLineStatusResponseDto> update(Long id, PaymentLineStatusRequestDto dto);

    ApiResponseDTO<PaymentLineStatusResponseDto> getById(Long id);

    ApiResponseDTO<PaymentLineStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<PaymentLineStatusResponseDto>> getAll();

    ApiResponseDTO<List<PaymentLineStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}