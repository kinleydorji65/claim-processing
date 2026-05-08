package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingStatusResponseDto;

import java.util.List;

public interface PostingStatusMasterService {

    PostingStatusResponseDto create(PostingStatusRequestDto dto);

    PostingStatusResponseDto update(Long id, PostingStatusRequestDto dto);

    PostingStatusResponseDto getById(Long id);

    PostingStatusResponseDto getByCode(String code);

    List<PostingStatusResponseDto> getAll();

    List<PostingStatusResponseDto> getAllActive();

    void delete(Long id);
}