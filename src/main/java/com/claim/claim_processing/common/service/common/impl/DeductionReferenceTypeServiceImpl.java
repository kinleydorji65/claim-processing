package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.DeductionReferenceTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DeductionReferenceTypeResponseDto;
import com.claim.claim_processing.common.entities.common.DeductionReferenceTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.common.DeductionReferenceTypeMapper;
import com.claim.claim_processing.common.repository.common.DeductionReferenceTypeRepository;
import com.claim.claim_processing.common.service.common.DeductionReferenceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionReferenceTypeServiceImpl implements DeductionReferenceTypeService {

    private final DeductionReferenceTypeRepository repository;
    private final DeductionReferenceTypeMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<DeductionReferenceTypeResponseDto> create(DeductionReferenceTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Code already exists: " + dto.getCode());
        }

        DeductionReferenceTypeMaster entity = mapper.toEntity(dto);
        DeductionReferenceTypeMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<DeductionReferenceTypeResponseDto>> getAll() {

        List<DeductionReferenceTypeResponseDto> list =
                repository.findAll()
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(list);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<DeductionReferenceTypeResponseDto>> getAllActive() {

        List<DeductionReferenceTypeResponseDto> list =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(list);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<DeductionReferenceTypeResponseDto> getById(Long id) {

        DeductionReferenceTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Deduction Reference Type", String.valueOf(id))
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    public ApiResponseDTO<DeductionReferenceTypeResponseDto> getByCode(String code) {

        DeductionReferenceTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Deduction Reference Type", code)
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<DeductionReferenceTypeResponseDto> update(Long id, DeductionReferenceTypeRequestDto dto) {

        DeductionReferenceTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Deduction Reference Type", String.valueOf(id))
                );

        mapper.updateEntityFromDto(dto, entity);
        DeductionReferenceTypeMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // -----------------------------
    // DELETE (SOFT DELETE)
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        DeductionReferenceTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Deduction Reference Type", String.valueOf(id))
                );

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);

        return ApiResponseDTO.success("Deleted successfully");
    }
}