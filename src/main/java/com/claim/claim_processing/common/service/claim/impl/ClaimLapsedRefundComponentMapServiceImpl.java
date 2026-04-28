package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundComponentMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundComponentMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.mapper.claim.ClaimLapsedRefundComponentMapMapper;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundComponentMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimLapsedRefundComponentMapServiceImpl
        implements ClaimLapsedRefundComponentMapService {

    private final ClaimLapsedRefundComponentMapRepository repository;
    private final ClaimLapsedRefundRepository ruleRepository;
    private final BenefitComponentTypeMasterRepository benefitRepository;
    private final ClaimLapsedRefundComponentMapMapper mapper;

    // ---------------------------------------------------
    // CREATE
    // ---------------------------------------------------
    @Override
    @Transactional
    public ClaimLapsedRefundComponentMapResponseDto create(
            ClaimLapsedRefundComponentMapRequestDto dto
    ) {

        // 1. DUPLICATE CHECK
        boolean exists = repository
                .existsByRule_IdAndBenefitComponentType_Id(
                        dto.getRuleId(),
                        dto.getBenefitComponentTypeId()
                );

        if (exists) {
            throw new RuntimeException("Mapping already exists for Rule + Benefit Component Type");
        }

        // 2. FETCH FK ENTITIES
        ClaimLapsedRefundMaster rule = ruleRepository.findById(dto.getRuleId())
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        BenefitComponentTypeMaster benefit = benefitRepository.findById(dto.getBenefitComponentTypeId())
                .orElseThrow(() -> new RuntimeException("Benefit Component not found"));

        // 3. MAP DTO → ENTITY
        ClaimLapsedRefundComponentMap entity = mapper.toEntity(dto);

        // 4. SET FK RELATIONS (IMPORTANT)
        entity.setRule(rule);
        entity.setBenefitComponentType(benefit);

        // 5. SAVE
        entity = repository.save(entity);

        // 6. RESPONSE
        return mapper.toDto(entity);
    }

    // ---------------------------------------------------
    // UPDATE
    // ---------------------------------------------------
    @Override
    @Transactional
    public ClaimLapsedRefundComponentMapResponseDto update(
            ClaimLapsedRefundComponentMapRequestDto dto
    ) {

        ClaimLapsedRefundComponentMap entity = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Mapping not found"));

        // FK validation + update
        if (dto.getRuleId() != null) {
            ClaimLapsedRefundMaster rule = ruleRepository.findById(dto.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule not found"));
            entity.setRule(rule);
        }

        if (dto.getBenefitComponentTypeId() != null) {
            BenefitComponentTypeMaster benefit = benefitRepository.findById(dto.getBenefitComponentTypeId())
                    .orElseThrow(() -> new RuntimeException("Benefit Component not found"));
            entity.setBenefitComponentType(benefit);
        }

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        entity.setUpdatedBy(dto.getUpdatedBy());

        entity = repository.save(entity);

        return mapper.toDto(entity);
    }

    // ---------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public ClaimLapsedRefundComponentMapResponseDto getById(Long id) {

        ClaimLapsedRefundComponentMap entity =
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Mapping not found"));

        return mapper.toDto(entity);
    }

    // ---------------------------------------------------
    // GET BY RULE ID
    // ---------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ClaimLapsedRefundComponentMapResponseDto> getByRuleId(Long ruleId) {

        return repository.findByRule_Id(ruleId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // ---------------------------------------------------
    // DELETE
    // ---------------------------------------------------
    @Override
    @Transactional
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Mapping not found");
        }

        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimLapsedRefundComponentMapResponseDto> getByBenefitComponentTypeId(Long benefitComponentTypeId) {

        return repository.findByBenefitComponentType_Id(benefitComponentTypeId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}