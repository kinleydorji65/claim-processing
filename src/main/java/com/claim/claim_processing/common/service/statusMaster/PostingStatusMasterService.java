package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingStatusResponseDto;

import java.util.List;

public interface PostingStatusMasterService {

    ApiResponseDTO<PostingStatusResponseDto> create(PostingStatusRequestDto dto);

    ApiResponseDTO<PostingStatusResponseDto> update(Long id, PostingStatusRequestDto dto);

    ApiResponseDTO<PostingStatusResponseDto> getById(Long id);

    ApiResponseDTO<PostingStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<PostingStatusResponseDto>> getAll();

    ApiResponseDTO<List<PostingStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}