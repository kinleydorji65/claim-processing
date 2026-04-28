package com.claim.claim_processing.common.service.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import java.util.List;

public interface BenefitComponentTypeMasterService {

    /**
     * Create new record
     */
    BenefitComponentTypeMasterResponseDto create(
            BenefitComponentTypeMasterRequestDto requestDto
    );

    /**
     * Update existing record
     */
    BenefitComponentTypeMasterResponseDto update(
            Long id,
            BenefitComponentTypeMasterRequestDto requestDto
    );

    /**
     * Get by id
     */
    BenefitComponentTypeMasterResponseDto getById(Long id);

    /**
     * Get all records
     */
    List<BenefitComponentTypeMasterResponseDto> getAll();

    /**
     * Get active/inactive records
     */
    List<BenefitComponentTypeMasterResponseDto> getByStatus(
            ActivityEnum isActive
    );

    /**
     * Search by name
     */
    List<BenefitComponentTypeMasterResponseDto> searchByName(
            String keyword
    );

    /**
     * Soft delete / deactivate
     */
    void delete(Long id);
}