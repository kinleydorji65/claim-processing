package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalAccumulationMapper;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalAccumulationRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalAccumulationService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalAccumulationServiceImpl
        implements PartialWithdrawalAccumulationService {

    private final PartialWithdrawalAccumulationRepository repository;
    private final PartialWithdrawalAccumulationMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> create(
            PartialWithdrawalAccumulationRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Partial Withdrawal Accumulation code already exists: " + dto.getCode()
            );
        }

        PartialWithdrawalAccumulationMaster entity = mapper.toEntity(dto);

        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());

        repository.save(entity);

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> update(
            Long id,
            PartialWithdrawalAccumulationRequestDto dto) {

        PartialWithdrawalAccumulationMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Accumulation not found with id: " + id
                        ));

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> getById(
            Long id) {

        PartialWithdrawalAccumulationMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Accumulation not found with id: " + id
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // =========================
    // GET BY CODE
    // =========================
    @Override
    public ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> getByCode(
            String code) {

        PartialWithdrawalAccumulationMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Accumulation not found with code: " + code
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    public ApiResponseDTO<List<PartialWithdrawalAccumulationResponseDto>> getAll() {

        List<PartialWithdrawalAccumulationResponseDto> responseDtos =
                repository.findAll()
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // =========================
    // GET ALL ACTIVE
    // =========================
    @Override
    public ApiResponseDTO<List<PartialWithdrawalAccumulationResponseDto>> getAllActive() {

        List<PartialWithdrawalAccumulationResponseDto> responseDtos =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PartialWithdrawalAccumulationMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Accumulation not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Partial Withdrawal Accumulation deleted successfully"
        );
    }
}