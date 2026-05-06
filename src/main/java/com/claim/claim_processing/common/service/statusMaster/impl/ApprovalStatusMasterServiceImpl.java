package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.ApprovalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.ApprovalStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.ApprovalStatusMaster;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.statusMaster.ApprovalStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.ApprovalStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.ApprovalStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalStatusMasterServiceImpl implements ApprovalStatusMasterService {

    private final ApprovalStatusMasterRepository repository;
    private final ApprovalStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApprovalStatusResponseDto create(ApprovalStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Code already exists: " + dto.getCode());
        }

        ApprovalStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        ApprovalStatusMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public ApprovalStatusResponseDto update(Long id, ApprovalStatusRequestDto dto) {

        ApprovalStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Approval Status not found with id: " + id)
                );

        // If code is changing, check duplicate
        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Code already exists: " + dto.getCode());
        }

        mapper.updateEntity(entity, dto);

        ApprovalStatusMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public ApprovalStatusResponseDto getById(Long id) {

        ApprovalStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Approval Status not found with id: " + id)
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public ApprovalStatusResponseDto getByCode(String code) {

        ApprovalStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Approval Status not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<ApprovalStatusResponseDto> getAll() {

        return mapper.toResponseDtoList(repository.findAll());
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<ApprovalStatusResponseDto> getAllActive() {

        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // ================= DELETE (soft delete) =================
    @Override
    public void delete(Long id) {

        ApprovalStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Approval Status not found with id: " + id)
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}