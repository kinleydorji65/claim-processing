package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.DecisionRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;
import com.claim.claim_processing.common.entities.common.DecisionMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.DecisionMapper;
import com.claim.claim_processing.common.repository.common.DecisionRepository;
import com.claim.claim_processing.common.service.common.DecisionService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionServiceImpl implements DecisionService {

    private final DecisionRepository repository;
    private final DecisionMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<DecisionResponseDto> createDecision(DecisionRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Decision code already exists: " + requestDto.getCode());
        }

        DecisionMaster entity = mapper.toEntity(requestDto);

        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        DecisionMaster saved = repository.save(entity);

        return ApiResponseDTO.success(
                "Decision created successfully",
                mapper.toResponseDto(saved)
        );
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<DecisionResponseDto>> getAll() {

        List<DecisionResponseDto> list =
                repository.findAll()
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success("All decisions fetched successfully", list);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<DecisionResponseDto>> getAllActive() {

        List<DecisionResponseDto> list =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success("Active decisions fetched successfully", list);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<DecisionResponseDto> getById(Long id) {

        DecisionMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Decision", String.valueOf(id)));

        return ApiResponseDTO.success(
                "Decision fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    public ApiResponseDTO<DecisionResponseDto> getByCode(String code) {

        DecisionMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Decision", code));

        return ApiResponseDTO.success(
                "Decision fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<DecisionResponseDto> updateDecision(Long id, DecisionRequestDto requestDto) {

        DecisionMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Decision", String.valueOf(id)));

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setUpdatedAt(LocalDateTime.now());

        DecisionMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Decision updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    // -----------------------------
    // DELETE (soft or hard depending on design)
    // -----------------------------
    @Override
    public ApiResponseDTO<String> deleteDecision(Long id) {

        DecisionMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Decision", String.valueOf(id)));

        repository.delete(entity);

        return ApiResponseDTO.success("Decision deleted successfully");
    }
}