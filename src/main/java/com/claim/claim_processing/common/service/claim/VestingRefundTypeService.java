package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.VestingRefundTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;

import java.util.List;

public interface VestingRefundTypeService {

    ApiResponseDTO<VestingRefundTypeResponseDto> create(VestingRefundTypeRequestDto requestDto);

    ApiResponseDTO<VestingRefundTypeResponseDto> update(Long id, VestingRefundTypeRequestDto requestDto);

    ApiResponseDTO<VestingRefundTypeResponseDto> getById(Long id);

    ApiResponseDTO<VestingRefundTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<VestingRefundTypeResponseDto>> getAll();

    ApiResponseDTO<String> delete(Long id);
}