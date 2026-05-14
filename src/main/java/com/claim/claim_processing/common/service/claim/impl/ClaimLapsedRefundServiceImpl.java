package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.mapper.claim.ClaimLapsedRefundMapper;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimLapsedRefundServiceImpl implements ClaimLapsedRefundService {

    private final ClaimLapsedRefundRepository repository;
    private final ClaimLapsedRefundMapper mapper;
     private final ClaimLapsedRefundCategoryMapRepository categoryMapRepository;
    private final ClaimLapsedRefundComponentMapRepository componentMapRepository;
    private final BenefitComponentTypeMasterRepository benefitComponentTypeMasterRepository;

    private final AgencyCategoryRepository agencyCategoryRepository;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    @Transactional
    public ApiResponseDTO<ClaimLapsedRefundResponseDto> create(ClaimLapsedRefundRequestDto dto) {

        ClaimLapsedRefundMaster entity = mapper.toEntity(dto);
        ClaimLapsedRefundMaster saved = repository.save(entity);
        repository.flush();
        ClaimLapsedRefundCategoryMap mapMemberCategory = mapMemberCategory(saved, dto.getMemberCategoryId());
        ClaimLapsedRefundComponentMap benefitComponent = mapClaimLapsedBenefitComponent(saved, mapMemberCategory, dto.getBenefitTypeId(), null);

        List<AgencyCategory> agencyCategories = List.of(mapMemberCategory.getCategory());
        List<BenefitComponentTypeMaster> benefitComponents = List.of(benefitComponent.getBenefitComponentType());
        return ApiResponseDTO.success(mapper.toResponseDto(saved, agencyCategories, benefitComponents));
    }

    private ClaimLapsedRefundCategoryMap mapMemberCategory(ClaimLapsedRefundMaster rule, String categoryId) {

    AgencyCategory category = getMemberCategory(categoryId);

    ClaimLapsedRefundCategoryMap map =
            categoryMapRepository
                    .findByRule_IdAndCategory_CategoryId(rule.getId(), categoryId)
                    .orElseGet(ClaimLapsedRefundCategoryMap::new);

    map.setRule(rule);
    map.setCategory(category);

    return categoryMapRepository.save(map);
}

    private ClaimLapsedRefundComponentMap mapClaimLapsedBenefitComponent(ClaimLapsedRefundMaster rule, ClaimLapsedRefundCategoryMap categoryMap, Long benefitTypeId, Long existingBenefitId) {
        ClaimLapsedRefundComponentMap map;
        if (existingBenefitId != null) {
            map = componentMapRepository.findById(existingBenefitId)
                    .orElseGet(ClaimLapsedRefundComponentMap::new);
        }else {
            Boolean isDuplicate = checkDuplicateBenefitComponent(rule.getId(), categoryMap.getId(), benefitTypeId);
            if (isDuplicate) {
                throw new RuntimeException("Duplicate benefit component found");
            }
            map = new ClaimLapsedRefundComponentMap();
        }
            map.setClaimLapsedRefundCategoryMap(categoryMap);
            map.setRule(rule);
            map.setBenefitComponentType(getBenefitTypeComponent(benefitTypeId));
            componentMapRepository.save(map);
       
        return map;
    }

    private Boolean checkDuplicateBenefitComponent(Long ruleId, Long categoryMapId, Long benefitTypeId) {
        return componentMapRepository.existsByRule_IdAndClaimLapsedRefundCategoryMap_IdAndBenefitComponentType_Id(
                ruleId,
                categoryMapId,
                benefitTypeId
        );
    }

    private AgencyCategory getMemberCategory(String memberCategoryId) {
        return agencyCategoryRepository.findById(memberCategoryId)
                .orElseThrow(() -> new RuntimeException("Member category not found with id: " + memberCategoryId));
    }

    private BenefitComponentTypeMaster getBenefitTypeComponent(Long benefitTypeId) {
        return benefitComponentTypeMasterRepository.findById(benefitTypeId)
                .orElseThrow(() -> new RuntimeException("Benefit type not found with id: " + benefitTypeId));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    public ApiResponseDTO<ClaimLapsedRefundResponseDto> getById(Long id) {

        ClaimLapsedRefundMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Lapsed Refund not found"));
        List<AgencyCategory> agencyCategories = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
            .stream()
            .map(ClaimLapsedRefundCategoryMap::getCategory)
            .toList();
        List<Long> categoriesId = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
            .stream()
            .map(ClaimLapsedRefundCategoryMap::getId)
            .toList();
        List<BenefitComponentTypeMaster> benefitComponents = componentMapRepository.findByRule_IdAndClaimLapsedRefundCategoryMap_IdIn(entity.getRuleType().getId(), categoriesId)
            .stream()
            .map(ClaimLapsedRefundComponentMap::getBenefitComponentType)
            .toList();
        return ApiResponseDTO.success(mapper.toResponseDto(entity, agencyCategories, benefitComponents));
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @Override
public ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getAll() {
    List<ClaimLapsedRefundResponseDto> responseDtos = repository.findAll().stream().map(entity -> {
        List<AgencyCategory> agencyCategories = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
            .stream()
            .map(ClaimLapsedRefundCategoryMap::getCategory)
            .toList();
        
        // FIX: Get components directly by rule ID only
        List<BenefitComponentTypeMaster> benefitComponents = componentMapRepository
            .findByRule_Id(entity.getId())  // Add this method
            .stream()
            .map(ClaimLapsedRefundComponentMap::getBenefitComponentType)
            .collect(Collectors.toList());
        
        return mapper.toResponseDto(entity, agencyCategories, benefitComponents);
    }).toList();
    return ApiResponseDTO.success(responseDtos);
}

    // -------------------------------
    // UPDATE
    // -------------------------------
    @Override
    public ApiResponseDTO<ClaimLapsedRefundResponseDto> update(Long id, ClaimLapsedRefundRequestDto dto) {

        ClaimLapsedRefundMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Lapsed Refund not found"));

        mapper.updateEntityFromDto(dto, entity);

        ClaimLapsedRefundMaster updated = repository.save(entity);
        ClaimLapsedRefundCategoryMap mapMemberCategory = mapMemberCategory(updated, dto.getMemberCategoryId());
        ClaimLapsedRefundComponentMap benefitComponent = mapClaimLapsedBenefitComponent(updated, mapMemberCategory, dto.getBenefitTypeId(), dto.getExistingBenefitId());

        List<AgencyCategory> agencyCategories = List.of(mapMemberCategory.getCategory());
        List<BenefitComponentTypeMaster> benefitComponents = List.of(benefitComponent.getBenefitComponentType());
        return ApiResponseDTO.success(mapper.toResponseDto(updated, agencyCategories, benefitComponents));
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ClaimLapsedRefundMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Lapsed Refund not found"));

        repository.delete(entity);
        return ApiResponseDTO.success("Claim Lapsed Refund deleted successfully");
    }

    // -------------------------------
    // ACTIVE RULES
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getActiveRules() {
        List<ClaimLapsedRefundResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y).stream().map(entity -> {
            List<AgencyCategory> agencyCategories = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getCategory)
                .toList();
            List<Long> categoriesId = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getId)
                .toList();
            List<BenefitComponentTypeMaster> benefitComponents = componentMapRepository.findByRule_IdAndClaimLapsedRefundCategoryMap_IdIn(entity.getRuleType().getId(), categoriesId)
                .stream()
                .map(ClaimLapsedRefundComponentMap::getBenefitComponentType)
                .toList();
            return mapper.toResponseDto(entity, agencyCategories, benefitComponents);
        }).toList();
        return ApiResponseDTO.success(responseDtos);
    }

    // -------------------------------
    // GET BY RULE CODE
    // -------------------------------
    @Override
    public ApiResponseDTO<ClaimLapsedRefundResponseDto> getByRuleCode(String ruleCode) {

        ClaimLapsedRefundMaster entity = repository.findByRuleCode(ruleCode)
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        List<AgencyCategory> agencyCategories = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
            .stream()
            .map(ClaimLapsedRefundCategoryMap::getCategory)
            .toList();
        List<Long> categoriesId = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
            .stream()
            .map(ClaimLapsedRefundCategoryMap::getId)
            .toList();
        List<BenefitComponentTypeMaster> benefitComponents = componentMapRepository.findByRule_IdAndClaimLapsedRefundCategoryMap_IdIn(entity.getRuleType().getId(), categoriesId)
            .stream()
            .map(ClaimLapsedRefundComponentMap::getBenefitComponentType)
            .toList();
        return ApiResponseDTO.success(mapper.toResponseDto(entity, agencyCategories, benefitComponents));
    }

    // -------------------------------
    // FK FILTER METHODS
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getByClaimCircumstance(Long claimCircumstanceId) {
        List<ClaimLapsedRefundResponseDto> responseDtos = repository.findByClaimCircumstance_Id(claimCircumstanceId).stream().map(entity -> {
            List<AgencyCategory> agencyCategories = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getCategory)
                .toList();
            List<Long> categoriesId = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getId)
                .toList();
            List<BenefitComponentTypeMaster> benefitComponents = componentMapRepository.findByRule_IdAndClaimLapsedRefundCategoryMap_IdIn(entity.getRuleType().getId(), categoriesId)
                .stream()
                .map(ClaimLapsedRefundComponentMap::getBenefitComponentType)
                .toList();
            return mapper.toResponseDto(entity, agencyCategories, benefitComponents);
        }).toList();
        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getBySchemeType(Long schemeTypeId) {
        List<ClaimLapsedRefundResponseDto> responseDtos = repository.findBySchemeType_Id(schemeTypeId).stream().map(entity -> {
            List<AgencyCategory> agencyCategories = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getCategory)
                .toList();
            List<Long> categoriesId = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getId)
                .toList();
            List<BenefitComponentTypeMaster> benefitComponents = componentMapRepository.findByRule_IdAndClaimLapsedRefundCategoryMap_IdIn(entity.getRuleType().getId(), categoriesId)
                .stream()
                .map(ClaimLapsedRefundComponentMap::getBenefitComponentType)
                .toList();
            return mapper.toResponseDto(entity, agencyCategories, benefitComponents);
        }).toList();
        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getByRuleType(Long ruleTypeId) {
        List<ClaimLapsedRefundResponseDto> responseDtos = repository.findByRuleType_Id(ruleTypeId).stream().map(entity -> {
            List<AgencyCategory> agencyCategories = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getCategory)
                .toList();
            List<Long> categoriesId = categoryMapRepository.findByRule_Id(entity.getRuleType().getId())
                .stream()
                .map(ClaimLapsedRefundCategoryMap::getId)
                .toList();
            List<BenefitComponentTypeMaster> benefitComponents = componentMapRepository.findByRule_IdAndClaimLapsedRefundCategoryMap_IdIn(entity.getRuleType().getId(), categoriesId)
                .stream()
                .map(ClaimLapsedRefundComponentMap::getBenefitComponentType)
                .toList();
            return mapper.toResponseDto(entity, agencyCategories, benefitComponents);
        }).toList();
        return ApiResponseDTO.success(responseDtos);
    }
}