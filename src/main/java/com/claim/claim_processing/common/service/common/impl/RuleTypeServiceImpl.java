package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.RuleTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.RuleTypeMapper;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.service.common.RuleTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleTypeServiceImpl implements RuleTypeService {

    private final RuleTypeRepository repository;
    private final RuleTypeMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<RuleTypeResponseDto> create(
            RuleTypeRequestDto dto
    ) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Rule Type code already exists: " + dto.getCode()
            );
        }

        RuleTypeMaster entity = mapper.toEntity(dto);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());
        RuleTypeMaster savedEntity = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(savedEntity)
        );
    }

    // -----------------------------
    // UPDATE (PATCH)
    // -----------------------------
    @Override
    public ApiResponseDTO<RuleTypeResponseDto> update(
            Long id,
            RuleTypeRequestDto dto
    ) {

        RuleTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Rule Type",
                                String.valueOf(id)
                        )
                );

        // PATCH update (only non-null values)
        mapper.updateEntityFromDto(dto, entity);
        entity.setUpdatedBy(dto.getUpdatedBy());

        RuleTypeMaster updatedEntity = repository.save(entity);

        return ApiResponseDTO.success(
                "Rule Type updated successfully",
                mapper.toResponseDto(updatedEntity)
        );
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<RuleTypeResponseDto> getById(
            Long id
    ) {

        RuleTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Rule Type",
                                String.valueOf(id)
                        )
                );

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    public ApiResponseDTO<RuleTypeResponseDto> getByCode(
            String code
    ) {

        RuleTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Rule Type",
                                code
                        )
                );

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<RuleTypeResponseDto>> getAll() {

        List<RuleTypeResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<RuleTypeResponseDto>> getAllActive() {

        List<RuleTypeResponseDto> response = repository
                .findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(response);
    }

    // -----------------------------
    // SOFT DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(
            Long id
    ) {

        RuleTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Rule Type",
                                String.valueOf(id)
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);

        return ApiResponseDTO.success(
                "Rule Type deactivated successfully",
                null
        );
    }
}