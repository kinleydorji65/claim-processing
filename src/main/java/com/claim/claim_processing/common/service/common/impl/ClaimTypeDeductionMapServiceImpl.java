package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.ClaimTypeDeductionMapRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimTypeDeductionMapResponseDto;
import com.claim.claim_processing.common.entities.common.ClaimTypeDeductionMap;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.ClaimTypeDeductionMapMapper;
import com.claim.claim_processing.common.repository.common.ClaimTypeDeductionMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.common.DeductionTypeRepository;
import com.claim.claim_processing.common.service.common.ClaimTypeDeductionMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimTypeDeductionMapServiceImpl implements ClaimTypeDeductionMapService {

    private final ClaimTypeDeductionMapRepository repository;
    private final ClaimTypeMasterRepository claimTypeRepo;
    private final DeductionTypeRepository deductionTypeRepo;
    private final ClaimTypeDeductionMapMapper mapper;

    // ---------------------------------------------------
    // CREATE
    // ---------------------------------------------------
    @Override
    public ClaimTypeDeductionMapResponseDto create(ClaimTypeDeductionMapRequestDto dto) {

        if (repository.existsByClaimType_IdAndDeductionType_Id(
                dto.getClaimTypeId(),
                dto.getDeductionTypeId()
        )) {
            throw new RuntimeException("Mapping already exists");
        }

        ClaimTypeDeductionMap entity = mapper.toEntity(dto);

        entity.setClaimType(
                claimTypeRepo.findById(dto.getClaimTypeId())
                        .orElseThrow(() -> new RuntimeException("ClaimType not found"))
        );

        entity.setDeductionType(
                deductionTypeRepo.findById(dto.getDeductionTypeId())
                        .orElseThrow(() -> new RuntimeException("DeductionType not found"))
        );

        return mapper.toDto(repository.save(entity));
    }

    // ---------------------------------------------------
    // UPDATE
    // ---------------------------------------------------
    @Override
    public ClaimTypeDeductionMapResponseDto update(Long id, ClaimTypeDeductionMapRequestDto dto) {

        ClaimTypeDeductionMap entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        entity.setClaimType(
                claimTypeRepo.findById(dto.getClaimTypeId())
                        .orElseThrow(() -> new RuntimeException("ClaimType not found"))
        );

        entity.setDeductionType(
                deductionTypeRepo.findById(dto.getDeductionTypeId())
                        .orElseThrow(() -> new RuntimeException("DeductionType not found"))
        );

        entity.setIsAllowed(dto.getIsAllowed());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setRemarks(dto.getRemarks());

        return mapper.toDto(repository.save(entity));
    }

    // ---------------------------------------------------
    // PATCH
    // ---------------------------------------------------
    @Override
    public ClaimTypeDeductionMapResponseDto patch(Long id, ClaimTypeDeductionMapRequestDto dto) {

        ClaimTypeDeductionMap entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (dto.getClaimTypeId() != null) {
            entity.setClaimType(
                    claimTypeRepo.findById(dto.getClaimTypeId())
                            .orElseThrow(() -> new RuntimeException("ClaimType not found"))
            );
        }

        if (dto.getDeductionTypeId() != null) {
            entity.setDeductionType(
                    deductionTypeRepo.findById(dto.getDeductionTypeId())
                            .orElseThrow(() -> new RuntimeException("DeductionType not found"))
            );
        }

        if (dto.getIsAllowed() != null) entity.setIsAllowed(dto.getIsAllowed());
        if (dto.getDisplayOrder() != null) entity.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getRemarks() != null) entity.setRemarks(dto.getRemarks());

        if (dto.getIsActive() != null) {
            entity.setIsActive(ActivityEnum.valueOf(dto.getIsActive()));
        }

        return mapper.toDto(repository.save(entity));
    }

    // ---------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------
    @Override
    public ClaimTypeDeductionMapResponseDto getById(Long id) {
        return mapper.toDto(
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Record not found"))
        );
    }

    // ---------------------------------------------------
    // GET ALL
    // ---------------------------------------------------
    @Override
    public List<ClaimTypeDeductionMapResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // ---------------------------------------------------
    // FK API: BY CLAIM TYPE
    // ---------------------------------------------------
    @Override
    public List<ClaimTypeDeductionMapResponseDto> getByClaimTypeId(Long claimTypeId) {
        return repository.findByClaimType_Id(claimTypeId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // ---------------------------------------------------
    // FK API: BY DEDUCTION TYPE
    // ---------------------------------------------------
    @Override
    public List<ClaimTypeDeductionMapResponseDto> getByDeductionTypeId(Long deductionTypeId) {
        return repository.findByDeductionType_Id(deductionTypeId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // ---------------------------------------------------
    // DELETE
    // ---------------------------------------------------
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}