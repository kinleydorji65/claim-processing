package com.claim.claim_processing.common.service.refundMaster;

import com.claim.claim_processing.common.DTO.request.refundMaster.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.refundMaster.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.RefundScopeUpdateDto;

import java.util.List;

public interface RefundScopeService {

    ApiResponseDTO<RefundScopeResponseDto> create(RefundScopeRequestDto requestDto);

    ApiResponseDTO<RefundScopeResponseDto> getById(Long id);

    ApiResponseDTO<RefundScopeResponseDto> getByCode(String code);

    ApiResponseDTO<List<RefundScopeResponseDto>> getAll();

    ApiResponseDTO<List<RefundScopeResponseDto>> getAllActive();

    ApiResponseDTO<RefundScopeResponseDto> update(Long id, RefundScopeUpdateDto updateDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}