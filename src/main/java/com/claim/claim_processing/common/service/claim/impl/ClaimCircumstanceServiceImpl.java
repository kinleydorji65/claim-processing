package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.ClaimCircumstanceMapper;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;
import com.claim.claim_processing.common.service.claim.ClaimCircumstanceService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimCircumstanceServiceImpl implements ClaimCircumstanceService {

    private final ClaimCircumstanceRepository repository;
    private final ClaimCircumstanceMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimCircumstanceResponseDto> create(
            ClaimCircumstanceCreateRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Claim Circumstance code already exists: " + requestDto.getCode()
            );
        }

        ClaimCircumstanceMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getCreatedBy());

        repository.save(entity);

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimCircumstanceResponseDto> getById(Long id) {

        ClaimCircumstanceMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Claim Circumstance not found with id: " + id
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ClaimCircumstanceResponseDto>> getAll() {

        List<ClaimCircumstanceResponseDto> responseDtos = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ClaimCircumstanceResponseDto>> getAllActive() {

        List<ClaimCircumstanceResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimCircumstanceResponseDto> update(
            Long id,
            ClaimCircumstanceUpdateRequestDto requestDto) {

        ClaimCircumstanceMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Claim Circumstance not found with id: " + id
                        ));

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setUpdatedBy(requestDto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ClaimCircumstanceMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Claim Circumstance not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Claim Circumstance deleted successfully"
        );
    }

    @Override
    public ApiResponseDTO<ClaimCircumstanceResponseDto> getByCode(String code) {

        ClaimCircumstanceMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Claim Circumstance not found with code: " + code
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }
}