package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.DisasterTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.DisasterTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.DisasterTypeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.DisasterTypeMaster;
import com.claim.claim_processing.common.mapper.partial.DisasterTypeMapper;
import com.claim.claim_processing.common.repository.partial.DisasterTypeRepository;
import com.claim.claim_processing.common.service.partial.DisasterTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisasterTypeServiceImpl implements DisasterTypeService {

    private final DisasterTypeRepository repository;
    private final DisasterTypeMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    // CREATE
    @Override
    public ApiResponseDTO<DisasterTypeResponseDto> create(
            DisasterTypeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Disaster Type code already exists: " + requestDto.getCode()
            );
        }

        DisasterTypeMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getUpdatedBy());

        repository.save(entity);

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    // UPDATE
    @Override
    public ApiResponseDTO<DisasterTypeResponseDto> update(
            Long id,
            DisasterTypeUpdateDto updateDto) {

        DisasterTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Disaster Type not found with id: " + id
                        ));

        mapper.updateEntityFromDto(updateDto, entity);

        entity.setUpdatedBy(updateDto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<DisasterTypeResponseDto> getById(Long id) {

        DisasterTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Disaster Type not found with id: " + id));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    public ApiResponseDTO<DisasterTypeResponseDto> getByCode(String code) {

        DisasterTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound("Disaster Type not found with code: " + code));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<DisasterTypeResponseDto>> getAll() {

        List<DisasterTypeResponseDto> responseDtos = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<DisasterTypeResponseDto>> getAllActive() {

        List<DisasterTypeResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        DisasterTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Disaster Type not found with id: " + id));

        repository.delete(entity);

        return ApiResponseDTO.success("Disaster Type deleted successfully");
    }
}