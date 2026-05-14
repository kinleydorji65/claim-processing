package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.RuleEvaluationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.RuleEvaluationStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.RuleEvaluationStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.RuleEvaluationStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.RuleEvaluationStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.RuleEvaluationStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleEvaluationStatusMasterServiceImpl
        implements RuleEvaluationStatusMasterService {

    private final RuleEvaluationStatusMasterRepository repository;
    private final RuleEvaluationStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApiResponseDTO<RuleEvaluationStatusResponseDto> create(RuleEvaluationStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Rule evaluation status code already exists: " + dto.getCode()
            );
        }

        RuleEvaluationStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        RuleEvaluationStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // ================= UPDATE =================
    @Override
    public ApiResponseDTO<RuleEvaluationStatusResponseDto> update(Long id, RuleEvaluationStatusRequestDto dto) {

        RuleEvaluationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rule evaluation status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Rule evaluation status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        RuleEvaluationStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ================= GET BY ID =================
    @Override
    public ApiResponseDTO<RuleEvaluationStatusResponseDto> getById(Long id) {

        RuleEvaluationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rule evaluation status not found with id: " + id
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET BY CODE =================
    @Override
    public ApiResponseDTO<RuleEvaluationStatusResponseDto> getByCode(String code) {

        RuleEvaluationStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rule evaluation status not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET ALL =================
    @Override
    public ApiResponseDTO<List<RuleEvaluationStatusResponseDto>> getAll() {

        List<RuleEvaluationStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(RuleEvaluationStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public ApiResponseDTO<List<RuleEvaluationStatusResponseDto>> getAllActive() {

        List<RuleEvaluationStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(RuleEvaluationStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        RuleEvaluationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rule evaluation status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);

        return ApiResponseDTO.success("Rule evaluation status deleted successfully");
    }
}