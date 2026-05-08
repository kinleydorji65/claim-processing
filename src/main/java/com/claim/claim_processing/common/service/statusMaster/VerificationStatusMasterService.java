package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.VerificationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.VerificationStatusResponseDto;

import java.util.List;

public interface VerificationStatusMasterService {

    VerificationStatusResponseDto create(VerificationStatusRequestDto dto);

    VerificationStatusResponseDto update(Long id, VerificationStatusRequestDto dto);

    VerificationStatusResponseDto getById(Long id);

    VerificationStatusResponseDto getByCode(String code);

    List<VerificationStatusResponseDto> getAll();

    List<VerificationStatusResponseDto> getAllActive();

    void delete(Long id);
}