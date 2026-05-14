package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.HousePurchaseTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.HousePurchaseTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.HousePurchaseTypeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.HousePurchaseTypeMaster;
import com.claim.claim_processing.common.mapper.partial.HousePurchaseTypeMapper;
import com.claim.claim_processing.common.repository.partial.HousePurchaseTypeRepository;
import com.claim.claim_processing.common.service.partial.HousePurchaseTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HousePurchaseTypeServiceImpl implements HousePurchaseTypeService {

    private final HousePurchaseTypeRepository repository;
    private final HousePurchaseTypeMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<HousePurchaseTypeResponseDto> create(
            HousePurchaseTypeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "House Purchase Type code already exists: " + requestDto.getCode()
            );
        }

        HousePurchaseTypeMaster entity = mapper.toEntity(requestDto);

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
    public ApiResponseDTO<HousePurchaseTypeResponseDto> getById(Long id) {

        HousePurchaseTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "House Purchase Type not found with id: " + id
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    public ApiResponseDTO<HousePurchaseTypeResponseDto> getByCode(String code) {

        HousePurchaseTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "House Purchase Type not found with code: " + code
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<HousePurchaseTypeResponseDto>> getAll() {

        List<HousePurchaseTypeResponseDto> responseDtos = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<HousePurchaseTypeResponseDto>> getAllActive() {

        List<HousePurchaseTypeResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<HousePurchaseTypeResponseDto> update(
            Long id,
            HousePurchaseTypeUpdateDto updateDto) {

        HousePurchaseTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "House Purchase Type not found with id: " + id
                        ));

        mapper.updateEntityFromDto(updateDto, entity);

        entity.setUpdatedBy(updateDto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        HousePurchaseTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "House Purchase Type not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "House Purchase Type deleted successfully"
        );
    }
}