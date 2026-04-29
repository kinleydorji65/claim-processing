package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundCategoryMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.mapper.claim.ClaimLapsedRefundCategoryMapMapper;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundRepository;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundCategoryMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimLapsedRefundCategoryMapServiceImpl
        implements ClaimLapsedRefundCategoryMapService {

    private final ClaimLapsedRefundCategoryMapRepository repository;
    private final ClaimLapsedRefundRepository ruleRepository;
    private final AgencyCategoryRepository categoryRepository;
    private final ClaimLapsedRefundCategoryMapMapper mapper;

    // =====================================================
    // CREATE
    // =====================================================
    @Override
    public ClaimLapsedRefundCategoryMapResponseDto create(
            ClaimLapsedRefundCategoryMapRequestDto requestDto
    ) {

        ClaimLapsedRefundMaster rule = ruleRepository.findById(requestDto.getRuleId())
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        AgencyCategory category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ClaimLapsedRefundCategoryMap entity = mapper.toEntity(requestDto);

        entity.setRule(rule);
        entity.setCategory(category);

        return mapper.toDto(repository.save(entity));
    }

    // =====================================================
    // UPDATE
    // =====================================================
    @Override
    public ClaimLapsedRefundCategoryMapResponseDto update(
            Long id,
            ClaimLapsedRefundCategoryMapRequestDto requestDto
    ) {

        ClaimLapsedRefundCategoryMap entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mapping not found"));

        ClaimLapsedRefundMaster rule = ruleRepository.findById(requestDto.getRuleId())
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        AgencyCategory category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setRule(rule);
        entity.setCategory(category);

        return mapper.toDto(repository.save(entity));
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @Override
    public ClaimLapsedRefundCategoryMapResponseDto getById(Long id) {
        ClaimLapsedRefundCategoryMap entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mapping not found"));

        return mapper.toDto(entity);
    }

    // =====================================================
    // GET BY RULE ID
    // =====================================================
    @Override
    public List<ClaimLapsedRefundCategoryMapResponseDto> getByRuleId(Long ruleId) {
        return repository.findByRule_Id(ruleId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // =====================================================
    // DELETE
    // =====================================================
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Mapping not found");
        }
        repository.deleteById(id);
    }

    @Override
    public List<ClaimLapsedRefundCategoryMapResponseDto> getByCategoryId(String categoryId) {

        return repository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}