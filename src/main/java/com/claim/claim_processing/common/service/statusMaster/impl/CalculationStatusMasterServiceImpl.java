package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.CalculationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.CalculationStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.CalculationStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.CalculationStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.CalculationStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.CalculationStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationStatusMasterServiceImpl implements CalculationStatusMasterService {

    private final CalculationStatusMasterRepository repository;
    private final CalculationStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApiResponseDTO<CalculationStatusResponseDto> create(CalculationStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Calculation status code already exists: " + dto.getCode()
            );
        }

        CalculationStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        CalculationStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // ================= UPDATE =================
    @Override
    public ApiResponseDTO<CalculationStatusResponseDto> update(Long id, CalculationStatusRequestDto dto) {

        CalculationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Calculation status not found with id: " + id
                        )
                );

        // Prevent duplicate code update
        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Calculation status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        CalculationStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ================= GET BY ID =================
    @Override
    public ApiResponseDTO<CalculationStatusResponseDto> getById(Long id) {

        CalculationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Calculation status not found with id: " + id
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET BY CODE =================
    @Override
    public ApiResponseDTO<CalculationStatusResponseDto> getByCode(String code) {

        CalculationStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Calculation status not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET ALL =================
    @Override
    public ApiResponseDTO<List<CalculationStatusResponseDto>> getAll() {

        List<CalculationStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(CalculationStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public ApiResponseDTO<List<CalculationStatusResponseDto>> getAllActive() {

        List<CalculationStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(CalculationStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= DELETE (Soft Delete) =================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        CalculationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Calculation status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);

        return ApiResponseDTO.success("Calculation status deleted successfully");
    }
}