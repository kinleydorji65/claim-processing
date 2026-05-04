package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.claim.CessationTypeMapper;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;
import com.claim.claim_processing.common.service.claim.CessationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CessationTypeServiceImpl implements CessationTypeService {

    private final CessationTypeRepository repository;
    private final ClaimCircumstanceRepository circumstanceRepository;
    private final CessationTypeMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public CessationTypeResponseDto create(CessationTypeCreateRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Cessation type already exists: " + dto.getCode());
        }

        CessationTypeMaster entity = mapper.toEntity(dto);

        entity.setClaimCircumstance(getCircumstance(dto.getClaimCircumstanceId()));
        entity.setIsActive(ActivityEnum.Y);

        return mapper.toResponseDto(repository.save(entity));
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public CessationTypeResponseDto update(Long id, CessationTypeUpdateRequestDto dto) {

        CessationTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("CessationType not found: " + id));

        mapper.updateEntityFromDto(dto, entity);

        if (dto.getClaimCircumstanceId() != null) {
            entity.setClaimCircumstance(getCircumstance(dto.getClaimCircumstanceId()));
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public CessationTypeResponseDto getById(Long id) {

        return mapper.toResponseDto(
                repository.findById(id)
                        .orElseThrow(() -> ClaimException.notFound("CessationType not found: " + id))
        );
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<CessationTypeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // =========================
    // GET ACTIVE
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<CessationTypeResponseDto> getActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // =========================
    // GET BY CLAIM CIRCUMSTANCE
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<CessationTypeResponseDto> getByClaimCircumstance(Long circumstanceId) {

        ClaimCircumstanceMaster circumstance = getCircumstance(circumstanceId);

        return mapper.toResponseDtoList(
                repository.findByClaimCircumstance(circumstance)
        );
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void delete(Long id) {

        CessationTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("CessationType not found: " + id));

        repository.delete(entity);
    }

    // =========================
    // FK HELPER
    // =========================
    private ClaimCircumstanceMaster getCircumstance(Long id) {
        return circumstanceRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("ClaimCircumstance not found: " + id));
    }
}