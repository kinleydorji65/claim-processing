package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.DeductionTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DeductionTypeResponseDto;
import com.claim.claim_processing.common.entities.common.DeductionTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.common.DeductionTypeMapper;
import com.claim.claim_processing.common.repository.common.DeductionTypeRepository;
import com.claim.claim_processing.common.service.common.DeductionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionTypeServiceImpl implements DeductionTypeService {

    private final DeductionTypeRepository repository;
    private final DeductionTypeMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public ApiResponseDTO<DeductionTypeResponseDto> create(DeductionTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Deduction Type code already exists: " + dto.getCode());
        }

        DeductionTypeMaster entity = mapper.toEntity(dto);
        DeductionTypeMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    public ApiResponseDTO<DeductionTypeResponseDto> getById(Long id) {

        DeductionTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Deduction Type", String.valueOf(id))
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // -------------------------------
    // GET BY CODE
    // -------------------------------
    @Override
    public ApiResponseDTO<DeductionTypeResponseDto> getByCode(String code) {

        DeductionTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Deduction Type", code)
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @Override
    public ApiResponseDTO<List<DeductionTypeResponseDto>> getAllActive() {

        List<DeductionTypeResponseDto> list =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(list);
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @Override
    public ApiResponseDTO<DeductionTypeResponseDto> patch(
            Long id,
            DeductionTypeRequestDto dto
    ) {

        try {

            DeductionTypeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Deduction Type",
                                    String.valueOf(id)
                            )
                    );

            // PATCH -> update only non-null fields
            mapper.updateEntityFromDto(dto, entity);

            DeductionTypeMaster updated = repository.save(entity);

            return ApiResponseDTO.success(
                    "Deduction Type updated successfully",
                    mapper.toResponseDto(updated)
            );

        } catch (ClaimException e) {
            throw e;
        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to update Deduction Type: " + e.getMessage()
            );
        }
    }

    // -------------------------------
    // DELETE (SOFT DELETE)
    // -------------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        DeductionTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Deduction Type", String.valueOf(id))
                );

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);

        return ApiResponseDTO.success("Deleted successfully");
    }
}