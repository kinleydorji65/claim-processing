package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.AccountTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.AccountTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.AccountTypeUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.AccountTypeMapper;
import com.claim.claim_processing.common.repository.claim.AccountTypeRepository;
import com.claim.claim_processing.common.service.claim.AccountTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepository repository;
    private final AccountTypeMapper mapper;

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<AccountTypeResponseDto>> getAllActive() {

        List<AccountTypeResponseDto> responseDtos =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<AccountTypeResponseDto> getById(Long id) {

        AccountTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Account Type",
                                String.valueOf(id)
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<AccountTypeResponseDto> create(
            AccountTypeCreateRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Account Type code already exists: "
                            + requestDto.getCode()
            );
        }

        AccountTypeMaster entity = mapper.toEntity(requestDto);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        AccountTypeMaster savedEntity = repository.save(entity);

        return ApiResponseDTO.success(
                mapper.toResponseDto(savedEntity)
        );
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<AccountTypeResponseDto> update(
            Long id,
            AccountTypeUpdateRequestDto requestDto) {

        AccountTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Account Type",
                                String.valueOf(id)
                        ));

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setUpdatedAt(LocalDateTime.now());

        AccountTypeMaster updatedEntity = repository.save(entity);

        return ApiResponseDTO.success(
                mapper.toResponseDto(updatedEntity)
        );
    }

    // -----------------------------
    // DEACTIVATE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> deactivate(Long id) {

        AccountTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Account Type",
                                String.valueOf(id)
                        ));

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        return ApiResponseDTO.success(
                "Account Type deactivated successfully"
        );
    }
}