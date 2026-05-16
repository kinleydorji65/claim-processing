package com.claim.claim_processing.common.service.calculationMaster.impl;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationStageRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationStageResponseDto;
import com.claim.claim_processing.common.entities.calculationMaster.CalculationStageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.calculationMaster.CalculationStageMasterMapper;
import com.claim.claim_processing.common.repository.calculationMaster.CalculationStageMasterRepository;
import com.claim.claim_processing.common.service.calculationMaster.CalculationStageMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalculationStageMasterServiceImpl implements CalculationStageMasterService {

    private final CalculationStageMasterRepository repository;
    private final CalculationStageMasterMapper mapper;

    @Override
    public ApiResponseDTO<CalculationStageResponseDto> create(CalculationStageRequestDto request) {

        if (repository.existsByCode(request.getCode())) {
            throw ClaimException.conflict("Calculation stage already exists with code: " + request.getCode());
        }

        CalculationStageMaster entity = mapper.toEntity(request);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(request.getCreatedBy());
        entity.setCreatedBy(request.getUpdatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        CalculationStageMaster saved = repository.save(entity);

        return ApiResponseDTO.created(mapper.toDto(saved));
    }

    @Override
    public ApiResponseDTO<CalculationStageResponseDto> update(Long id, CalculationStageRequestDto request) {

        CalculationStageMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Calculation stage not found with id: " + id));

        if (request.getCode() != null
                && !request.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(request.getCode())) {
            throw ClaimException.conflict("Calculation stage already exists with code: " + request.getCode());
        }

        mapper.updateFromDto(request, entity);
        entity.setUpdatedBy(request.getUpdatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        CalculationStageMaster updated = repository.save(entity);

        return ApiResponseDTO.success("Calculation stage updated successfully", mapper.toDto(updated));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<CalculationStageResponseDto> getById(Long id) {

        CalculationStageMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Calculation stage not found with id: " + id));

        return ApiResponseDTO.success("Calculation stage fetched successfully", mapper.toDto(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<CalculationStageResponseDto> getByCode(String code) {

        CalculationStageMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound("Calculation stage not found with code: " + code));

        return ApiResponseDTO.success("Calculation stage fetched successfully", mapper.toDto(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<CalculationStageResponseDto>> getAll() {

        List<CalculationStageResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponseDTO.success("Calculation stages fetched successfully", response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<CalculationStageResponseDto>> getAllActive() {

        List<CalculationStageResponseDto> response = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponseDTO.success("Active calculation stages fetched successfully", response);
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        CalculationStageMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Calculation stage not found with id: " + id));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Calculation stage deleted successfully",
                "Deleted successfully"
        );
    }
}