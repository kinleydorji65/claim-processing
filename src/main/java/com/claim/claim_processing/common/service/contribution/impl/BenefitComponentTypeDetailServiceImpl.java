package com.claim.claim_processing.common.service.contribution.impl;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentDetailRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentDetailResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.mapper.contribution.BenefitComponentTypeDetailMapper;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeDetailRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.service.contribution.BenefitComponentTypeDetailService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BenefitComponentTypeDetailServiceImpl implements BenefitComponentTypeDetailService {

    private final BenefitComponentTypeDetailRepository repository;
    private final BenefitComponentTypeMasterRepository benefitRepo;
    private final ComponentMasterRepository componentRepo;
    private final BenefitComponentTypeDetailMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public BenefitComponentDetailResponseDto create(BenefitComponentDetailRequestDto dto) {

        BenefitComponentTypeDetail entity = mapper.toEntity(dto);

        entity.setBenefitComponentType(getBenefit(dto.getBenefitComponentTypeId()));
        entity.setComponent(getComponent(dto.getComponentId()));
        entity.setIsActive(ActivityEnum.Y);

        return mapper.toResponseDto(repository.save(entity));
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public BenefitComponentDetailResponseDto update(Long id, BenefitComponentDetailRequestDto dto) {

        BenefitComponentTypeDetail entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("BenefitComponentTypeDetail", id.toString()));

        mapper.updateEntityFromDto(dto, entity);

        if (dto.getBenefitComponentTypeId() != null) {
            entity.setBenefitComponentType(getBenefit(dto.getBenefitComponentTypeId()));
        }

        if (dto.getComponentId() != null) {
            entity.setComponent(getComponent(dto.getComponentId()));
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public BenefitComponentDetailResponseDto getById(Long id) {

        BenefitComponentTypeDetail entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("BenefitComponentTypeDetail", id.toString()));

        return mapper.toResponseDto(entity);
    }

    // =========================
    // GET ALL ACTIVE
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<BenefitComponentDetailResponseDto> getAllActive() {

        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // =========================
    // FK FILTERS
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<BenefitComponentDetailResponseDto> getByBenefitComponentTypeId(Long id) {

        return mapper.toResponseDtoList(
                repository.findByBenefitComponentType_Id(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitComponentDetailResponseDto> getByComponentId(Long id) {

        return mapper.toResponseDtoList(
                repository.findByComponent_Id(id)
        );
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void delete(Long id) {

        BenefitComponentTypeDetail entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("BenefitComponentTypeDetail", id.toString()));

        repository.delete(entity);
    }

    // =========================
    // FK HELPERS
    // =========================
    private BenefitComponentTypeMaster getBenefit(Long id) {
        return benefitRepo.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("BenefitComponentTypeMaster", id.toString()));
    }

    private ComponentMaster getComponent(Long id) {
        return componentRepo.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("ComponentMaster", id.toString()));
    }
}