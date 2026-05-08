package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RentClearanceStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.RentClearanceStatusResponseDto;

import java.util.List;

public interface RentClearanceStatusMasterService {

    RentClearanceStatusResponseDto create(RentClearanceStatusRequestDto dto);

    RentClearanceStatusResponseDto update(Long id, RentClearanceStatusRequestDto dto);

    RentClearanceStatusResponseDto getById(Long id);

    RentClearanceStatusResponseDto getByCode(String code);

    List<RentClearanceStatusResponseDto> getAll();

    List<RentClearanceStatusResponseDto> getAllActive();

    void delete(Long id);
}