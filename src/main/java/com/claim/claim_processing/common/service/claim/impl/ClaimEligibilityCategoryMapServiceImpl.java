package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityCategoryMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.mapper.claim.ClaimEligibilityCategoryMapMapper;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityRepository;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityCategoryMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimEligibilityCategoryMapServiceImpl
        implements ClaimEligibilityCategoryMapService {

    private final ClaimEligibilityCategoryMapRepository repository;
    private final ClaimEligibilityRepository ruleRepository;
    private final AgencyCategoryRepository categoryRepository;
    private final ClaimEligibilityCategoryMapMapper mapper;

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @Override
    @Transactional
    public ClaimEligibilityCategoryMapResponseDto create(
            ClaimEligibilityCategoryMapRequestDto dto) {

        // 1. DUPLICATE CHECK (based on UNIQUE constraint)
        boolean exists = repository.existsByRule_IdAndCategory_CategoryId(
                dto.getRuleId(),
                dto.getCategoryId()
        );

        if (exists) {
            throw new RuntimeException(
                    "Mapping already exists for RuleId="
                            + dto.getRuleId()
                            + " and CategoryId="
                            + dto.getCategoryId()
            );
        }

        // 2. MAP DTO → ENTITY (structure only)
        ClaimEligibilityCategoryMap entity = mapper.toEntity(dto);

        // 3. FETCH FK ENTITIES (VALIDATION)
        ClaimEligibilityMaster rule = ruleRepository.findById(dto.getRuleId())
                .orElseThrow(() -> new RuntimeException(
                        "Rule not found: " + dto.getRuleId()
                ));

        AgencyCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Category not found: " + dto.getCategoryId()
                ));

        // 4. SET RELATIONS
        entity.setRule(rule);
        entity.setCategory(category);

        // 5. SAVE
        entity = repository.save(entity);

        // 6. RESPONSE
        return mapper.toResponseDto(entity);
    }

    // -------------------------------------------------
    // GET ALL
    // -------------------------------------------------
    @Override
    public List<ClaimEligibilityCategoryMapResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // -------------------------------------------------
    // GET BY RULE ID
    // -------------------------------------------------
    @Override
    public List<ClaimEligibilityCategoryMapResponseDto> getByRuleId(Long ruleId) {
        return mapper.toResponseDtoList(repository.findByRule_Id(ruleId));
    }

    // -------------------------------------------------
    // DELETE
    // -------------------------------------------------
    @Override
    @Transactional
    public void delete(Long id) {

        ClaimEligibilityCategoryMap entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Mapping not found: " + id
                ));

        repository.delete(entity);
    }

    @Override
    public List<ClaimEligibilityCategoryMapResponseDto> getByCategoryId(String categoryId) {

        return mapper.toResponseDtoList(
                repository.findByCategory_CategoryId(categoryId)
        );
    }
}