package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import com.claim.claim_processing.common.mapper.claim.ClaimEligibilityMapper;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimEligibilityServiceImpl implements ClaimEligibilityService {

    private final ClaimEligibilityComponentMapRepository claimEligibilityComponentMapRepository;
    private final BenefitComponentTypeMasterRepository benefitComponentTypeMasterRepository;
    private final ClaimEligibilityCategoryMapRepository claimEligibilityCategoryMapRepository;

    private final ClaimEligibilityRepository claimEligibilityRepository;
    private final ClaimEligibilityMapper claimEligibilityMapper;

    private final ClaimCircumstanceRepository claimCircumstanceRepository;
    private final SchemeTypeRepository schemeRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<ClaimEligibilityResponseDto>> getAllActive() {
        List<ClaimEligibilityResponseDto> responseDtos = claimEligibilityRepository.findByIsActiveOrderByRuleNameAsc(ActivityEnum.Y).stream()
                .map(entity -> {
                    List<AgencyCategory> agencyCategories = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId())
                            .stream()
                            .map(ClaimEligibilityCategoryMap::getCategory)
                            .toList();
                    List<Long> categoryIds = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId()).stream()
                            .map(ClaimEligibilityCategoryMap::getId)
                            .toList();

                    List<BenefitComponentTypeMaster> benefitComponents = claimEligibilityComponentMapRepository
                            .findByRule_IdAndClaimEligibilityCategoryMap_IdIn(entity.getId(), categoryIds)
                            .stream()
                            .map(ClaimEligibilityComponentMap::getBenefitComponentType)
                            .toList();

                    return claimEligibilityMapper.toResponseDto(entity, agencyCategories, benefitComponents);
                })
                .collect(Collectors.toList());
        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ClaimEligibilityResponseDto> getById(Long id) {
        ClaimEligibilityMaster entity = claimEligibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim eligibility rule not found with id: " + id));
        List<AgencyCategory> agencyCategories = claimEligibilityCategoryMapRepository
                .findByRule_Id(entity.getId())
                .stream()
                .map(ClaimEligibilityCategoryMap::getCategory)
                .toList();
        List<Long> categoryIds = claimEligibilityCategoryMapRepository
                .findByRule_Id(entity.getId())
                .stream()
                .map(ClaimEligibilityCategoryMap::getId)
                .toList();
        List<BenefitComponentTypeMaster> benefitComponents = claimEligibilityComponentMapRepository
                .findByRule_IdAndClaimEligibilityCategoryMap_IdIn(entity.getId(), categoryIds)
                .stream()
                .map(ClaimEligibilityComponentMap::getBenefitComponentType)
                .toList();
        return ApiResponseDTO.success(claimEligibilityMapper.toResponseDto(entity, agencyCategories, benefitComponents));
    }

    @Override
    public ApiResponseDTO<ClaimEligibilityResponseDto> create(ClaimEligibilityCreateRequestDto requestDto) {

        if (claimEligibilityRepository.existsByRuleCode(requestDto.getRuleCode())) {
            throw new RuntimeException("Claim eligibility rule code already exists: " + requestDto.getRuleCode());
        }

        ClaimEligibilityMaster entity = claimEligibilityMapper.toEntity(requestDto);

        if (requestDto.getClaimCircumstanceId() != null) {
            ClaimCircumstanceMaster claimCircumstance = claimCircumstanceRepository
                    .findById(requestDto.getClaimCircumstanceId())
                    .orElseThrow(() -> new RuntimeException(
                            "Claim circumstance not found with id: " + requestDto.getClaimCircumstanceId()));
            entity.setClaimCircumstance(claimCircumstance);
        }
        if (requestDto.getSchemeTypeId() != null) {
            SchemeMaster schemeType = schemeRepository.findById(requestDto.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Scheme type not found with id: " + requestDto.getSchemeTypeId()));
            entity.setSchemeType(schemeType);
        }

        entity.setCreatedBy("SYSTEM");

        ClaimEligibilityMaster saved = claimEligibilityRepository.save(entity);
        ClaimEligibilityCategoryMap categoryMap = mapClaimEligibilityToCategory(saved,
                requestDto.getMemberCategoryId());
        BenefitComponentTypeMaster benefitComponentType = mapBenefitComponentTypeToEligibility(saved, categoryMap,
                requestDto.getBenefitTypeId());
        List<AgencyCategory> agencyCategories = List.of(categoryMap.getCategory());
        List<BenefitComponentTypeMaster> benefitComponents = List.of(benefitComponentType);
        return ApiResponseDTO.success(claimEligibilityMapper.toResponseDto(saved, agencyCategories, benefitComponents));
    }

    private AgencyCategory getMemberCategory(String memberCategoryId) {
        return agencyCategoryRepository.findById(memberCategoryId)
                .orElseThrow(() -> new RuntimeException("Member category not found with id: " + memberCategoryId));
    }

    private BenefitComponentTypeMaster getBenefitTypeComponent(Long benefitTypeId) {
        return benefitComponentTypeMasterRepository.findById(benefitTypeId)
                .orElseThrow(() -> new RuntimeException("Benefit type not found with id: " + benefitTypeId));
    }

    @Override
    public ApiResponseDTO<ClaimEligibilityResponseDto> update(Long id, ClaimEligibilityUpdateRequestDto requestDto) {

        ClaimEligibilityMaster entity = claimEligibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim eligibility rule not found with id: " + id));

        claimEligibilityMapper.updateEntityFromDto(requestDto, entity);

        if (requestDto.getClaimCircumstanceId() != null) {
            ClaimCircumstanceMaster claimCircumstance = claimCircumstanceRepository
                    .findById(requestDto.getClaimCircumstanceId())
                    .orElseThrow(() -> new RuntimeException(
                            "Claim circumstance not found with id: " + requestDto.getClaimCircumstanceId()));
            entity.setClaimCircumstance(claimCircumstance);
        }

        if (requestDto.getSchemeTypeId() != null) {
            SchemeMaster schemeType = schemeRepository.findById(requestDto.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Scheme type not found with id: " + requestDto.getSchemeTypeId()));
            entity.setSchemeType(schemeType);
        }

        entity.setUpdatedBy("SYSTEM");

        ClaimEligibilityMaster updated = claimEligibilityRepository.save(entity);
        ClaimEligibilityCategoryMap categoryMap = mapClaimEligibilityToCategory(updated,
                requestDto.getMemberCategoryId());
        BenefitComponentTypeMaster benefitComponentType = mapBenefitComponentTypeToEligibility(updated, categoryMap,
                requestDto.getBenefitTypeId());
        List<AgencyCategory> agencyCategories = List.of(categoryMap.getCategory());
        List<BenefitComponentTypeMaster> benefitComponents = List.of(benefitComponentType);
        return ApiResponseDTO.success(claimEligibilityMapper.toResponseDto(updated, agencyCategories, benefitComponents));
    }

    @Override
    public ApiResponseDTO<String> deactivate(Long id) {
        ClaimEligibilityMaster entity = claimEligibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim eligibility rule not found with id: " + id));

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        claimEligibilityRepository.save(entity);
        return ApiResponseDTO.success("Claim eligibility rule deactivated successfully");
    }

    @Override
    public ApiResponseDTO<List<ClaimEligibilityResponseDto>> getByClaimCircumstanceId(Long claimCircumstanceId) {
        List<ClaimEligibilityResponseDto> response = claimEligibilityRepository.findByClaimCircumstance_Id(claimCircumstanceId)
                .stream()
                .map(entity -> {
                    List<AgencyCategory> agencyCategories = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId())
                            .stream()
                            .map(ClaimEligibilityCategoryMap::getCategory)
                            .toList();
                    List<Long> categoryIds = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId())
                            .stream()
                            .map(ClaimEligibilityCategoryMap::getId)
                            .toList();

                    // Get benefit components for ALL categories
                    List<BenefitComponentTypeMaster> benefitComponents = agencyCategories.stream()
                            .flatMap(category -> {
                                List<ClaimEligibilityComponentMap> maps = claimEligibilityComponentMapRepository
                                        .findByRule_IdAndClaimEligibilityCategoryMap_IdIn(entity.getId(), categoryIds);
                                return maps.stream()
                                        .map(ClaimEligibilityComponentMap::getBenefitComponentType);
                            })
                            .distinct()
                            .collect(Collectors.toList());

                    return claimEligibilityMapper.toResponseDto(entity, agencyCategories, benefitComponents);
                })
                .toList();
        return ApiResponseDTO.success(response);
    }

    @Override
    public ApiResponseDTO<List<ClaimEligibilityResponseDto>> getBySchemeTypeId(Long schemeTypeId) {
        List<ClaimEligibilityResponseDto> response = claimEligibilityRepository.findBySchemeType_Id(schemeTypeId)
                .stream()
                .map(entity -> {
                    List<AgencyCategory> agencyCategories = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId())
                            .stream()
                            .map(ClaimEligibilityCategoryMap::getCategory)
                            .toList();
                    List<Long> categoryIds = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId())
                            .stream()
                            .map(ClaimEligibilityCategoryMap::getId)
                            .toList();

                    // Get benefit components for ALL categories
                    List<BenefitComponentTypeMaster> benefitComponents = agencyCategories.stream()
                            .flatMap(category -> {
                                List<ClaimEligibilityComponentMap> maps = claimEligibilityComponentMapRepository
                                        .findByRule_IdAndClaimEligibilityCategoryMap_IdIn(entity.getId(), categoryIds);
                                return maps.stream()
                                        .map(ClaimEligibilityComponentMap::getBenefitComponentType);
                            })
                            .distinct()
                            .collect(Collectors.toList());

                    return claimEligibilityMapper.toResponseDto(entity, agencyCategories, benefitComponents);
                })
                .toList();
        return ApiResponseDTO.success(response);
    }

    @Override
    public ApiResponseDTO<List<ClaimEligibilityResponseDto>> getByRuleTypeId(Long ruleTypeId) {
        List<ClaimEligibilityResponseDto> response = claimEligibilityRepository.findByRuleType_Id(ruleTypeId)
                .stream()
                .map(entity -> {
                    List<AgencyCategory> agencyCategories = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId())
                            .stream()
                            .map(ClaimEligibilityCategoryMap::getCategory)
                            .toList();
                    List<Long> categoryIds = claimEligibilityCategoryMapRepository
                            .findByRule_Id(entity.getId())
                            .stream()
                            .map(ClaimEligibilityCategoryMap::getId)
                            .toList();

                    // Get benefit components for ALL categories
                    List<BenefitComponentTypeMaster> benefitComponents = agencyCategories.stream()
                            .flatMap(category -> {
                                List<ClaimEligibilityComponentMap> maps = claimEligibilityComponentMapRepository
                                        .findByRule_IdAndClaimEligibilityCategoryMap_IdIn(entity.getId(), categoryIds);
                                return maps.stream()
                                        .map(ClaimEligibilityComponentMap::getBenefitComponentType);
                            })
                            .distinct()
                            .collect(Collectors.toList());

                    return claimEligibilityMapper.toResponseDto(entity, agencyCategories, benefitComponents);
                })
                .toList();
        return ApiResponseDTO.success(response);
    }

    private ClaimEligibilityCategoryMap mapClaimEligibilityToCategory(ClaimEligibilityMaster eligibility,
            String memberCategoryId) {
        ClaimEligibilityCategoryMap map = claimEligibilityCategoryMapRepository
                .findByRule_IdAndCategory_CategoryId(eligibility.getId(), memberCategoryId)
                .orElse(null);
        AgencyCategory category;
        if (map != null) {
            map.setRule(eligibility);
            category = map.getCategory();
            map.setCategory(category);
            return map;
        } else {
            map = ClaimEligibilityCategoryMap
                    .builder()
                    .rule(eligibility)
                    .category(getMemberCategory(memberCategoryId))
                    .build();
            return map;
        }
    }

    private BenefitComponentTypeMaster mapBenefitComponentTypeToEligibility(ClaimEligibilityMaster eligibility,
            ClaimEligibilityCategoryMap categoryMap, Long benefitTypeId) {
        ClaimEligibilityComponentMap componentMap = claimEligibilityComponentMapRepository
                .findByRule_IdAndClaimEligibilityCategoryMap_Id(eligibility.getId(), categoryMap.getId())
                .orElse(null);
        if (componentMap != null) {
            componentMap.setClaimEligibilityCategoryMap(null);
            ;
            componentMap.setRule(eligibility);
            componentMap.setBenefitComponentType(getBenefitTypeComponent(benefitTypeId));
            return componentMap.getBenefitComponentType();
        } else {
            ClaimEligibilityComponentMap newMap = ClaimEligibilityComponentMap
                    .builder()
                    .rule(eligibility)
                    .benefitComponentType(getBenefitTypeComponent(benefitTypeId))
                    .build();
            return newMap.getBenefitComponentType();
        }
    }
}