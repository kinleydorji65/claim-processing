package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.InterestMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.InterestMethodResponseDto;
import com.claim.claim_processing.common.entities.common.InterestMethodMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.InterestMethodMapper;
import com.claim.claim_processing.common.repository.common.InterestMethodRepository;
import com.claim.claim_processing.common.service.common.InterestMethodService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestMethodServiceImpl implements InterestMethodService {

    private final InterestMethodRepository repository;
    private final InterestMethodMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<InterestMethodResponseDto> create(
            InterestMethodRequestDto dto
    ) {

        try {

            if (repository.existsByCode(dto.getCode())) {
                throw ClaimException.conflict(
                        "Interest Method code already exists: "
                                + dto.getCode()
                );
            }

            InterestMethodMaster entity = mapper.toEntity(dto);

            InterestMethodMaster saved = repository.save(entity);

            return ApiResponseDTO.success(
                    "Interest Method created successfully",
                    mapper.toResponseDto(saved)
            );

        } catch (ClaimException e) {
            throw e;
        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to create Interest Method: " + e.getMessage()
            );
        }
    }

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<InterestMethodResponseDto> patch(
            Long id,
            InterestMethodRequestDto dto
    ) {

        try {

            InterestMethodMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Interest Method",
                                    String.valueOf(id)
                            )
                    );

            mapper.updateEntityFromDto(dto, entity);

            InterestMethodMaster updated = repository.save(entity);

            return ApiResponseDTO.success(
                    "Interest Method updated successfully",
                    mapper.toResponseDto(updated)
            );

        } catch (ClaimException e) {
            throw e;
        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to update Interest Method: " + e.getMessage()
            );
        }
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<InterestMethodResponseDto> getById(
            Long id
    ) {

        try {

            InterestMethodMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Interest Method",
                                    String.valueOf(id)
                            )
                    );

            return ApiResponseDTO.success(
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException e) {
            throw e;
        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to fetch Interest Method: " + e.getMessage()
            );
        }
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    public ApiResponseDTO<InterestMethodResponseDto> getByCode(
            String code
    ) {

        try {

            InterestMethodMaster entity =
                    repository.findByCode(code)
                            .orElseThrow(() ->
                                    ClaimException.resourceNotFound(
                                            "Interest Method Code",
                                            code
                                    )
                            );

            return ApiResponseDTO.success(
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException e) {
            throw e;
        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to fetch Interest Method by code: " + e.getMessage()
            );
        }
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<InterestMethodResponseDto>> getAll() {

        try {

            List<InterestMethodResponseDto> response =
                    repository.findAll()
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to fetch Interest Methods: " + e.getMessage()
            );
        }
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<InterestMethodResponseDto>> getAllActive() {

        try {

            List<InterestMethodResponseDto> response =
                    repository.findByIsActive(ActivityEnum.Y)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to fetch active Interest Methods: " + e.getMessage()
            );
        }
    }

    // -----------------------------
    // SOFT DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(
            Long id
    ) {

        try {

            InterestMethodMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Interest Method",
                                    String.valueOf(id)
                            )
                    );

            entity.setIsActive(ActivityEnum.N);

            repository.save(entity);

            return ApiResponseDTO.success(
                    "Interest Method deactivated successfully",
                    null
            );

        } catch (ClaimException e) {
            throw e;
        } catch (Exception e) {
            throw ClaimException.internalError(
                    "Failed to delete Interest Method: " + e.getMessage()
            );
        }
    }
}