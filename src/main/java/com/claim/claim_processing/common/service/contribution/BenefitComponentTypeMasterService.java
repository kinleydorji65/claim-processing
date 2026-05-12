package com.claim.claim_processing.common.service.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import java.util.List;

public interface BenefitComponentTypeMasterService {

    /**
     * Create new record
     */
    ApiResponseDTO<BenefitComponentTypeMasterResponseDto> create(
            BenefitComponentTypeMasterRequestDto requestDto
    );

    /**
     * Update existing record
     */
    ApiResponseDTO<BenefitComponentTypeMasterResponseDto> update(
            Long id,
            BenefitComponentTypeMasterRequestDto requestDto
    );

    /**
     * Get by id
     */
    ApiResponseDTO<BenefitComponentTypeMasterResponseDto> getById(Long id);

    /**
     * Get all records
     */
    ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> getAll();


    ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> getAllWithoutComponent();

    /**
     * Get active/inactive records
     */
    ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> getByStatus(
            ActivityEnum isActive
    );

    /**
     * Search by name
     */
    ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> searchByName(
            String keyword
    );

    /**
     * Soft delete / deactivate
     */
    ApiResponseDTO<String> delete(Long id);
}