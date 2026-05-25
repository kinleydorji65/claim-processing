package com.claim.claim_processing.common.service.calculationMaster.impl;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationTriggerTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationTriggerTypeResponseDto;
import com.claim.claim_processing.common.entities.calculationMaster.CalculationTriggerTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.calculationMaster.CalculationTriggerTypeMapper;
import com.claim.claim_processing.common.repository.calculationMaster.CalculationTriggerTypeRepository;
import com.claim.claim_processing.common.service.calculationMaster.CalculationTriggerTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalculationTriggerTypeServiceImpl implements CalculationTriggerTypeService {

    private final CalculationTriggerTypeRepository repository;
    private final CalculationTriggerTypeMapper mapper;

    @Override
    public ApiResponseDTO<CalculationTriggerTypeResponseDto> create(
            CalculationTriggerTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Calculation trigger type already exists with code: " + dto.getCode()
            );
        }

        CalculationTriggerTypeMaster entity = mapper.toEntity(dto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        CalculationTriggerTypeMaster saved = repository.save(entity);

        return ApiResponseDTO.created(mapper.toDto(saved));
    }

    @Override
    public ApiResponseDTO<CalculationTriggerTypeResponseDto> update(
            Long id,
            CalculationTriggerTypeRequestDto dto) {

        CalculationTriggerTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Calculation trigger type not found with id: " + id
                ));

        if (dto.getCode() != null
                && !dto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Calculation trigger type already exists with code: " + dto.getCode()
            );
        }

        mapper.patchEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        CalculationTriggerTypeMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Calculation trigger type updated successfully",
                mapper.toDto(updated)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<CalculationTriggerTypeResponseDto> getById(Long id) {

        CalculationTriggerTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Calculation trigger type not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Calculation trigger type fetched successfully",
                mapper.toDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<CalculationTriggerTypeResponseDto> getByCode(String code) {

        CalculationTriggerTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Calculation trigger type not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Calculation trigger type fetched successfully",
                mapper.toDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<CalculationTriggerTypeResponseDto>> getAll() {

        List<CalculationTriggerTypeResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponseDTO.success(
                "Calculation trigger types fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<CalculationTriggerTypeResponseDto>> getAllActive() {

        List<CalculationTriggerTypeResponseDto> response = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponseDTO.success(
                "Active calculation trigger types fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        CalculationTriggerTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Calculation trigger type not found with id: " + id
                ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Calculation trigger type deleted successfully",
                "Deleted successfully"
        );
    }
}