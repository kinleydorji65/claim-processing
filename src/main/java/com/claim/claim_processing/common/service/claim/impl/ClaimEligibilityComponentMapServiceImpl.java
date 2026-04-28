package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityComponentMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityComponentMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.mapper.claim.ClaimEligibilityComponentMapMapper;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityComponentMapService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimEligibilityComponentMapServiceImpl
        implements ClaimEligibilityComponentMapService {

    private final ClaimEligibilityComponentMapRepository repository;
    private final ClaimEligibilityRepository ruleRepository;
    private final BenefitComponentTypeMasterRepository benefitRepository;
    private final ClaimEligibilityComponentMapMapper mapper;

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @Override
    @Transactional
    public ClaimEligibilityComponentMapResponseDto create(
            ClaimEligibilityComponentMapRequestDto dto) {

        // duplicate check
        if (repository.existsByRule_IdAndBenefitComponentType_Id(
                dto.getRuleId(),
                dto.getBenefitComponentTypeId()
        )) {
            throw new RuntimeException("Duplicate mapping exists");
        }

        ClaimEligibilityMaster rule = ruleRepository.findById(dto.getRuleId())
                .orElseThrow(() -> new EntityNotFoundException("Rule not found: " + dto.getRuleId()));

        BenefitComponentTypeMaster benefit = benefitRepository.findById(dto.getBenefitComponentTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Benefit type not found: " + dto.getBenefitComponentTypeId()));

        ClaimEligibilityComponentMap entity = mapper.toEntity(dto);
        entity.setRule(rule);
        entity.setBenefitComponentType(benefit);

        return mapper.toResponseDto(repository.save(entity));
    }

    // -------------------------------------------------
    // UPDATE
    // -------------------------------------------------
    @Override
    @Transactional
    public ClaimEligibilityComponentMapResponseDto update(
            ClaimEligibilityComponentMapRequestDto dto) {

        ClaimEligibilityComponentMap entity = repository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Mapping not found: " + dto.getId()));

        ClaimEligibilityMaster rule = ruleRepository.findById(dto.getRuleId())
                .orElseThrow(() -> new EntityNotFoundException("Rule not found"));

        BenefitComponentTypeMaster benefit = benefitRepository.findById(dto.getBenefitComponentTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Benefit type not found"));

        entity.setRule(rule);
        entity.setBenefitComponentType(benefit);
        entity.setIsActive(dto.getIsActive());

        return mapper.toResponseDto(repository.save(entity));
    }

    // -------------------------------------------------
    // GET BY ID
    // -------------------------------------------------
    @Override
    public ClaimEligibilityComponentMapResponseDto getById(Long id) {

        return repository.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Mapping not found: " + id));
    }

    // -------------------------------------------------
    // GET ALL
    // -------------------------------------------------
    @Override
    public List<ClaimEligibilityComponentMapResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // -------------------------------------------------
    // GET BY RULE ID
    // -------------------------------------------------
    @Override
    public List<ClaimEligibilityComponentMapResponseDto> getByRuleId(Long ruleId) {
        return mapper.toResponseDtoList(repository.findByRule_Id(ruleId));
    }

    // -------------------------------------------------
    // DELETE
    // -------------------------------------------------
    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Mapping not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<ClaimEligibilityComponentMapResponseDto> getByBenefitComponentTypeId(Long benefitComponentTypeId) {
        return mapper.toResponseDtoList(
                repository.findByBenefitComponentType_Id(benefitComponentTypeId)
        );
    }

}