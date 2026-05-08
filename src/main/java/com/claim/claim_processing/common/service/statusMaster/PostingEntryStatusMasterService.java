package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingEntryStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingEntryStatusResponseDto;

import java.util.List;

public interface PostingEntryStatusMasterService {

    PostingEntryStatusResponseDto create(PostingEntryStatusRequestDto dto);

    PostingEntryStatusResponseDto update(Long id, PostingEntryStatusRequestDto dto);

    PostingEntryStatusResponseDto getById(Long id);

    PostingEntryStatusResponseDto getByCode(String code);

    List<PostingEntryStatusResponseDto> getAll();

    List<PostingEntryStatusResponseDto> getAllActive();

    void delete(Long id);
}