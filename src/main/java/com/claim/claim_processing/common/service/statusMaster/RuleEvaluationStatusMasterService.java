package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RuleEvaluationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.RuleEvaluationStatusResponseDto;

import java.util.List;

public interface RuleEvaluationStatusMasterService {

    ApiResponseDTO<RuleEvaluationStatusResponseDto> create(RuleEvaluationStatusRequestDto dto);

    ApiResponseDTO<RuleEvaluationStatusResponseDto> update(Long id, RuleEvaluationStatusRequestDto dto);

    ApiResponseDTO<RuleEvaluationStatusResponseDto> getById(Long id);

    ApiResponseDTO<RuleEvaluationStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<RuleEvaluationStatusResponseDto>> getAll();

    ApiResponseDTO<List<RuleEvaluationStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}