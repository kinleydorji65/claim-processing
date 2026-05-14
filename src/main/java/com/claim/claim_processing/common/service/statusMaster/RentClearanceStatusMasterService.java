package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RentClearanceStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.RentClearanceStatusResponseDto;

import java.util.List;

public interface RentClearanceStatusMasterService {

    ApiResponseDTO<RentClearanceStatusResponseDto> create(RentClearanceStatusRequestDto dto);

    ApiResponseDTO<RentClearanceStatusResponseDto> update(Long id, RentClearanceStatusRequestDto dto);

    ApiResponseDTO<RentClearanceStatusResponseDto> getById(Long id);

    ApiResponseDTO<RentClearanceStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<RentClearanceStatusResponseDto>> getAll();

    ApiResponseDTO<List<RentClearanceStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}