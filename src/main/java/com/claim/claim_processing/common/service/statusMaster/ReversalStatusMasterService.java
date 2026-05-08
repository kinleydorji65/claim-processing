package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ReversalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.ReversalStatusResponseDto;

import java.util.List;

public interface ReversalStatusMasterService {

    ReversalStatusResponseDto create(ReversalStatusRequestDto dto);

    ReversalStatusResponseDto update(Long id, ReversalStatusRequestDto dto);

    ReversalStatusResponseDto getById(Long id);

    ReversalStatusResponseDto getByCode(String code);

    List<ReversalStatusResponseDto> getAll();

    List<ReversalStatusResponseDto> getAllActive();

    void delete(Long id);
}