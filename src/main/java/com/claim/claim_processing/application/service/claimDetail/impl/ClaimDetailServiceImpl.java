package com.claim.claim_processing.application.service.claimDetail.impl;

import com.claim.claim_processing.common.repository.common.CoaMainAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaSubAccountRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationRuleEvaluationListDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimBankResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimRuleEvaluationListDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.entity.claimDetail.ClaimAccountingEvent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationSummary;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionItem;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimForfeitedComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerEntry;
import com.claim.claim_processing.application.entity.claimDetail.ClaimRuleEvaluation;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;
import com.claim.claim_processing.application.mapper.claimDetail.AllClaimDetailMapper;
import com.claim.claim_processing.application.mapper.claimDetail.GeneralClaimDetailMapper;
import com.claim.claim_processing.application.repository.claimDetail.ClaimAccountingEventRepository;
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
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.CoaMainAccount;
import com.claim.claim_processing.common.entities.common.CoaSubAccount;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.BankType;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.repository.common.SubmissionChannelRepository;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.repository.others.BankTypeRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseAuthorityRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.SubClaimMappingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimDetailServiceImpl implements ClaimDetailService {

    private final CoaMainAccountRepository coaMainAccountRepository;
    private final CoaSubAccountRepository coaSubAccountRepository;
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
    private final SchemeTypeRepository schemeTypeRepository;
    private final SubClaimMappingRepository subClaimMappingRepository;
    private final ComponentMasterRepository componentMasterRepository;
    private final NormalClaimDetailRepository normalClaimDetailRepository;
    private final PartialWithdrawalDetailRepository partialWithdrawalDetailRepository;
    private final BeneficiarySettlementDetailRepository beneficiarySettlementDetailRepository;
    private final LegalRecoveryDetailRepository legalRecoveryDetailRepository;

    private final ClaimTypeMasterRepository claimTypeMasterRepository;
    private final SubmissionChannelRepository submissionChannelMasterRepository;
    private final ClaimantTypeRepository claimantTypeRepository;
    private final BankTypeRepository bankTypeRepository;
    private final ClaimAccountingEventRepository claimAccountingEventRepository;

    @Override
    @Transactional
    public GeneralClaimDetailResponse create(GeneralClaimResponse requestResponse) {
        log.info("Creating claim detail for application: {}", requestResponse.getApplicationNumber());

        // 1. Convert to entity
        ClaimDetail claimDetail = allClaimDetailMapper.toEntity(requestResponse);
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
        // 5. Return response
        GeneralClaimDetailResponse response = generalClaimDetailMapper.mapToResponse(claimDetail);
        log.info("Claim detail created successfully for application: {}", requestResponse.getApplicationNumber());

        return response;
    }

    private void setClaimDetailReferences(ClaimDetail claimDetail, GeneralClaimResponse requestResponse) {

        ClaimTypeMaster claimTypeMaster = claimTypeMasterRepository.findById(requestResponse.getClaimTypeId())
                .orElseThrow(() -> new RuntimeException(
                        "Claim Type not found with ID: " + requestResponse.getClaimTypeId()));
        claimDetail.setClaimType(claimTypeMaster);
        // 2. Set references (FIXED: Don't throw exceptions for null IDs)
        if (requestResponse.getSubmissionChannelId() != null) {
            SubmissionChannelMaster submissionChannelMaster = submissionChannelMasterRepository
                    .findById(requestResponse.getSubmissionChannelId())
                    .orElseThrow(() -> new RuntimeException(
                            "Submission Channel not found with ID: " + requestResponse.getSubmissionChannelId()));
            claimDetail.setSubmissionChannel(submissionChannelMaster);
        }
        // Set Agency Category
        if (requestResponse.getMemberCategoryId() != null) {
            AgencyCategory agencyCategory = agencyCategoryRepository.findById(requestResponse.getMemberCategoryId())
                    .orElseThrow(() -> new RuntimeException(
                            "Agency Category not found with ID: " + requestResponse.getMemberCategoryId()));
            claimDetail.setMemberCategory(agencyCategory);
        }

        // Set Scheme Type
        if (requestResponse.getSchemeTypeId() != null) {
            SchemeType schemeType = schemeTypeRepository.findById(requestResponse.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Scheme Type not found with ID: " + requestResponse.getSchemeTypeId()));
            claimDetail.setSchemeType(schemeType);
        }

        // Set Status
        if (requestResponse.getStatusId() != null) {
            claimDetail.setStatus(getStatusMaster(requestResponse.getStatusId()));
        }
    }

    private NormalClaimDetail saveNormalClaimDetail(GeneralClaimResponse requestResponse, ClaimDetail claimDetail) {
        // FIXED: Check if normalClaimDetails exists and has ID
        if (requestResponse.getNormalClaimDetails() != null
                && requestResponse.getNormalClaimDetails().getId() != null) {
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

    private PartialWithdrawalDetail savePartialWithdrawalDetail(GeneralClaimResponse requestResponse,
            ClaimDetail claimDetail) {
        // FIXED: Check if partialWithdrawalDetails exists and has ID
        if (requestResponse.getPartialWithdrawalDetails() != null
                && requestResponse.getPartialWithdrawalDetails().getId() != null) {
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

    private BeneficiarySettlementDetail saveBeneficiarySettlementDetail(GeneralClaimResponse requestResponse,
            ClaimDetail claimDetail) {
        // FIXED: Check if beneficiarySettlementDetails exists and has ID
        if (requestResponse.getBeneficiarySettlementDetails() != null
                && requestResponse.getBeneficiarySettlementDetails().getId() != null) {
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

    @Override
@Transactional(readOnly = true)
public ApiResponseDTO<Page<GeneralClaimDetailResponse>> getAllApprovedDetails(Pageable pageable) {
    log.info("Fetching all claim details with pagination: page={}, size={}", 
            pageable.getPageNumber(), pageable.getPageSize());
    
    try {
        Page<ClaimDetail> claimDetailsPage = claimDetailRepository.findAll(pageable);
        
        
        log.info("Found {} total claims", claimDetailsPage.getTotalElements());
        
        Page<GeneralClaimDetailResponse> responsePage = claimDetailsPage.map(claimDetail -> {
            try {
                GeneralClaimDetailResponse response = generalClaimDetailMapper.mapToResponse(claimDetail);
                
                if (response == null) {
                    log.warn("Mapper returned NULL for claim detail ID: {}", claimDetail.getId());
                    return null;
                }
                
                // Set all mappings
                response.setBankDetails(mapBankDetails(claimDetail));
                response.setDeductionDetail(mapDeductionDetail(claimDetail));
                response.setForfeitedComponents(mapForfeitedComponents(claimDetail));
                response.setCalculationSummary(mapCalculationSummary(claimDetail));
                response.setNormalClaimDetails(mapNormalClaimDetail(claimDetail));
                response.setPartialWithdrawalDetails(mapPartialWithdrawalDetail(claimDetail));
                response.setBeneficiarySettlementDetail(mapBeneficiarySettlementDetail(claimDetail));
                response.setLegalRecoveryDetail(mapLegalRecoveryDetail(claimDetail));
                
                // Set Accounting Event if exists
                    response.setAccountingEventDetail(
                        mapAccountingEvent(claimDetail));
                
                
                return response;
                
            } catch (Exception e) {
                log.error("Error mapping claim detail {}: {}", claimDetail.getId(), e.getMessage(), e);
                return null;
            }
        });
        
        List<GeneralClaimDetailResponse> nonNullContent = responsePage.getContent().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        Page<GeneralClaimDetailResponse> finalPage = new PageImpl<>(
                nonNullContent,
                responsePage.getPageable(),
                claimDetailsPage.getTotalElements()
        );
        
        log.info("Successfully mapped {} claims", nonNullContent.size());
        return ApiResponseDTO.success(finalPage);
        
    } catch (Exception e) {
        log.error("Error fetching claim details: {}", e.getMessage(), e);
        throw ClaimException.internalError("Failed to fetch claim details: " + e.getMessage());
    }
}

// ========== BENEFICIARY SETTLEMENT MAPPING ==========

private BeneficiarySettlementResponseDto mapBeneficiarySettlementDetail(ClaimDetail claimDetail) {
        BeneficiarySettlementDetail beneficiarySettlementDetail = beneficiarySettlementDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
    if (beneficiarySettlementDetail == null) {
        return null;
    }
    return BeneficiarySettlementResponseDto.builder()
            .id(beneficiarySettlementDetail.getId())
            .claimApplicationId(beneficiarySettlementDetail.getClaimApplication() != null ? 
                    beneficiarySettlementDetail.getClaimApplication().getId() : null)
            .applicationNumber(beneficiarySettlementDetail.getClaimApplication() != null ? 
                    beneficiarySettlementDetail.getClaimApplication().getApplicationNumber() : null)
            .claimDetailId(beneficiarySettlementDetail.getClaimDetail() != null ? 
                    beneficiarySettlementDetail.getClaimDetail().getId() : null)
            .beneficiaryClaimantDetails(mapBeneficiaryClaimants(beneficiarySettlementDetail.getClaimantDetails()))
            .cessationTypeId(beneficiarySettlementDetail.getCessationType() != null ? 
                    beneficiarySettlementDetail.getCessationType().getId() : null)
            .cessationTypeName(beneficiarySettlementDetail.getCessationType() != null ? 
                    beneficiarySettlementDetail.getCessationType().getName() : null)
            .dateOfDeath(beneficiarySettlementDetail.getDateOfDeath())
            .lastContributionDate(beneficiarySettlementDetail.getLastContributionDate())
            .createdBy(beneficiarySettlementDetail.getCreatedBy())
            .createdAt(beneficiarySettlementDetail.getCreatedAt() != null ? 
                    beneficiarySettlementDetail.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(beneficiarySettlementDetail.getUpdatedBy())
            .updatedAt(beneficiarySettlementDetail.getUpdatedAt() != null ? 
                    beneficiarySettlementDetail.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

// ========== BENEFICIARY CLAIMANT MAPPING ==========

private List<BeneficiaryClaimantResponseDto> mapBeneficiaryClaimants(List<BeneficiaryClaimantDetail> claimantDetails) {
    if (claimantDetails == null || claimantDetails.isEmpty()) {
        return null;
    }
    return claimantDetails.stream()
            .filter(Objects::nonNull)
            .map(claimant -> {
                return BeneficiaryClaimantResponseDto.builder()
                        .id(claimant.getId())
                        .beneficiarySettlementDetailId(claimant.getBeneficiarySettlementDetail() != null ? 
                                claimant.getBeneficiarySettlementDetail().getId() : null)
                        .nomineeId(claimant.getNominee() != null ? 
                                claimant.getNominee().getId() : null)
                        .nomineeFirstName(claimant.getNominee() != null ? 
                                claimant.getNominee().getFirstName() : null)
                        .nomineeMiddleName(claimant.getNominee() != null ? 
                                claimant.getNominee().getMiddleName() : null)
                        .nomineeLastName(claimant.getNominee() != null ? 
                                claimant.getNominee().getLastName() : null)
                        .dependentId(claimant.getDependent() != null ? 
                                claimant.getDependent().getId() : null)
                        .dependentFirstName(claimant.getDependent() != null ? 
                                claimant.getDependent().getFirstName() : null)
                        .dependentMiddleName(claimant.getDependent() != null ? 
                                claimant.getDependent().getMiddleName() : null)
                        .dependentLastName(claimant.getDependent() != null ? 
                                claimant.getDependent().getLastName() : null)
                        .claimantTypeId(claimant.getClaimantType() != null ? 
                                claimant.getClaimantType().getId() : null)
                        .claimantTypeName(claimant.getClaimantType() != null ? 
                                claimant.getClaimantType().getName() : null)
                        .payeeTypeId(claimant.getPayeeType() != null ? 
                                claimant.getPayeeType().getId() : null)
                        .payeeTypeName(claimant.getPayeeType() != null ? 
                                claimant.getPayeeType().getName() : null)
                        .relationshipTypeId(claimant.getRelationshipType() != null ? 
                                claimant.getRelationshipType().getRelationTypeId() : null)
                        .relationshipTypeName(claimant.getRelationshipType() != null ? 
                                claimant.getRelationshipType().getRelationTypeName() : null)
                        .beneficiaryIdentifier(claimant.getBeneficiaryIdentifier())
                        .beneficiaryName(claimant.getBeneficiaryName())
                        .dateOfBirth(claimant.getDateOfBirth())
                        .beneficiarySharePercentage(claimant.getBeneficiarySharePercentage())
                        .isMemberFamily(claimant.getIsMemberFamily())
                        .isMinor(claimant.getIsMinor())
                        .guardianName(claimant.getGuardianName())
                        .guardianIdentifier(claimant.getGuardianIdentifier())
                        .benefitAmount(claimant.getBenefitAmount())
                        .remarks(claimant.getRemarks())
                        .createdBy(claimant.getCreatedBy())
                        .createdAt(claimant.getCreatedAt())
                        .updatedBy(claimant.getUpdatedBy())
                        .updatedAt(claimant.getUpdatedAt())
                        .build();
            })
            .collect(Collectors.toList());
}



// ========== NORMAL CLAIM DETAIL MAPPING ==========

private NormalClaimResponseDto mapNormalClaimDetail(ClaimDetail claimDetail) {
        NormalClaimDetail normalClaimDetail = normalClaimDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
    if (normalClaimDetail == null) {
        return null;
    }
    return NormalClaimResponseDto.builder()
            .id(normalClaimDetail.getId())
            .claimApplicationId(normalClaimDetail.getClaimApplication() != null ? 
                    normalClaimDetail.getClaimApplication().getId() : null)
            .applicationNumber(normalClaimDetail.getClaimApplication() != null ? 
                    normalClaimDetail.getClaimApplication().getApplicationNumber() : null)
            .claimDetailId(normalClaimDetail.getClaimDetail() != null ? 
                    normalClaimDetail.getClaimDetail().getId() : null)
            .cessationTypeId(normalClaimDetail.getCessationType() != null ? 
                    normalClaimDetail.getCessationType().getId() : null)
            .cessationTypeName(normalClaimDetail.getCessationType() != null ? 
                    normalClaimDetail.getCessationType().getName() : null)
            .payeeTypeId(normalClaimDetail.getPayeeType() != null ? 
                    normalClaimDetail.getPayeeType().getId() : null)
            .payeeTypeName(normalClaimDetail.getPayeeType() != null ? 
                    normalClaimDetail.getPayeeType().getName() : null)
            .terminationReasonTypeId(normalClaimDetail.getTerminationReasonType() != null ? 
                    normalClaimDetail.getTerminationReasonType().getId() : null)
            .terminationReasonTypeName(normalClaimDetail.getTerminationReasonType() != null ? 
                    normalClaimDetail.getTerminationReasonType().getName() : null)
            .cessationEffectiveDate(normalClaimDetail.getCessationEffectiveDate())
            .dateOfServiceJoining(normalClaimDetail.getDateOfServiceJoining())
            .terminatedBy(normalClaimDetail.getTerminatedBy())
            .terminationRemarks(normalClaimDetail.getTerminationRemarks())
            .relievingOrderNumber(normalClaimDetail.getRelievingOrderNumber())
            .relievingReferenceNumber(normalClaimDetail.getRelievingReferenceNumber())
            .lastPayMonth(normalClaimDetail.getLastPayMonth())
            .finalBasicSalary(normalClaimDetail.getFinalBasicSalary())
            .remarks(normalClaimDetail.getRemarks())
            .createdBy(normalClaimDetail.getCreatedBy())
            .createdAt(normalClaimDetail.getCreatedAt() != null ? 
                    normalClaimDetail.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(normalClaimDetail.getUpdatedBy())
            .updatedAt(normalClaimDetail.getUpdatedAt() != null ? 
                    normalClaimDetail.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

// ========== PARTIAL WITHDRAWAL DETAIL MAPPING ==========

// ========== PARTIAL WITHDRAWAL DETAIL MAPPING ==========

private PartialWithdrawalResponseDto mapPartialWithdrawalDetail(ClaimDetail claimDetail) {
        PartialWithdrawalDetail partialWithdrawalDetail = partialWithdrawalDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
    if (partialWithdrawalDetail == null) {
        return null;
    }
    return PartialWithdrawalResponseDto.builder()
            .id(partialWithdrawalDetail.getId())
            .claimApplicationId(partialWithdrawalDetail.getClaimApplication() != null ? 
                    partialWithdrawalDetail.getClaimApplication().getId() : null)
            .applicationNumber(partialWithdrawalDetail.getClaimApplication() != null ? 
                    partialWithdrawalDetail.getClaimApplication().getApplicationNumber() : null)
            .claimDetailId(partialWithdrawalDetail.getClaimDetail() != null ? 
                    partialWithdrawalDetail.getClaimDetail().getId() : null)
            .payeeTypeId(partialWithdrawalDetail.getPayeeType() != null ? 
                    partialWithdrawalDetail.getPayeeType().getId() : null)
            .payeeTypeName(partialWithdrawalDetail.getPayeeType() != null ? 
                    partialWithdrawalDetail.getPayeeType().getName() : null)
            .withdrawalReasonId(partialWithdrawalDetail.getWithdrawalReason() != null ? 
                    partialWithdrawalDetail.getWithdrawalReason().getId() : null)
            .withdrawalReasonName(partialWithdrawalDetail.getWithdrawalReason() != null ? 
                    partialWithdrawalDetail.getWithdrawalReason().getName() : null)
            .actualWithdrawalAmount(partialWithdrawalDetail.getActualWithdrawalAmount())
            .unemploymentStartDate(partialWithdrawalDetail.getUnemploymentStartDate())
            .disabilityDate(partialWithdrawalDetail.getDisabilityDate())
            .unemploymentCauseId(partialWithdrawalDetail.getUnemploymentCauseMaster() != null ? 
                    partialWithdrawalDetail.getUnemploymentCauseMaster().getId() : null)
            .unemploymentCauseCode(partialWithdrawalDetail.getUnemploymentCauseMaster() != null ? 
                    partialWithdrawalDetail.getUnemploymentCauseMaster().getCode() : null)
            .unemploymentCauseName(partialWithdrawalDetail.getUnemploymentCauseMaster() != null ? 
                    partialWithdrawalDetail.getUnemploymentCauseMaster().getName() : null)
            .incidentDate(partialWithdrawalDetail.getIncidentDate())
            .placeOfIncident(partialWithdrawalDetail.getPlaceOfIncident())
            .businessTypeId(partialWithdrawalDetail.getBusinessType() != null ? 
                    partialWithdrawalDetail.getBusinessType().getId() : null)
            .businessTypeName(partialWithdrawalDetail.getBusinessType() != null ? 
                    partialWithdrawalDetail.getBusinessType().getName() : null)
            .businessName(partialWithdrawalDetail.getBusinessName())
            .proposedInvestmentAmount(partialWithdrawalDetail.getProposedInvestmentAmount())
            .housePurchaseType(partialWithdrawalDetail.getHousePurchaseType())
            .propertyLocation(partialWithdrawalDetail.getPropertyLocation())
            .estimatedCost(partialWithdrawalDetail.getEstimatedCost())
            .description(partialWithdrawalDetail.getDescription())
            .createdBy(partialWithdrawalDetail.getCreatedBy())
            .createdAt(partialWithdrawalDetail.getCreatedAt() != null ? 
                    partialWithdrawalDetail.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(partialWithdrawalDetail.getUpdatedBy())
            .updatedAt(partialWithdrawalDetail.getUpdatedAt() != null ? 
                    partialWithdrawalDetail.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

// ========== LEGAL RECOVERY DETAIL MAPPING ==========

private LegalRecoveryResponseDto mapLegalRecoveryDetail(ClaimDetail claimDetail) {
    LegalRecoveryDetail legalRecoveryDetail = legalRecoveryDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
        if (legalRecoveryDetail == null) {
        return null;
    }
    return LegalRecoveryResponseDto.builder()
            .id(legalRecoveryDetail.getId())
            .claimApplicationId(legalRecoveryDetail.getClaimApplication() != null ? 
                    legalRecoveryDetail.getClaimApplication().getId() : null)
            .claimApplicationNumber(legalRecoveryDetail.getClaimApplication() != null ? 
                    legalRecoveryDetail.getClaimApplication().getApplicationNumber() : null)
            .claimDetailId(legalRecoveryDetail.getClaimDetail() != null ? 
                    legalRecoveryDetail.getClaimDetail().getId() : null)
            .judgementNumber(legalRecoveryDetail.getJudgementNumber())
            .payeeTypeId(legalRecoveryDetail.getPayeeType() != null ? 
                    legalRecoveryDetail.getPayeeType().getId() : null)
            .payeeTypeName(legalRecoveryDetail.getPayeeType() != null ? 
                    legalRecoveryDetail.getPayeeType().getName() : null)
            .judgementDate(legalRecoveryDetail.getJudgementDate())
            .reason(legalRecoveryDetail.getReason())
            .currentStatusName(null) // No StatusMaster relationship in entity
            .createdBy(legalRecoveryDetail.getCreatedBy())
            .createdAt(legalRecoveryDetail.getCreatedAt())
            .updatedBy(legalRecoveryDetail.getUpdatedBy())
            .updatedAt(legalRecoveryDetail.getUpdatedAt())
            .build();
}

// ========== CALCULATION SUMMARY MAPPING ==========

private ClaimCalculationSummaryResponseDto mapCalculationSummary(ClaimDetail claimDetail) {
    ClaimCalculationSummary calculationSummary = claimCalculationSummaryRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
        if (calculationSummary == null) {
        return null;
    }
    return ClaimCalculationSummaryResponseDto.builder()
            .id(calculationSummary.getId())
            .calculationEffectiveDate(calculationSummary.getCalculationEffectiveDate())
            .finalPayableAmount(calculationSummary.getFinalPayableAmount())
            .totalAmount(calculationSummary.getTotalAmount())
            .isPfEligible(calculationSummary.getIsPfEligible())
            .isPensionEligible(calculationSummary.getIsPensionEligible())
            .totalContributionMonth(calculationSummary.getTotalContributionMonth())
            .recommendedBenefitType(calculationSummary.getRecommendedBenefitType())
            .ruleEvaluations(mapRuleEvaluations(calculationSummary.getRuleEvaluations()))
            .createdBy(calculationSummary.getCreatedBy())
            .createdAt(calculationSummary.getCreatedAt() != null ? calculationSummary.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(calculationSummary.getUpdatedBy())
            .updatedAt(calculationSummary.getUpdatedAt() != null ? calculationSummary.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

// ========== RULE EVALUATIONS MAPPING ==========

private List<ClaimRuleEvaluationListDto> mapRuleEvaluations(List<ClaimRuleEvaluation> ruleEvaluations) {
    if (ruleEvaluations == null || ruleEvaluations.isEmpty()) {
        return null;
    }
    return ruleEvaluations.stream()
            .filter(Objects::nonNull)
            .map(rule -> {
                return ClaimRuleEvaluationListDto.builder()
                        .id(rule.getId())
                        .calculationSummaryId(rule.getCalculationSummary() != null ? rule.getCalculationSummary().getId() : null)
                        .subClaimCode(rule.getSubRule().getSubClaimCode())
                        .subClaimType(rule.getSubRule().getSubClaimType())
                        .subClaimDesc(rule.getSubRule().getSubClaimDesc())
                        .ruleCode(rule.getSubRule().getRuleType().getCode())
                        .evaluatedAt(rule.getEvaluatedAt().toLocalDateTime())
                        .remarks(rule.getRemarks())
                        .components(mapCalculationComponents(rule.getComponents()))
                        .build();
            })
            .collect(Collectors.toList());
}

// ========== CALCULATION COMPONENTS MAPPING ==========

private List<ClaimCalculationComponentDto> mapCalculationComponents(List<ClaimCalculationComponent> components) {
    
        if (components == null || components.isEmpty()) {
        return null;
    }
    return components.stream()
            .filter(Objects::nonNull)
            .map(component -> {
                return ClaimCalculationComponentDto.builder()
                        .id(component.getId())
                        .ruleEvaluationId(component.getRuleEvaluation() != null ? component.getRuleEvaluation().getId() : null)
                        .componentCode(component.getComponentMaster().getCode())
                        .componentName(component.getComponentMaster().getName())
                        .amount(component.getAmount())
                        .createdBy(component.getCreatedBy())
                        .createdAt(component.getCreatedAt() != null ? component.getCreatedAt().toLocalDateTime() : null)
                        .updatedBy(component.getUpdatedBy())
                        .updatedAt(component.getUpdatedAt() != null ? component.getUpdatedAt().toLocalDateTime() : null)
                        .build();
            })
            .collect(Collectors.toList());
}

private AccountingEventResponseDto mapAccountingEvent(ClaimDetail claimDetail) {
    ClaimAccountingEvent accountingEvent = claimAccountingEventRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
    if (accountingEvent == null) {
        
    }
if (accountingEvent == null) {
        return null;
    }
    return AccountingEventResponseDto.builder()
            .id(accountingEvent.getId())
            .eventType(accountingEvent.getEventType())
            .claimDetailId(claimDetail.getId())
            .claimApplicationNumber(accountingEvent.getClaimApplicationNumber())
            .nppfNumber(accountingEvent.getNppfNumber())
            .identityNumber(accountingEvent.getIdentityNumber())
            .memberName(accountingEvent.getMemberName())
            .agencyCategoryId(accountingEvent.getAgencyCategoryId())
            .agencyCode(accountingEvent.getAgencyCode())
            .agencyName(accountingEvent.getAgencyName())
            .status(accountingEvent.getStatus())
            .postedBy(accountingEvent.getPostedBy())
            .postedAt(accountingEvent.getPostedAt())
            .createdBy(accountingEvent.getCreatedBy())
            .createdAt(accountingEvent.getCreatedAt())
            .updatedBy(accountingEvent.getUpdatedBy())
            .updatedAt(accountingEvent.getUpdatedAt())
            .ledgerEntries(mapLedgerEntries(accountingEvent.getLedgerEntries()))
            .build();
}

private List<AccountingEventResponseDto.LedgerEntryResponseDto> mapLedgerEntries(List<ClaimLedgerEntry> ledgerEntries) {
    if (ledgerEntries == null || ledgerEntries.isEmpty()) {
        return null;
    }
    return ledgerEntries.stream()
            .filter(Objects::nonNull)
            .map(entry -> {
                CoaMainAccount main = coaMainAccountRepository.findByAccountCode(entry.getMainAccountCode()).orElse(null);
                CoaSubAccount sub = coaSubAccountRepository.findBySubAccountCode(entry.getSubAccountCode()).orElse(null);
                return AccountingEventResponseDto.LedgerEntryResponseDto.builder()
                        .id(entry.getId())
                        .seqNo(entry.getSeqNo())
                        .mainAccountCode(entry.getMainAccountCode())
                        .mainAccountName(main.getAccountName()) // Will need to fetch from COA table if needed
                        .subAccountCode(entry.getSubAccountCode())
                        .subAccountName(sub.getSubAccountName()) // Will need to fetch from COA table if needed
                        .drcr(entry.getDrcr())
                        .amount(entry.getAmount())
                        .entryRole(entry.getEntryRole())
                        .componentCode(entry.getComponentCode())
                        .narration(entry.getNarration())
                        .createdBy(entry.getCreatedBy())
                        .createdAt(entry.getCreatedAt())
                        .build();
            })
            .collect(Collectors.toList());
}

// ========== FORFEITED COMPONENTS MAPPING ==========

private List<ClaimForfeitedComponentResponseDto> mapForfeitedComponents(ClaimDetail claimDetail) {
    List<ClaimForfeitedComponent> forfeitedComponents = claimForfeitedComponentRepository.findByClaimDetail_Id(claimDetail.getId());
    if (forfeitedComponents == null || forfeitedComponents.isEmpty()) {
        return null;
    }
    return forfeitedComponents.stream()
            .filter(Objects::nonNull)
            .map(component -> {
                return ClaimForfeitedComponentResponseDto.builder()
                        .id(component.getId())
                        .componentCode(component.getComponentCode())
                        .componentName(component.getComponentName())
                        .componentType(component.getComponentType())
                        .amount(component.getAmount())
                        .ruleCode(component.getRuleCode())
                        .subClaimCode(component.getSubClaimCode())
                        .createdBy(component.getCreatedBy())
                        .createdAt(component.getCreatedAt() != null ? component.getCreatedAt().toLocalDateTime() : null)
                        .updatedBy(component.getUpdatedBy())
                        .updatedAt(component.getUpdatedAt() != null ? component.getUpdatedAt().toLocalDateTime() : null)
                        .build();
            })
            .collect(Collectors.toList());
}

// ========== ONLY BANK DETAILS MAPPING ==========

private List<ClaimBankResponseDto> mapBankDetails(ClaimDetail claimDetail) {
    List<ClaimBankDetail> bankDetails = claimBankDetailRepository.findByClaimDetail_Id(claimDetail.getId());
    if (bankDetails == null || bankDetails.isEmpty()) {
        return null;
    }
    return bankDetails.stream()
            .filter(Objects::nonNull)
            .map(bank -> {
                return ClaimBankResponseDto.builder()
                        .id(bank.getId())
                        .beneficiaryIdentifier(bank.getBeneficiaryIdentifier())
                        .claimantTypeId(bank.getClaimantType() != null ? bank.getClaimantType().getId() : null)
                        .claimantTypeName(bank.getClaimantType() != null ? bank.getClaimantType().getName() : null)
                        .bankTypeId(bank.getBankType() != null ? bank.getBankType().getBankTypeId() : null)
                        .bankTypeName(bank.getBankType() != null ? bank.getBankType().getBankTypeName() : null)
                        .accountNumber(bank.getAccountNumber())
                        .accountHolderName(bank.getAccountHolderName())
                        .ifscOrRoutingCode(bank.getIfscOrRoutingCode())
                        .isDefaultBank(bank.getIsDefaultBank())
                        .verifiedBy(bank.getVerifiedBy())
                        .verifiedAt(bank.getVerifiedAt() != null ? bank.getVerifiedAt().toLocalDateTime() : null)
                        .createdBy(bank.getCreatedBy())
                        .createdAt(bank.getCreatedAt() != null ? bank.getCreatedAt().toLocalDateTime() : null)
                        .updatedBy(bank.getUpdatedBy())
                        .updatedAt(bank.getUpdatedAt() != null ? bank.getUpdatedAt().toLocalDateTime() : null)
                        .build();
            })
            .collect(Collectors.toList());
}

private ClaimDeductionResponseDto mapDeductionDetail(ClaimDetail claimDetail) {
    ClaimDeductionDetail deductionDetail = claimDeductionDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
    if (deductionDetail == null) {
        return null;
    }
    return ClaimDeductionResponseDto.builder()
            .id(deductionDetail.getId())
            .outstandingAmount(deductionDetail.getOutstandingAmount())
            .verifiedDeductedAmount(deductionDetail.getVerifiedDeductedAmount())
            .approvedDeductedAmount(deductionDetail.getApprovedDeductedAmount())
            .deductedAmount(deductionDetail.getDeductedAmount())
            .remarks(deductionDetail.getRemarks())
            .deductionItems(mapDeductionItems(deductionDetail.getDeductionItems()))
            .createdBy(deductionDetail.getCreatedBy())
            .createdAt(deductionDetail.getCreatedAt() != null ? deductionDetail.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(deductionDetail.getUpdatedBy())
            .updatedAt(deductionDetail.getUpdatedAt() != null ? deductionDetail.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private List<ClaimDeductionItemResponseDto> mapDeductionItems(List<ClaimDeductionItem> deductionItems) {
    if (deductionItems == null || deductionItems.isEmpty()) {
        return null;
    }
    return deductionItems.stream()
            .filter(Objects::nonNull)
            .map(item -> {
                return ClaimDeductionItemResponseDto.builder()
                        .id(item.getId())
                        .deductionCategory(item.getDeductionCategory())
                        .outstandingAmount(item.getOutstandingAmount())
                        .deductedAmount(item.getDeductedAmount())
                        .remainingAmount(item.getRemainingAmount())
                        .remarks(item.getRemarks())
                        .createdBy(item.getCreatedBy())
                        .createdAt(item.getCreatedAt() != null ? item.getCreatedAt().toLocalDateTime() : null)
                        .updatedBy(item.getUpdatedBy())
                        .updatedAt(item.getUpdatedAt() != null ? item.getUpdatedAt().toLocalDateTime() : null)
                        .build();
            })
            .collect(Collectors.toList());
}

    private LegalRecoveryDetail saveLegalRecoveryDetail(GeneralClaimResponse requestResponse, ClaimDetail claimDetail) {
        // FIXED: Check if legalRecoveryDetails exists and has ID
        LegalRecoveryDetail legalRecoveryDetail = null;
        if (requestResponse.getLegalRecoveryDetail() != null
                && requestResponse.getLegalRecoveryDetail().getId() != null) {
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

    private List<ClaimBankDetail> saveBankDetails(List<ClaimApplicationBankResponseDto> bankDetails,
            ClaimDetail claimDetail) {
        if (bankDetails == null || bankDetails.isEmpty()) {
            return List.of();
        }

        List<ClaimBankDetail> claimBankDetails = bankDetails.stream()
                .filter(Objects::nonNull)
                .map(bankDetailResponse -> {
                    ClaimBankDetail bankDetail = allClaimDetailMapper.toBankDetailEntity(bankDetailResponse);
                    BankType bankType = bankTypeRepository.findByBankTypeId(bankDetailResponse.getBankTypeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Bank Type not found with ID: " + bankDetailResponse.getBankTypeId()));
                    if(bankDetailResponse.getClaimantTypeId() != null && bankDetailResponse.getClaimantTypeId() > 0) {
                        ClaimantTypeMaster claimantTypeMaster = claimantTypeRepository
                            .findById(bankDetailResponse.getClaimantTypeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Claimant Type not found with ID: " + bankDetailResponse.getClaimantTypeId()));
                        bankDetail.setClaimantType(claimantTypeMaster);
                    }
                    
                    bankDetail.setClaimDetail(claimDetail);
                    bankDetail.setBankType(bankType);
                    return bankDetail;
                })
                .toList();

        if (!claimBankDetails.isEmpty()) {
            claimBankDetailRepository.saveAllAndFlush(claimBankDetails);
        }
        return claimBankDetails;
    }

    private ClaimDeductionDetail saveDeductionDetail(ClaimApplicationDeductionResponseDto deductionDetailResponse,
            ClaimDetail claimDetail) {
        if (deductionDetailResponse == null) {
            return null;
        }

        ClaimDeductionDetail deductionDetail = allClaimDetailMapper.toDeductionDetailEntity(deductionDetailResponse);
        deductionDetail.setClaimDetail(claimDetail);
        deductionDetail = claimDeductionDetailRepository.saveAndFlush(deductionDetail);
        System.out.println("deduction detail id: " + deductionDetail.getId());
        // Save deduction items
        if (deductionDetailResponse.getDeductionItems() != null
                && !deductionDetailResponse.getDeductionItems().isEmpty()) {
            saveDeductionItems(deductionDetailResponse.getDeductionItems(), deductionDetail);
        }

        return deductionDetail;
    }

    private void saveDeductionItems(List<ClaimApplicationDeductionItemResponseDto> deductionItems,
            ClaimDeductionDetail deductionDetail) {
        if (deductionItems == null || deductionItems.isEmpty()) {
            return;
        }

        List<ClaimDeductionItem> claimDeductionItems = deductionItems.stream()
                .filter(Objects::nonNull)
                .map(itemResponse -> {
                    ClaimDeductionItem item = allClaimDetailMapper.toDeductionItemEntity(itemResponse);
                    item.setDeductionDetail(deductionDetail);
                    claimDeductionItemRepository.saveAndFlush(item);
                    return item;
                })
                .toList();

        if (!claimDeductionItems.isEmpty()) {
            claimDeductionItemRepository.saveAllAndFlush(claimDeductionItems);
        }
    }

    private ClaimCalculationSummary saveCalculationSummary(
            ClaimApplicationCalculationSummaryResponseDto calculationSummaryResponse, ClaimDetail claimDetail) {
        if (calculationSummaryResponse == null) {
            return null;
        }
        System.out.println("Calculation Summary Response: " + claimDetail.getId() + " - " + calculationSummaryResponse);
        ClaimCalculationSummary calculationSummary = allClaimDetailMapper
                .toCalculationSummaryEntity(calculationSummaryResponse);
        calculationSummary.setClaimDetail(claimDetail);
        calculationSummary = claimCalculationSummaryRepository.saveAndFlush(calculationSummary);

        // Save rule evaluations
        if (calculationSummaryResponse.getRuleEvaluations() != null
                && !calculationSummaryResponse.getRuleEvaluations().isEmpty()) {
            saveRuleEvaluations(calculationSummaryResponse.getRuleEvaluations(), calculationSummary);
        }

        return calculationSummary;
    }

    private void saveRuleEvaluations(
        List<ClaimApplicationRuleEvaluationListDto> ruleEvaluations,
        ClaimCalculationSummary calculationSummary) {
    
    if (ruleEvaluations == null || ruleEvaluations.isEmpty()) {
        // Clear existing if any
        if (calculationSummary.getRuleEvaluations() != null) {
            calculationSummary.getRuleEvaluations().clear();
        }
        return;
    }

    log.info("Saving {} rule evaluations", ruleEvaluations.size());

    // Clear existing rule evaluations first
    if (calculationSummary.getRuleEvaluations() != null) {
        // Remove all existing rule evaluations (they will be deleted)
        calculationSummary.getRuleEvaluations().clear();
    }

    for (ClaimApplicationRuleEvaluationListDto ruleEvaluationDto : ruleEvaluations) {
        if (ruleEvaluationDto == null) continue;

        log.info("Processing rule evaluation for subClaimCode: {}", ruleEvaluationDto.getSubClaimCode());

        // Create new rule evaluation entity
        ClaimRuleEvaluation rule = allClaimDetailMapper.toRuleEvaluationEntity(ruleEvaluationDto);

        // Find and set SubClaimMapping
        if (ruleEvaluationDto.getSubClaimCode() != null) {
            SubClaimMapping subClaimMapping = subClaimMappingRepository
                    .findBySubClaimCodeIgnoreCase(ruleEvaluationDto.getSubClaimCode())
                    .orElseThrow(() -> new RuntimeException(
                            "Sub Claim Mapping not found with code: " + ruleEvaluationDto.getSubClaimCode()));
            rule.setSubRule(subClaimMapping);
        }

        // Set relationship
        rule.setCalculationSummary(calculationSummary);

        // Save the rule evaluation first
        rule = claimRuleEvaluationRepository.saveAndFlush(rule);
        log.info("RuleEvaluation saved with ID: {}", rule.getId());

        // Save components if any
        if (ruleEvaluationDto.getComponents() != null && !ruleEvaluationDto.getComponents().isEmpty()) {
            saveCalculationComponents(ruleEvaluationDto.getComponents(), rule);
        }

        // Add to the collection AFTER saving (don't replace)
        calculationSummary.getRuleEvaluations().add(rule);
    }

    // Save the calculation summary to persist the relationship
    claimCalculationSummaryRepository.saveAndFlush(calculationSummary);
    log.info("All rule evaluations saved successfully");
}


    private void saveCalculationComponents(
        List<ClaimApplicationCalculationComponentDto> calculationComponents,
        ClaimRuleEvaluation ruleEvaluation) {
    
    if (calculationComponents == null || calculationComponents.isEmpty()) {
        // Clear existing if any
        if (ruleEvaluation.getComponents() != null) {
            ruleEvaluation.getComponents().clear();
        }
        return;
    }

    log.info("Saving {} calculation components for rule evaluation ID: {}", 
        calculationComponents.size(), ruleEvaluation.getId());

    // Clear existing components first
    if (ruleEvaluation.getComponents() != null) {
        ruleEvaluation.getComponents().clear();
    }

    for (ClaimApplicationCalculationComponentDto componentResponse : calculationComponents) {
        if (componentResponse == null) continue;

        log.info("Processing component: {}", componentResponse.getComponentCode());

        // Create new component entity
        ClaimCalculationComponent component = allClaimDetailMapper
                .toCalculationComponentEntity(componentResponse);

        // Find and set ComponentMaster
        if (componentResponse.getComponentCode() != null) {
            ComponentMaster componentMaster = componentMasterRepository
                    .findByCode(componentResponse.getComponentCode())
                    .orElseThrow(() -> new RuntimeException(
                            "Component Master not found with code: " + componentResponse.getComponentCode()));
            component.setComponentMaster(componentMaster);
        }

        // Set relationship
        component.setRuleEvaluation(ruleEvaluation);

        // Save the component
        component = claimCalculationComponentRepository.saveAndFlush(component);
        log.info("CalculationComponent saved with ID: {}", component.getId());

        // Add to collection AFTER saving
        ruleEvaluation.getComponents().add(component);
    }

    // Save the rule evaluation to persist the relationship
    claimRuleEvaluationRepository.saveAndFlush(ruleEvaluation);
    log.info("All calculation components saved successfully");
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
                    ClaimForfeitedComponent component = allClaimDetailMapper
                            .toForfeitedComponentEntity(componentResponse);
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