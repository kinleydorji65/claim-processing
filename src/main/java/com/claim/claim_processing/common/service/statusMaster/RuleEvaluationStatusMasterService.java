package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RuleEvaluationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.RuleEvaluationStatusResponseDto;

import java.util.List;

public interface RuleEvaluationStatusMasterService {

    RuleEvaluationStatusResponseDto create(RuleEvaluationStatusRequestDto dto);

    RuleEvaluationStatusResponseDto update(Long id, RuleEvaluationStatusRequestDto dto);

    RuleEvaluationStatusResponseDto getById(Long id);

    RuleEvaluationStatusResponseDto getByCode(String code);

    List<RuleEvaluationStatusResponseDto> getAll();

    List<RuleEvaluationStatusResponseDto> getAllActive();

    void delete(Long id);
}