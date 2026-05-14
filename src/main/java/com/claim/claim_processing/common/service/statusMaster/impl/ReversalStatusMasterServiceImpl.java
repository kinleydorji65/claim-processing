package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.ReversalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.ReversalStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.ReversalStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.ReversalStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.ReversalStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.ReversalStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReversalStatusMasterServiceImpl implements ReversalStatusMasterService {

    private final ReversalStatusMasterRepository repository;
    private final ReversalStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApiResponseDTO<ReversalStatusResponseDto> create(ReversalStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Reversal status code already exists: " + dto.getCode()
            );
        }

        ReversalStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        ReversalStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // ================= UPDATE =================
    @Override
    public ApiResponseDTO<ReversalStatusResponseDto> update(Long id, ReversalStatusRequestDto dto) {

        ReversalStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Reversal status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Reversal status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        ReversalStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ================= GET BY ID =================
    @Override
    public ApiResponseDTO<ReversalStatusResponseDto> getById(Long id) {

        ReversalStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Reversal status not found with id: " + id
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET BY CODE =================
    @Override
    public ApiResponseDTO<ReversalStatusResponseDto> getByCode(String code) {

        ReversalStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Reversal status not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET ALL =================
    @Override
    public ApiResponseDTO<List<ReversalStatusResponseDto>> getAll() {

        List<ReversalStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(ReversalStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public ApiResponseDTO<List<ReversalStatusResponseDto>> getAllActive() {

        List<ReversalStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(ReversalStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ReversalStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Reversal status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);

        return ApiResponseDTO.success("Reversal status deleted successfully");
    }
}