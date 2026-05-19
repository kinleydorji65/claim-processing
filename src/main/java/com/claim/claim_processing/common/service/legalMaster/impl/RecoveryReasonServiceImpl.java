package com.claim.claim_processing.common.service.legalMaster.impl;

import com.claim.claim_processing.common.DTO.request.legalMaster.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.legalMaster.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legalMaster.RecoveryReasonUpdateDto;
import com.claim.claim_processing.common.entities.legalMaster.RecoveryReasonMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.legalMaster.RecoveryReasonMapper;
import com.claim.claim_processing.common.repository.legalMaster.RecoveryReasonRepository;
import com.claim.claim_processing.common.service.legalMaster.RecoveryReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecoveryReasonServiceImpl implements RecoveryReasonService {

    private final RecoveryReasonRepository repository;
    private final RecoveryReasonMapper mapper;

    @Override
    public ApiResponseDTO<RecoveryReasonResponseDto> create(RecoveryReasonRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Recovery Reason code already exists: " + dto.getCode());
        }

        RecoveryReasonMaster entity = mapper.toEntity(dto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        RecoveryReasonMaster saved = repository.save(entity);

        return ApiResponseDTO.created(mapper.toResponseDto(saved));
    }

    @Override
    public ApiResponseDTO<RecoveryReasonResponseDto> update(Long id, RecoveryReasonUpdateDto dto) {

        RecoveryReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound("Recovery Reason", String.valueOf(id)));

        // PATCH behavior (only non-null fields updated)
        mapper.updateEntityFromDto(dto, entity);
        entity.setUpdatedBy(dto.getUpdatedBy());

        RecoveryReasonMaster updated = repository.save(entity);

        return ApiResponseDTO.success("Updated successfully", mapper.toResponseDto(updated));
    }

    @Override
    public ApiResponseDTO<RecoveryReasonResponseDto> getById(Long id) {

        RecoveryReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound("Recovery Reason", String.valueOf(id)));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<RecoveryReasonResponseDto> getByCode(String code) {

        RecoveryReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.resourceNotFound("Recovery Reason", code));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<List<RecoveryReasonResponseDto>> getAll() {

        List<RecoveryReasonResponseDto> list =
                repository.findAll().stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(list);
    }

    @Override
    public ApiResponseDTO<List<RecoveryReasonResponseDto>> getAllActive() {

        List<RecoveryReasonResponseDto> list =
                repository.findByIsActive(ActivityEnum.Y).stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(list);
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        RecoveryReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Recovery Reason",
                        String.valueOf(id)
                ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Recovery reason deleted successfully",
                "Deleted successfully"
        );
    }
}