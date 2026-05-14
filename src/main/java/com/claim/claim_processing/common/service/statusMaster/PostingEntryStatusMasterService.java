package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingEntryStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingEntryStatusResponseDto;

import java.util.List;

public interface PostingEntryStatusMasterService {

    ApiResponseDTO<PostingEntryStatusResponseDto> create(PostingEntryStatusRequestDto dto);

    ApiResponseDTO<PostingEntryStatusResponseDto> update(Long id, PostingEntryStatusRequestDto dto);

    ApiResponseDTO<PostingEntryStatusResponseDto> getById(Long id);

    ApiResponseDTO<PostingEntryStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<PostingEntryStatusResponseDto>> getAll();

    ApiResponseDTO<List<PostingEntryStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}