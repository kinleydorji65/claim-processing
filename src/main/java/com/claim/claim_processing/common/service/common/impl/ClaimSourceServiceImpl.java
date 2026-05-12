package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.ClaimSourceRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.ClaimSourceResponseDto;
import com.claim.claim_processing.common.DTO.update.common.ClaimSourceUpdateDto;
import com.claim.claim_processing.common.entities.common.ClaimSourceMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.ClaimSourceMapper;
import com.claim.claim_processing.common.repository.common.ClaimSourceRepository;
import com.claim.claim_processing.common.service.common.ClaimSourceService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimSourceServiceImpl implements ClaimSourceService {

    private final ClaimSourceRepository repository;
    private final ClaimSourceMapper mapper;

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ClaimSourceResponseDto>> getAllActive() {

        List<ClaimSourceResponseDto> response =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success("Active claim sources fetched successfully", response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimSourceResponseDto> getById(Long id) {

        ClaimSourceMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Claim Source", String.valueOf(id)));

        return ApiResponseDTO.success("Claim source fetched successfully",
                mapper.toResponseDto(entity));
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimSourceResponseDto> getByCode(String code) {

        ClaimSourceMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Claim Source", code));

        return ApiResponseDTO.success("Claim source fetched successfully",
                mapper.toResponseDto(entity));
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimSourceResponseDto> create(ClaimSourceRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Claim Source code already exists: " + requestDto.getCode());
        }

        ClaimSourceMaster entity = mapper.toEntity(requestDto);

        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        ClaimSourceMaster saved = repository.save(entity);

        return ApiResponseDTO.success("Claim source created successfully",
                mapper.toResponseDto(saved));
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimSourceResponseDto> update(Long id, ClaimSourceUpdateDto updateDto) {

        ClaimSourceMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Claim Source", String.valueOf(id)));

        mapper.updateEntityFromDto(updateDto, entity);

        entity.setUpdatedAt(LocalDateTime.now());

        ClaimSourceMaster updated = repository.save(entity);

        return ApiResponseDTO.success("Claim source updated successfully",
                mapper.toResponseDto(updated));
    }

    // -----------------------------
    // DEACTIVATE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> deactivate(Long id) {

        ClaimSourceMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Claim Source", String.valueOf(id)));

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        return ApiResponseDTO.success("Claim source deactivated successfully");
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        try {

            ClaimSourceMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound("Claim Source", String.valueOf(id)));

            entity.setIsActive(ActivityEnum.N);
            repository.save(entity);

            return ApiResponseDTO.success("Claim Source deleted successfully", "DELETED");

        } catch (Exception ex) {
            throw ClaimException.internalError("Failed to delete Claim Source", ex);
        }
    }
}