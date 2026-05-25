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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepository repository;
    private final AccountTypeMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<AccountTypeResponseDto>> getAllActive() {

        List<AccountTypeResponseDto> response = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Active account types fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<AccountTypeResponseDto> getById(Long id) {

        AccountTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Account Type",
                        String.valueOf(id)
                ));

        return ApiResponseDTO.success(
                "Account type fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<AccountTypeResponseDto> getByCode(String code) {

        AccountTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Account type not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Account type fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    public ApiResponseDTO<AccountTypeResponseDto> create(
            AccountTypeCreateRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Account type already exists with code: " + requestDto.getCode()
            );
        }

        AccountTypeMaster entity = mapper.toEntity(requestDto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(requestDto.getCreatedBy());

        AccountTypeMaster savedEntity = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(savedEntity)
        );
    }

    @Override
    public ApiResponseDTO<AccountTypeResponseDto> update(
            Long id,
            AccountTypeUpdateRequestDto requestDto) {

        AccountTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Account Type",
                        String.valueOf(id)
                ));

        if (requestDto.getCode() != null
                && !requestDto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Account type already exists with code: " + requestDto.getCode()
            );
        }

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        AccountTypeMaster updatedEntity = repository.save(entity);

        return ApiResponseDTO.success(
                "Account type updated successfully",
                mapper.toResponseDto(updatedEntity)
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        AccountTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Account Type",
                        String.valueOf(id)
                ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Account type deleted successfully",
                "Deleted successfully"
        );
    }

    @Override
    public ApiResponseDTO<String> deactivate(Long id) {

        AccountTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Account Type",
                        String.valueOf(id)
                ));

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        return ApiResponseDTO.success(
                "Account type deactivated successfully",
                "Deactivated successfully"
        );
    }
}