package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.InterestMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.InterestMethodResponseDto;

import java.util.List;

public interface InterestMethodService {

    // -----------------------------
    // CREATE
    // -----------------------------
    ApiResponseDTO<InterestMethodResponseDto> create(
            InterestMethodRequestDto dto
    );

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    ApiResponseDTO<InterestMethodResponseDto> patch(
            Long id,
            InterestMethodRequestDto dto
    );

    // -----------------------------
    // GET BY ID
    // -----------------------------
    ApiResponseDTO<InterestMethodResponseDto> getById(
            Long id
    );

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    ApiResponseDTO<InterestMethodResponseDto> getByCode(
            String code
    );

    // -----------------------------
    // GET ALL
    // -----------------------------
    ApiResponseDTO<List<InterestMethodResponseDto>> getAll();

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    ApiResponseDTO<List<InterestMethodResponseDto>> getAllActive();

    // -----------------------------
    // SOFT DELETE
    // -----------------------------
    ApiResponseDTO<String> delete(
            Long id
    );
}