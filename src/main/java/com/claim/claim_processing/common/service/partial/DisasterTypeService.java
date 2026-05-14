package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.DisasterTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.DisasterTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.DisasterTypeUpdateDto;

import java.util.List;

public interface DisasterTypeService {

    // -----------------------------
    // CREATE
    // -----------------------------
    ApiResponseDTO<DisasterTypeResponseDto> create(
            DisasterTypeRequestDto requestDto
    );

    // -----------------------------
    // UPDATE
    // -----------------------------
    ApiResponseDTO<DisasterTypeResponseDto> update(
            Long id,
            DisasterTypeUpdateDto updateDto
    );

    // -----------------------------
    // GET BY ID
    // -----------------------------
    ApiResponseDTO<DisasterTypeResponseDto> getById(
            Long id
    );

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    ApiResponseDTO<DisasterTypeResponseDto> getByCode(
            String code
    );

    // -----------------------------
    // GET ALL
    // -----------------------------
    ApiResponseDTO<List<DisasterTypeResponseDto>> getAll();

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    ApiResponseDTO<List<DisasterTypeResponseDto>> getAllActive();

    // -----------------------------
    // DELETE
    // -----------------------------
    ApiResponseDTO<String> delete(
            Long id
    );
}