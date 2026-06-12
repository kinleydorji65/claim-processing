package com.claim.claim_processing.application.service.claimDetail.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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
import com.claim.claim_processing.application.repository.detail.BeneficiaryClaimantDetailRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiarySettlementDetailRepository;
import com.claim.claim_processing.application.repository.detail.NormalClaimDetailRepository;
import com.claim.claim_processing.application.repository.detail.PartialWithdrawalDetailRepository;
import com.claim.claim_processing.application.service.claimDetail.ClaimDetailService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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

    public GeneralClaimDetailResponse create(GeneralClaimResponse rerequestResponse){
        ClaimDetail claimDetail = allClaimDetailMapper.toEntity(rerequestResponse);
        AgencyCategory agencyCategory = agencyCategoryRepository.findById(rerequestResponse.getMemberCategoryId())
                .orElseThrow(() -> new RuntimeException("Agency Category not found with ID: " + rerequestResponse.getMemberCategoryId()));

        SpecialCaseRefundAuthorityMaster specialCaseRefundAuthorityMaster = specialCaseAuthorityRepository.findById(rerequestResponse.getSpecialCaseAuthorityId())
                .orElseThrow(() -> new RuntimeException("Special Case Refund Authority not found with ID: " + rerequestResponse.getSpecialCaseAuthorityId()));
        
        StageMaster stageMaster = stageRepository.findById(rerequestResponse.getCurrentStageId())
                .orElseThrow(() -> new RuntimeException("Stage not found with ID: " + rerequestResponse.getCurrentStageId()));

        SchemeType schemeType = schemeTypeRepository.findById(rerequestResponse.getSchemeTypeId())
                .orElseThrow(() -> new RuntimeException("Scheme Type not found with ID: " + rerequestResponse.getSchemeTypeId()));

        claimDetail.setMemberCategory(agencyCategory);
        claimDetail.setSpecialCaseAuthority(specialCaseRefundAuthorityMaster);
        claimDetail.setStatus(getStatusMaster(rerequestResponse.getStatusId()));
        claimDetail.setCurrentStage(stageMaster);
        claimDetail.setSchemeType(schemeType);
        claimDetailRepository.saveAndFlush(claimDetail);
        saveBankDetails(rerequestResponse.getBankDetails(), claimDetail);
        saveDeductionDetail(rerequestResponse.getDeductionDetail(), claimDetail);
        saveCalculationSummary(rerequestResponse.getCalculationSummary(), claimDetail);
        saveForfeitedComponents(rerequestResponse.getForfeitedComponents(), claimDetail);
        saveNormalClaimDetail(rerequestResponse, claimDetail);
        savePartialWithdrawalDetail(rerequestResponse, claimDetail);
        saveBeneficiarySettlementDetail(rerequestResponse, claimDetail);
        GeneralClaimDetailResponse response = generalClaimDetailMapper.mapToResponse(claimDetail);

        return response;
    }

    private NormalClaimDetail saveNormalClaimDetail(GeneralClaimResponse rerequestResponse, ClaimDetail claimDetail) {
        NormalClaimDetail normalClaimDetail = normalClaimDetailRepository.findByClaimApplication_Id(rerequestResponse.getId()).orElseThrow(() -> new RuntimeException("Normal Claim Detail not found with ID: " + rerequestResponse.getId()));
        normalClaimDetail.setClaimDetail(claimDetail); 
        claimDetailRepository.saveAndFlush(claimDetail);
        return normalClaimDetail;
    }

    private PartialWithdrawalDetail savePartialWithdrawalDetail(GeneralClaimResponse rerequestResponse, ClaimDetail claimDetail) {
        PartialWithdrawalDetail partialWithdrawalDetail = partialWithdrawalDetailRepository.findByClaimApplication_Id(rerequestResponse.getId()).orElseThrow(() -> new RuntimeException("Partial Withdrawal Detail not found with ID: " + rerequestResponse.getId()));
        partialWithdrawalDetail.setClaimDetail(claimDetail); 
        claimDetailRepository.saveAndFlush(claimDetail);
        return partialWithdrawalDetail;
    }

    private BeneficiarySettlementDetail saveBeneficiarySettlementDetail(GeneralClaimResponse rerequestResponse, ClaimDetail claimDetail) {
        BeneficiarySettlementDetail beneficiarySettlementDetail = beneficiarySettlementDetailRepository.findByClaimApplication_Id(rerequestResponse.getId()).orElseThrow(() -> new RuntimeException("Beneficiary Settlement Detail not found with ID: " + rerequestResponse.getId()));
        beneficiarySettlementDetail.setClaimDetail(claimDetail); 
        claimDetailRepository.saveAndFlush(claimDetail);
        return beneficiarySettlementDetail;
    }

    private List<ClaimBankDetail> saveBankDetails(List<ClaimApplicationBankResponseDto> bankDetails, ClaimDetail claimDetail) {
        List<ClaimBankDetail> claimBankDetails = bankDetails.stream()
                .map(bankDetailResponse -> {
                    ClaimBankDetail bankDetail = allClaimDetailMapper.toBankDetailEntity(bankDetailResponse);
                    bankDetail.setClaimDetail(claimDetail); 
                    return bankDetail;
                })
                .toList();
        claimBankDetailRepository.saveAllAndFlush(claimBankDetails);
        return claimBankDetails;
    }

    private ClaimDeductionDetail saveDeductionDetail(ClaimApplicationDeductionResponseDto deductionDetailResponse, ClaimDetail claimDetail) {
        ClaimDeductionDetail deductionDetail = allClaimDetailMapper.toDeductionDetailEntity(deductionDetailResponse);
        deductionDetail.setClaimDetail(claimDetail); 
        claimDeductionDetailRepository.saveAndFlush(deductionDetail);
        saveDeductionItems(deductionDetailResponse.getDeductionItems(), deductionDetail);
        return deductionDetail;
        
    } 

    private void saveDeductionItems(List<ClaimApplicationDeductionItemResponseDto> deductionItems, ClaimDeductionDetail deductionDetail) {
        List<ClaimDeductionItem> claimDeductionItems = deductionItems.stream()
                .map(itemResponse -> {
                    ClaimDeductionItem item = allClaimDetailMapper.toDeductionItemEntity(itemResponse);
                    item.setDeductionDetail(deductionDetail); 
                    return item;
                })
                .toList();
        claimDeductionItemRepository.saveAllAndFlush(claimDeductionItems);
    }

    private ClaimCalculationSummary saveCalculationSummary(ClaimApplicationCalculationSummaryResponseDto calculationSummaryResponse, ClaimDetail claimDetail) {
        ClaimCalculationSummary calculationSummary = allClaimDetailMapper.toCalculationSummaryEntity(calculationSummaryResponse);
        calculationSummary.setClaimDetail(claimDetail); 
        claimCalculationSummaryRepository.saveAndFlush(calculationSummary);
        saveRuleEvaluations(calculationSummaryResponse.getRuleEvaluations(), calculationSummary);
        return calculationSummary;
    }

    private void saveRuleEvaluations(List<ClaimApplicationRuleEvaluationListDto> ruleEvaluations, ClaimCalculationSummary calculationSummary) {
        ruleEvaluations.stream()
                .map(ruleEvaluation -> {
                    ClaimRuleEvaluation rule = allClaimDetailMapper.toRuleEvaluationEntity(ruleEvaluation);
                    SubClaimMapping subClaimMapping = subClaimMappingRepository.findBySubClaimCodeIgnoreCase(ruleEvaluation.getSubClaimCode())
                            .orElseThrow(() -> new RuntimeException("Sub Claim Mapping not found with ID: " + ruleEvaluation.getSubClaimCode()));
                    rule.setCalculationSummary(calculationSummary);
                    rule.setSubRule(subClaimMapping);
                    claimRuleEvaluationRepository.saveAndFlush(rule);
                    saveCalculationComponents(ruleEvaluation.getComponents(), rule);

                    return rule;
                })
                .toList();
    }

    private void saveCalculationComponents(List<ClaimApplicationCalculationComponentDto> calculationComponents, ClaimRuleEvaluation ruleEvaluation) {
        List<ClaimCalculationComponent> claimCalculationComponents = calculationComponents.stream()
                .map(componentResponse -> {
                    ClaimCalculationComponent component = allClaimDetailMapper.toCalculationComponentEntity(componentResponse);
                    ComponentMaster componentMaster = componentMasterRepository.findByCode(componentResponse.getComponentCode())
                            .orElseThrow(() -> new RuntimeException("Component Master not found with ID: " + componentResponse.getComponentCode()));
                    component.setRuleEvaluation(ruleEvaluation); 
                    component.setComponentMaster(componentMaster);
                    return component;
                })
                .toList();
        claimCalculationComponentRepository.saveAllAndFlush(claimCalculationComponents);
    }

    private List<ClaimForfeitedComponent> saveForfeitedComponents(List<ClaimApplicationForfeitedComponentResponseDto> forfeitedComponentResponses, ClaimDetail claimDetail) {
        List<ClaimForfeitedComponent> claimForfeitedComponents = forfeitedComponentResponses.stream()
                .map(componentResponse -> {
                    ClaimForfeitedComponent component = allClaimDetailMapper.toForfeitedComponentEntity(componentResponse);
                    component.setClaimDetail(claimDetail); 
                    component.setAmount(componentResponse.getAmount());
                    return component;
                })
                .toList();
        claimForfeitedComponentRepository.saveAllAndFlush(claimForfeitedComponents);
        return claimForfeitedComponents;
    }

    private StatusMaster getStatusMaster(Long statusId) {
        return statusMasterRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));
    }
}
