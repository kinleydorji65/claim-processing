package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ApprovalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.ApprovalStatusResponseDto;

import java.util.List;

public interface ApprovalStatusMasterService {

    ApiResponseDTO<ApprovalStatusResponseDto> create(ApprovalStatusRequestDto dto);

    ApiResponseDTO<ApprovalStatusResponseDto> update(Long id, ApprovalStatusRequestDto dto);

    ApiResponseDTO<ApprovalStatusResponseDto> getById(Long id);

    ApiResponseDTO<ApprovalStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<ApprovalStatusResponseDto>> getAll();

    ApiResponseDTO<List<ApprovalStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}
