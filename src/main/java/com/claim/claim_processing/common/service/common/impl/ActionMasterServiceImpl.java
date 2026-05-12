package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.ActionResponseDto;
import com.claim.claim_processing.common.entities.common.ActionMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.ActionMasterMapper;
import com.claim.claim_processing.common.repository.common.ActionMasterRepository;
import com.claim.claim_processing.common.service.common.ActionMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionMasterServiceImpl implements ActionMasterService {

    private final ActionMasterRepository repository;
    private final ActionMasterMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ActionResponseDto> create(ActionRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Action code already exists: " + dto.getCode());
        }

        ActionMaster entity = mapper.toEntity(dto);

        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        ActionMaster saved = repository.save(entity);

        return ApiResponseDTO.success(
                "Action created successfully",
                mapper.toResponseDto(saved)
        );
    }

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ActionResponseDto> patch(ActionRequestDto dto) {

        ActionMaster entity = repository.findById(dto.getId())
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Action", String.valueOf(dto.getId()))
                );

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedAt(LocalDateTime.now());

        ActionMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Action updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<ActionResponseDto> getById(Long id) {

        ActionMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Action", String.valueOf(id))
                );

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ActionResponseDto>> getAll() {

        List<ActionResponseDto> list = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Actions fetched successfully",
                list
        );
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ActionResponseDto>> getAllActive() {

        List<ActionResponseDto> list = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Active actions fetched successfully",
                list
        );
    }

    // -----------------------------
    // SOFT DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ActionMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Action", String.valueOf(id))
                );

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        return ApiResponseDTO.success("Action deleted successfully");
    }
}