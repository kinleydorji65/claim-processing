package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.CessationTypeMapper;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;

import com.claim.claim_processing.common.service.claim.CessationTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CessationTypeServiceImpl implements CessationTypeService {

    private final CessationTypeRepository repository;
    private final CessationTypeMapper mapper;
    private final ClaimCircumstanceRepository claimCircumstanceRepository;

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<CessationTypeResponseDto>> getAll() {

        List<CessationTypeResponseDto> response =
                repository.findAll()
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(
                "Cessation Types fetched successfully",
                response
        );
    }

    // -----------------------------
    // GET ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<CessationTypeResponseDto>> getActive() {

        List<CessationTypeResponseDto> response =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(
                "Active Cessation Types fetched successfully",
                response
        );
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<CessationTypeResponseDto> getById(Long id) {

        CessationTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Cessation Type",
                                String.valueOf(id)
                        ));

        return ApiResponseDTO.success(
                "Cessation Type fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    public ApiResponseDTO<List<CessationTypeResponseDto>> getByClaimCircumstance(Long circumstanceId) {

        List<CessationTypeMaster> list =
                repository.findByClaimCircumstanceId(circumstanceId);

        if (list.isEmpty()) {
            throw ClaimException.notFound(
                    "No Cessation Types found for circumstance id: " + circumstanceId
            );
        }

        List<CessationTypeResponseDto> response =
                list.stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(
                "Cessation Types by circumstance fetched successfully",
                response
        );
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<CessationTypeResponseDto> create(
            CessationTypeCreateRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Cessation Type code already exists: " + requestDto.getCode()
            );
        }

        ClaimCircumstanceMaster claimCircumstance =
                claimCircumstanceRepository.findById(requestDto.getClaimCircumstanceId())
                        .orElseThrow(() -> ClaimException.notFound(
                                "Claim circumstance not found with id: "
                                        + requestDto.getClaimCircumstanceId()
                        ));

        CessationTypeMaster entity = mapper.toEntity(requestDto);

        entity.setClaimCircumstance(claimCircumstance);
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        CessationTypeMaster saved = repository.save(entity);

        return ApiResponseDTO.success(
                "Cessation Type created successfully",
                mapper.toResponseDto(saved)
        );
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<CessationTypeResponseDto> update(Long id, CessationTypeUpdateRequestDto requestDto) {

        CessationTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Cessation Type",
                                String.valueOf(id)
                        ));

        mapper.updateEntityFromDto(requestDto, entity);
        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        CessationTypeMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Cessation Type updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    // -----------------------------
    // DELETE (soft delete style recommended)
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        CessationTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Cessation Type",
                                String.valueOf(id)
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Cessation Type deleted successfully",
                "Deleted successfully"
        );
    }

    @Override
    public ApiResponseDTO<CessationTypeResponseDto> getByCode(String code) {

        CessationTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Cessation type not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Cessation type fetched successfully",
                mapper.toResponseDto(entity)
        );
    }
}