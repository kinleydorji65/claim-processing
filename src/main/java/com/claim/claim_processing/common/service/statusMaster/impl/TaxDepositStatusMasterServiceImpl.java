package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.TaxDepositStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.TaxDepositStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.TaxDepositStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.TaxDepositStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.TaxDepositStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.TaxDepositStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxDepositStatusMasterServiceImpl implements TaxDepositStatusMasterService {

    private final TaxDepositStatusMasterRepository repository;
    private final TaxDepositStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApiResponseDTO<TaxDepositStatusResponseDto> create(TaxDepositStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Tax deposit status code already exists: " + dto.getCode()
            );
        }

        TaxDepositStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        TaxDepositStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // ================= UPDATE =================
    @Override
    public ApiResponseDTO<TaxDepositStatusResponseDto> update(Long id, TaxDepositStatusRequestDto dto) {

        TaxDepositStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Tax deposit status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Tax deposit status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        TaxDepositStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ================= GET BY ID =================
    @Override
    public ApiResponseDTO<TaxDepositStatusResponseDto> getById(Long id) {

        TaxDepositStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Tax deposit status not found with id: " + id
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET BY CODE =================
    @Override
    public ApiResponseDTO<TaxDepositStatusResponseDto> getByCode(String code) {

        TaxDepositStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Tax deposit status not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET ALL =================
    @Override
    public ApiResponseDTO<List<TaxDepositStatusResponseDto>> getAll() {

        List<TaxDepositStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(TaxDepositStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public ApiResponseDTO<List<TaxDepositStatusResponseDto>> getAllActive() {

        List<TaxDepositStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(TaxDepositStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        TaxDepositStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Tax deposit status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);

        return ApiResponseDTO.success("Tax deposit status deleted successfully");
    }
}