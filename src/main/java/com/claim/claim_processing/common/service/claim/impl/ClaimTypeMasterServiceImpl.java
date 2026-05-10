package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.ClaimTypeMasterMapper;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.service.claim.ClaimTypeMasterService;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimTypeMasterServiceImpl implements ClaimTypeMasterService {

    private final ClaimTypeMasterRepository repository;
    private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;
    private final RuleTypeRepository ruleTypeRepository;
    private final ClaimTypeMasterMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto create(ClaimTypeMasterRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Claim Type code already exists: " + requestDto.getCode());
        }

        ClaimTypeMaster entity = mapper.toEntity(requestDto);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        repository.save(entity);
        List<ClaimTypeRuleMap> mappings = mapRulesToClaimType(entity, requestDto.getRuleTypeIds());
        return mapper.toResponseDto(entity, mappings);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto update(Long id, ClaimTypeMasterRequestDto requestDto) {

        ClaimTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with id: " + id));

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setUpdatedAt(LocalDateTime.now());
        List<ClaimTypeRuleMap> mappings = mapRulesToClaimType(entity, requestDto.getRuleTypeIds());
        return mapper.toResponseDto(repository.save(entity), mappings);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto getById(Long id) {
        ClaimTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with id: " + id));

        List<ClaimTypeRuleMap> mappings = claimTypeRuleMapRepository.findByClaimTypeId(entity.getId());
        return mapper.toResponseDto(entity, mappings);
    }

    // -----------------------------
    // GET BY CODE (IMPORTANT FOR RULE ENGINE)
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto getByCode(String code) {
        ClaimTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with code: " + code));
        
        List<ClaimTypeRuleMap> mappings = claimTypeRuleMapRepository.findByClaimTypeId(entity.getId());
        return mapper.toResponseDto(entity, mappings);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public List<ClaimTypeMasterResponseDto> getAll() {
        List<ClaimTypeMasterResponseDto> responseDtos = repository.findAll().stream().map(entity -> {
            List<ClaimTypeRuleMap> mappings = claimTypeRuleMapRepository.findByClaimTypeId(entity.getId());
            return mapper.toResponseDto(entity, mappings);
        }).toList();
        return responseDtos;
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public List<ClaimTypeMasterResponseDto> getAllActive() {
        List<ClaimTypeMasterResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y).stream().map(entity -> {
            List<ClaimTypeRuleMap> mappings = claimTypeRuleMapRepository.findByClaimTypeId(entity.getId());
            return mapper.toResponseDto(entity, mappings);
        }).toList();
        return responseDtos;
    }

    // -----------------------------
    // DELETE (soft delete recommended in future)
    // -----------------------------
    @Override
    public void delete(Long id) {
        ClaimTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with id: " + id));

        repository.delete(entity);
    }

    private List<ClaimTypeRuleMap> mapRulesToClaimType(ClaimTypeMaster claimType, List<Long> ruleTypeIds) {
        if (ruleTypeIds == null || ruleTypeIds.isEmpty()) {
            throw ClaimException.notFound("Rule Type IDs cannot be null or empty");
        }

        List<ClaimTypeRuleMap> existingMappings = 
        claimTypeRuleMapRepository.findByClaimType_IdAndRuleType_IdIn(claimType.getId(), ruleTypeIds);

        List<ClaimTypeRuleMap> mappings;
        if (existingMappings.isEmpty()) {
            mappings = ruleTypeIds.stream().map(ruleId -> {
                
                validateDuplicate(claimType.getId(), ruleId);
                return ClaimTypeRuleMap.builder()
                        .claimType(claimType)
                        .ruleType(getRuleType(ruleId))
                        .build();
            }).toList();
        }else {
            mappings = existingMappings.stream()
                    .filter(map -> ruleTypeIds.contains(map.getRuleType().getId()))
                    .map(map -> {
                        map.setClaimType(map.getClaimType());
                        map.setRuleType(getRuleType(map.getRuleType().getId()));
                        return map;
                    })
                    .toList();
        }
        
        return mappings;
    }

    private void validateDuplicate(Long claimTypeId, Long ruleId) {
        if (claimTypeRuleMapRepository.existsByClaimTypeIdAndRuleTypeId(claimTypeId, ruleId)) {
            throw ClaimException.conflict("Mapping already exists");
        }
    }

    private RuleTypeMaster getRuleType(Long id) {
        return ruleTypeRepository.findById(id)
                .orElseThrow(() ->ClaimException.resourceNotFound("Rule Type", String.valueOf(id)));
    }

}