package com.claim.claim_processing.application.service.claimDetail.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationRuleEvaluationListDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationSummary;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionItem;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimForfeitedComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimRuleEvaluation;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;
import com.claim.claim_processing.application.mapper.claimDetail.AllClaimDetailMapper;
import com.claim.claim_processing.application.mapper.claimDetail.GeneralClaimDetailMapper;
import com.claim.claim_processing.application.repository.claimDetail.ClaimBankDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimCalculationComponentRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimCalculationSummaryRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDeductionDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDeductionItemRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimForfeitedComponentRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimRuleEvaluationRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiarySettlementDetailRepository;
import com.claim.claim_processing.application.repository.detail.LegalRecoveryDetailRepository;
import com.claim.claim_processing.application.repository.detail.NormalClaimDetailRepository;
import com.claim.claim_processing.application.repository.detail.PartialWithdrawalDetailRepository;
import com.claim.claim_processing.application.service.claimDetail.ClaimDetailService;
import com.claim.claim_processing.application.service.claimDetail.ClaimLedgerDeductionTrackerService;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseAuthorityRepository;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.SubClaimMappingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimDetailServiceImpl implements ClaimDetailService {
    
    private final AllClaimDetailMapper allClaimDetailMapper;
    private final GeneralClaimDetailMapper generalClaimDetailMapper;
    private final ClaimBankDetailRepository claimBankDetailRepository;
    private final ClaimCalculationComponentRepository claimCalculationComponentRepository;
    private final ClaimCalculationSummaryRepository claimCalculationSummaryRepository;
    private final ClaimDeductionDetailRepository claimDeductionDetailRepository;
    private final ClaimDetailRepository claimDetailRepository;
    private final ClaimForfeitedComponentRepository claimForfeitedComponentRepository;
    private final ClaimRuleEvaluationRepository claimRuleEvaluationRepository;
    private final ClaimDeductionItemRepository claimDeductionItemRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;
    private final SpecialCaseAuthorityRepository specialCaseAuthorityRepository;
    private final StatusMasterRepository statusMasterRepository;
    private final StageRepository stageRepository;
    private final SchemeTypeRepository schemeTypeRepository;
    private final SubClaimMappingRepository subClaimMappingRepository;
    private final ComponentMasterRepository componentMasterRepository;
    private final NormalClaimDetailRepository normalClaimDetailRepository;
    private final PartialWithdrawalDetailRepository partialWithdrawalDetailRepository;
    private final BeneficiarySettlementDetailRepository beneficiarySettlementDetailRepository;
    private final ClaimLedgerDeductionTrackerService claimLedgerDeductionTrackerService;
    private final LegalRecoveryDetailRepository legalRecoveryDetailRepository;

    @Override
    @Transactional
    public GeneralClaimDetailResponse create(GeneralClaimResponse requestResponse) {
        log.info("Creating claim detail for application: {}", requestResponse.getApplicationNumber());
        
        // 1. Convert to entity
        ClaimDetail claimDetail = allClaimDetailMapper.toEntity(requestResponse);
        
        // 2. Set references (FIXED: Don't throw exceptions for null IDs)
        setClaimDetailReferences(claimDetail, requestResponse);
        
        // 3. Save claim detail first
        claimDetail = claimDetailRepository.saveAndFlush(claimDetail);
        log.info("Claim detail saved with ID: {}", claimDetail.getId());
        
        // 4. Save related entities
        saveBankDetails(requestResponse.getBankDetails(), claimDetail);
        saveDeductionDetail(requestResponse.getDeductionDetail(), claimDetail);
        saveCalculationSummary(requestResponse.getCalculationSummary(), claimDetail);
        saveForfeitedComponents(requestResponse.getForfeitedComponents(), claimDetail);
        saveNormalClaimDetail(requestResponse, claimDetail);
        savePartialWithdrawalDetail(requestResponse, claimDetail);
        saveBeneficiarySettlementDetail(requestResponse, claimDetail);
        saveLegalRecoveryDetail(requestResponse, claimDetail);
        claimLedgerDeductionTrackerService.create(claimDetail, requestResponse, claimDetail.getCreatedBy());
        // 5. Return response
        GeneralClaimDetailResponse response = generalClaimDetailMapper.mapToResponse(claimDetail);
        log.info("Claim detail created successfully for application: {}", requestResponse.getApplicationNumber());
        
        return response;
    }

    private void setClaimDetailReferences(ClaimDetail claimDetail, GeneralClaimResponse requestResponse) {
        // Set Agency Category
        if (requestResponse.getMemberCategoryId() != null) {
            AgencyCategory agencyCategory = agencyCategoryRepository.findById(requestResponse.getMemberCategoryId())
                    .orElseThrow(() -> new RuntimeException("Agency Category not found with ID: " + requestResponse.getMemberCategoryId()));
            claimDetail.setMemberCategory(agencyCategory);
        }

        // Set Special Case Authority (handle null)
        if (requestResponse.getSpecialCaseAuthorityId() != null) {
            SpecialCaseRefundAuthorityMaster specialCaseRefundAuthorityMaster = specialCaseAuthorityRepository
                    .findById(requestResponse.getSpecialCaseAuthorityId())
                    .orElseThrow(() -> new RuntimeException("Special Case Refund Authority not found with ID: " + requestResponse.getSpecialCaseAuthorityId()));
            claimDetail.setSpecialCaseAuthority(specialCaseRefundAuthorityMaster);
        }
        
        // Set Stage
        if (requestResponse.getCurrentStageId() != null) {
            StageMaster stageMaster = stageRepository.findById(requestResponse.getCurrentStageId())
                    .orElseThrow(() -> new RuntimeException("Stage not found with ID: " + requestResponse.getCurrentStageId()));
            claimDetail.setCurrentStage(stageMaster);
        }

        // Set Scheme Type
        if (requestResponse.getSchemeTypeId() != null) {
            SchemeType schemeType = schemeTypeRepository.findById(requestResponse.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException("Scheme Type not found with ID: " + requestResponse.getSchemeTypeId()));
            claimDetail.setSchemeType(schemeType);
        }

        // Set Status
        if (requestResponse.getStatusId() != null) {
            claimDetail.setStatus(getStatusMaster(requestResponse.getStatusId()));
        }
    }

    private NormalClaimDetail saveNormalClaimDetail(GeneralClaimResponse requestResponse, ClaimDetail claimDetail) {
        // FIXED: Check if normalClaimDetails exists and has ID
        if (requestResponse.getNormalClaimDetails() != null && requestResponse.getNormalClaimDetails().getId() != null) {
            NormalClaimDetail normalClaimDetail = normalClaimDetailRepository
                    .findById(requestResponse.getNormalClaimDetails().getId())
                    .orElse(null); // Don't throw, just return null if not found
            
            if (normalClaimDetail != null) {
                normalClaimDetail.setClaimDetail(claimDetail);
                return normalClaimDetailRepository.saveAndFlush(normalClaimDetail);
            }
        }
        return null;
    }

    private PartialWithdrawalDetail savePartialWithdrawalDetail(GeneralClaimResponse requestResponse, ClaimDetail claimDetail) {
        // FIXED: Check if partialWithdrawalDetails exists and has ID
        if (requestResponse.getPartialWithdrawalDetails() != null && requestResponse.getPartialWithdrawalDetails().getId() != null) {
            PartialWithdrawalDetail partialWithdrawalDetail = partialWithdrawalDetailRepository
                    .findById(requestResponse.getPartialWithdrawalDetails().getId())
                    .orElse(null);
            
            if (partialWithdrawalDetail != null) {
                partialWithdrawalDetail.setClaimDetail(claimDetail);
                return partialWithdrawalDetailRepository.saveAndFlush(partialWithdrawalDetail);
            }
        }
        return null;
    }

    private BeneficiarySettlementDetail saveBeneficiarySettlementDetail(GeneralClaimResponse requestResponse, ClaimDetail claimDetail) {
        // FIXED: Check if beneficiarySettlementDetails exists and has ID
        if (requestResponse.getBeneficiarySettlementDetails() != null && requestResponse.getBeneficiarySettlementDetails().getId() != null) {
            BeneficiarySettlementDetail beneficiarySettlementDetail = beneficiarySettlementDetailRepository
                    .findById(requestResponse.getBeneficiarySettlementDetails().getId())
                    .orElse(null);
            
            if (beneficiarySettlementDetail != null) {
                beneficiarySettlementDetail.setClaimDetail(claimDetail);
                return beneficiarySettlementDetailRepository.saveAndFlush(beneficiarySettlementDetail);
            }
        }
        return null;
    }

    private LegalRecoveryDetail saveLegalRecoveryDetail(GeneralClaimResponse requestResponse, ClaimDetail claimDetail) {
        // FIXED: Check if legalRecoveryDetails exists and has ID
        LegalRecoveryDetail legalRecoveryDetail = null;
        if (requestResponse.getLegalRecoveryDetail() != null && requestResponse.getLegalRecoveryDetail().getId() != null) {
            legalRecoveryDetail = legalRecoveryDetailRepository
                    .findById(requestResponse.getLegalRecoveryDetail().getId())
                    .orElse(null);
            
            if (legalRecoveryDetail != null) {
                legalRecoveryDetail.setClaimDetail(claimDetail);
                return legalRecoveryDetailRepository.saveAndFlush(legalRecoveryDetail);
            }
        }
        return legalRecoveryDetail;
    }

    private List<ClaimBankDetail> saveBankDetails(List<ClaimApplicationBankResponseDto> bankDetails, ClaimDetail claimDetail) {
        if (bankDetails == null || bankDetails.isEmpty()) {
            return List.of();
        }
        
        List<ClaimBankDetail> claimBankDetails = bankDetails.stream()
                .filter(Objects::nonNull)
                .map(bankDetailResponse -> {
                    ClaimBankDetail bankDetail = allClaimDetailMapper.toBankDetailEntity(bankDetailResponse);
                    bankDetail.setClaimDetail(claimDetail); 
                    return bankDetail;
                })
                .toList();
        
        if (!claimBankDetails.isEmpty()) {
            claimBankDetailRepository.saveAllAndFlush(claimBankDetails);
        }
        return claimBankDetails;
    }

    private ClaimDeductionDetail saveDeductionDetail(ClaimApplicationDeductionResponseDto deductionDetailResponse, ClaimDetail claimDetail) {
        if (deductionDetailResponse == null) {
            return null;
        }
        
        ClaimDeductionDetail deductionDetail = allClaimDetailMapper.toDeductionDetailEntity(deductionDetailResponse);
        deductionDetail.setClaimDetail(claimDetail); 
        deductionDetail = claimDeductionDetailRepository.saveAndFlush(deductionDetail);
        
        // Save deduction items
        if (deductionDetailResponse.getDeductionItems() != null && !deductionDetailResponse.getDeductionItems().isEmpty()) {
            saveDeductionItems(deductionDetailResponse.getDeductionItems(), deductionDetail);
        }
        
        return deductionDetail;
    } 

    private void saveDeductionItems(List<ClaimApplicationDeductionItemResponseDto> deductionItems, ClaimDeductionDetail deductionDetail) {
        if (deductionItems == null || deductionItems.isEmpty()) {
            return;
        }
        
        List<ClaimDeductionItem> claimDeductionItems = deductionItems.stream()
                .filter(Objects::nonNull)
                .map(itemResponse -> {
                    ClaimDeductionItem item = allClaimDetailMapper.toDeductionItemEntity(itemResponse);
                    item.setDeductionDetail(deductionDetail); 
                    return item;
                })
                .toList();
        
        if (!claimDeductionItems.isEmpty()) {
            claimDeductionItemRepository.saveAllAndFlush(claimDeductionItems);
        }
    }

    private ClaimCalculationSummary saveCalculationSummary(ClaimApplicationCalculationSummaryResponseDto calculationSummaryResponse, ClaimDetail claimDetail) {
        if (calculationSummaryResponse == null) {
            return null;
        }
        
        ClaimCalculationSummary calculationSummary = allClaimDetailMapper.toCalculationSummaryEntity(calculationSummaryResponse);
        calculationSummary.setClaimDetail(claimDetail); 
        calculationSummary = claimCalculationSummaryRepository.saveAndFlush(calculationSummary);
        
        // Save rule evaluations
        if (calculationSummaryResponse.getRuleEvaluations() != null && !calculationSummaryResponse.getRuleEvaluations().isEmpty()) {
            saveRuleEvaluations(calculationSummaryResponse.getRuleEvaluations(), calculationSummary);
        }
        
        return calculationSummary;
    }

    private void saveRuleEvaluations(List<ClaimApplicationRuleEvaluationListDto> ruleEvaluations, ClaimCalculationSummary calculationSummary) {
        if (ruleEvaluations == null || ruleEvaluations.isEmpty()) {
            return;
        }
        
        for (ClaimApplicationRuleEvaluationListDto ruleEvaluationDto : ruleEvaluations) {
            if (ruleEvaluationDto == null) continue;
            
            ClaimRuleEvaluation rule = allClaimDetailMapper.toRuleEvaluationEntity(ruleEvaluationDto);
            
            // Find SubClaimMapping
            SubClaimMapping subClaimMapping = subClaimMappingRepository
                    .findBySubClaimCodeIgnoreCase(ruleEvaluationDto.getSubClaimCode())
                    .orElseThrow(() -> new RuntimeException("Sub Claim Mapping not found with code: " + ruleEvaluationDto.getSubClaimCode()));
            
            rule.setCalculationSummary(calculationSummary);
            rule.setSubRule(subClaimMapping);
            rule = claimRuleEvaluationRepository.saveAndFlush(rule);
            
            // Save components
            if (ruleEvaluationDto.getComponents() != null && !ruleEvaluationDto.getComponents().isEmpty()) {
                saveCalculationComponents(ruleEvaluationDto.getComponents(), rule);
            }
        }
    }

    private void saveCalculationComponents(List<ClaimApplicationCalculationComponentDto> calculationComponents, ClaimRuleEvaluation ruleEvaluation) {
        if (calculationComponents == null || calculationComponents.isEmpty()) {
            return;
        }
        
        List<ClaimCalculationComponent> claimCalculationComponents = calculationComponents.stream()
                .filter(Objects::nonNull)
                .map(componentResponse -> {
                    ClaimCalculationComponent component = allClaimDetailMapper.toCalculationComponentEntity(componentResponse);
                    
                    // Find ComponentMaster
                    ComponentMaster componentMaster = componentMasterRepository
                            .findByCode(componentResponse.getComponentCode())
                            .orElseThrow(() -> new RuntimeException("Component Master not found with code: " + componentResponse.getComponentCode()));
                    
                    component.setRuleEvaluation(ruleEvaluation); 
                    component.setComponentMaster(componentMaster);
                    return component;
                })
                .toList();
        
        if (!claimCalculationComponents.isEmpty()) {
            claimCalculationComponentRepository.saveAllAndFlush(claimCalculationComponents);
        }
    }

    private List<ClaimForfeitedComponent> saveForfeitedComponents(
            List<ClaimApplicationForfeitedComponentResponseDto> forfeitedComponentResponses, 
            ClaimDetail claimDetail) {
        
        if (forfeitedComponentResponses == null || forfeitedComponentResponses.isEmpty()) {
            return List.of();
        }
        
        List<ClaimForfeitedComponent> claimForfeitedComponents = forfeitedComponentResponses.stream()
                .filter(Objects::nonNull)
                .map(componentResponse -> {
                    ClaimForfeitedComponent component = allClaimDetailMapper.toForfeitedComponentEntity(componentResponse);
                    component.setClaimDetail(claimDetail); 
                    component.setAmount(componentResponse.getAmount());
                    return component;
                })
                .toList();
        
        if (!claimForfeitedComponents.isEmpty()) {
            claimForfeitedComponentRepository.saveAllAndFlush(claimForfeitedComponents);
        }
        return claimForfeitedComponents;
    }

    private StatusMaster getStatusMaster(Long statusId) {
        return statusMasterRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));
    }
}