package com.claim.claim_processing.common.service.beneficiary;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.beneficiary.ClaimantTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.request.beneficiary.ClaimantTypeCreateRequestDto;

import java.util.List;
public interface ClaimantTypeService {

    ApiResponseDTO<List<ClaimantTypeResponseDto>> getAllActive();

    ApiResponseDTO<ClaimantTypeResponseDto> getById(Long id);

    ApiResponseDTO<ClaimantTypeResponseDto> getByCode(String code);

    ApiResponseDTO<ClaimantTypeResponseDto> create(ClaimantTypeCreateRequestDto requestDto);

    ApiResponseDTO<ClaimantTypeResponseDto> update(Long id, ClaimantTypeUpdateRequestDto requestDto);

    ApiResponseDTO<String> delete(Long id);
}
