package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ApprovalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.ApprovalStatusResponseDto;

import java.util.List;

public interface ApprovalStatusMasterService {

    ApprovalStatusResponseDto create(ApprovalStatusRequestDto dto);

    ApprovalStatusResponseDto update(Long id, ApprovalStatusRequestDto dto);

    ApprovalStatusResponseDto getById(Long id);

    ApprovalStatusResponseDto getByCode(String code);

    List<ApprovalStatusResponseDto> getAll();

    List<ApprovalStatusResponseDto> getAllActive();

    void delete(Long id);
}
