package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleMasterResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.mapper.claim.ClaimVestingRuleMasterMapper;
import com.claim.claim_processing.common.repository.claim.ClaimVestingRuleMasterRepository;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.service.claim.ClaimVestingRuleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimVestingRuleMasterServiceImpl implements ClaimVestingRuleMasterService {

    private final ClaimVestingRuleMasterRepository repository;
    private final AgencyCategoryRepository categoryRepository;
    private final ClaimVestingRuleMasterMapper mapper;

    // -----------------------------
    // CREATE RULE
    // -----------------------------
    @Override
    public ClaimVestingRuleMasterResponseDto createRule(ClaimVestingRuleMasterRequestDto requestDto) {

        // 1. Validate ruleCode uniqueness
        repository.findByRuleCode(requestDto.getRuleCode())
                .ifPresent(r -> {
                    throw new RuntimeException("Rule code already exists: " + requestDto.getRuleCode());
                });

        // 2. Map DTO → Entity
        ClaimVestingRuleMaster entity = mapper.toEntity(requestDto);

        // 3. FK resolution (IMPORTANT)
        AgencyCategory category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + requestDto.getCategoryId()));

        entity.setCategory(category);

        // 4. Save
        ClaimVestingRuleMaster saved = repository.save(entity);

        // 5. Entity → Response DTO
        return mapper.toResponseDto(saved);
    }

    // -----------------------------
    // UPDATE RULE
    // -----------------------------
    @Override
    public ClaimVestingRuleMasterResponseDto updateRule(Long id, ClaimVestingRuleMasterRequestDto requestDto) {

        ClaimVestingRuleMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));

        // 1. Update scalar fields
        mapper.updateEntityFromDto(requestDto, entity);

        // 2. FK resolution
        if (requestDto.getCategoryId() != null) {
            AgencyCategory category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + requestDto.getCategoryId()));

            entity.setCategory(category);
        }

        // 3. Save
        ClaimVestingRuleMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ClaimVestingRuleMasterResponseDto getById(Long id) {
        ClaimVestingRuleMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));

        return mapper.toResponseDto(entity);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ClaimVestingRuleMasterResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    // -----------------------------
    // GET BY CATEGORY
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ClaimVestingRuleMasterResponseDto> getByCategoryId(String categoryId) {
        return repository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public void deleteRule(Long id) {
        ClaimVestingRuleMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));

        repository.delete(entity);
    }
}