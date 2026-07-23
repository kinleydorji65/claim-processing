package com.claim.claim_processing.application.service.application.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationSummaryRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimPatchRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionDetail;
import com.claim.claim_processing.application.entity.application.ClaimApplicationForfeitedComponent;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;
import com.claim.claim_processing.application.mapper.application.GeneralClaimResponseBuilderMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.BeneficiarySettlementResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationBankResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationCalculationSummaryResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationDeductionResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationForfeitedComponentResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.LegalRecoveryResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.NormalClaimResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.PartialWithdrawalResponseMapper;
import com.claim.claim_processing.application.service.application.ClaimApplicationBankDetailService;
import com.claim.claim_processing.application.service.application.ClaimApplicationCalculationService;
import com.claim.claim_processing.application.service.application.ClaimApplicationDeductionDetailService;
import com.claim.claim_processing.application.service.application.ClaimApplicationFlowService;
import com.claim.claim_processing.application.service.application.ClaimApplicationForfeitedComponentService;
import com.claim.claim_processing.application.service.application.ClaimApplicationService;
import com.claim.claim_processing.application.service.detail.BeneficiarySettlementDetailService;
import com.claim.claim_processing.application.service.detail.LegalRecoveryService;
import com.claim.claim_processing.application.service.detail.NormalClaimService;
import com.claim.claim_processing.application.service.detail.PartialWithdrawalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimApplicationFlowServiceImpl implements ClaimApplicationFlowService {

        private final ClaimApplicationService claimApplicationService;
        private final ClaimApplicationCalculationService claimApplicationCalculationService;
        private final ClaimApplicationBankDetailService claimApplicationBankDetailService;
        private final ClaimApplicationDeductionDetailService claimApplicationDeductionDetailService;
        private final ClaimApplicationForfeitedComponentService claimApplicationForfeitedComponentService;
        private final ClaimApplicationWorkflowService claimApplicationWorkflowService;
        private final ClaimApplicationVerificationService claimApplicationVerificationService;

        private final NormalClaimService normalClaimService;
        private final PartialWithdrawalService partialWithdrawalService;
        private final BeneficiarySettlementDetailService beneficiarySettlementDetailService;
        private final ClaimApplicationApprovalService claimApplicationApprovalService;

        private final BenefitCalculationService benefitCalculationService;
        private final LegalRecoveryService legalRecoveryService;

        private final GeneralClaimResponseBuilderMapper generalClaimResponseBuilderMapper;
        private final BeneficiarySettlementResponseMapper beneficiarySettlementResponseMapper;
        private final ClaimApplicationBankResponseMapper claimApplicationBankResponseMapper;
        private final ClaimApplicationCalculationSummaryResponseMapper claimApplicationCalculationSummaryResponseMapper;
        private final ClaimApplicationDeductionResponseMapper claimApplicationDeductionResponseMapper;
        private final ClaimApplicationForfeitedComponentResponseMapper claimApplicationForfeitedComponentResponseMapper;
        private final NormalClaimResponseMapper normalClaimResponseMapper;
        private final PartialWithdrawalResponseMapper partialWithdrawalResponseMapper;
        private final LegalRecoveryResponseMapper legalRecoveryResponseMapper;

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimResponse> create(GeneralClaimCreateRequest request) {

                if (request == null) {
                        throw ClaimException.badRequest("Request body is required");
                }

                ClaimApplication claimApplication = claimApplicationService.create(request.getClaimApplication());

                NormalClaimDetail normalClaimDetail = null;
                PartialWithdrawalDetail partialWithdrawalDetail = null;
                BeneficiarySettlementDetail beneficiarySettlementDetail = null;
                LegalRecoveryDetail legalRecoveryDetail = null;

                List<ClaimApplicationBankDetail> bankDetailEntities = new ArrayList<>();
                ClaimApplicationCalculationSummary calculationEntity = null;
                if (request.getNormalClaim() != null) {
                        normalClaimDetail = normalClaimService.create(
                                        claimApplication,
                                        request.getNormalClaim());
                }

                if (request.getPartialWithdrawal() != null) {
                        partialWithdrawalDetail = partialWithdrawalService.create(
                                        claimApplication,
                                        request.getPartialWithdrawal());
                }

                if (request.getBeneficiarySettlement() != null) {
                        beneficiarySettlementDetail = beneficiarySettlementDetailService.create(
                                        claimApplication,
                                        request.getBeneficiarySettlement());
                }

                if (request.getLegalRecovery() != null) {
                        legalRecoveryDetail = legalRecoveryService.create(request.getLegalRecovery(), claimApplication);
                }

                if (request.getBankDetails() != null && !request.getBankDetails().isEmpty()) {
                        bankDetailEntities = claimApplicationBankDetailService.create(
                                        claimApplication,
                                        request.getBankDetails());
                }

                if (request.getClaimApplicationOther() != null) {

                        calculationEntity = claimApplicationCalculationService.initialCreate(claimApplication,
                                        request.getClaimApplicationOther());

                }
                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                .builder()
                                .fromStageId(request.getClaimApplication().getFromStageId())
                                .toStageId(request.getClaimApplication().getToStageId())
                                .toStageId(request.getClaimApplication().getToStageId())
                                .fromStatusId(request.getClaimApplication().getFromStatusId())
                                .toStatusId(request.getClaimApplication().getToStatusId())
                                .reason(request.getClaimApplication().getReason())
                                .actionBy(request.getClaimApplication().getCreatedBy())
                                .build();

                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .create(claimApplication, workflowRequest);

                GeneralClaimResponse response = generalClaimResponseBuilderMapper.toResponse(claimApplication);
                response.setWorkflowDetails(workflowDetails);

                response.setBankDetails(
                                bankDetailEntities.stream()
                                                .map(claimApplicationBankResponseMapper::toResponse)
                                                .toList());

                response.setCalculationSummary(
                                claimApplicationCalculationSummaryResponseMapper.toResponse(calculationEntity));

                response.setNormalClaimDetails(
                                normalClaimResponseMapper.toResponse(normalClaimDetail));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper.toResponse(partialWithdrawalDetail));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper.toResponse(beneficiarySettlementDetail));
                response.setCalculationSummary(
                                claimApplicationCalculationSummaryResponseMapper.toResponse(calculationEntity));
                response.setLegalRecoveryDetail(legalRecoveryResponseMapper.toResponse(legalRecoveryDetail));
                return ApiResponseDTO.success(response);
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimResponse> createForVerifier(Long applicationId,
                        ClaimApplicationCalculationSummaryRequest request) {
                ClaimApplication claimApplication = claimApplicationService.getById(applicationId);

                ClaimApplicationCalculationSummary calculationEntity = null;
                ClaimApplicationDeductionDetail deductionEntity = null;
                List<ClaimApplicationForfeitedComponent> forfeitedComponents = new ArrayList<>();

                if (request != null) {
                        calculationEntity = claimApplicationCalculationService.createForCalculation(
                                        claimApplication,
                                        request);

                        if (request.getForFeitedComponents() != null
                                        && !request.getForFeitedComponents().isEmpty()) {
                                forfeitedComponents = claimApplicationForfeitedComponentService.saveForfeitedComponents(
                                                claimApplication,
                                                request.getForFeitedComponents());
                        }
                        deductionEntity = claimApplicationDeductionDetailService.saveCalculationDeductions(
                                        claimApplication,
                                        request.getDeductionDetail());

                }
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());
                List<ClaimApplicationBankDetail> bankDetailEntities = claimApplicationBankDetailService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());
                GeneralClaimResponse response = generalClaimResponseBuilderMapper.toResponse(claimApplication);
                response.setWorkflowDetails(workflowDetails);

                response.setBankDetails(
                                bankDetailEntities.stream()
                                                .map(claimApplicationBankResponseMapper::toResponse)
                                                .toList());

                response.setCalculationSummary(
                                claimApplicationCalculationSummaryResponseMapper.toResponse(calculationEntity));

                response.setNormalClaimDetails(
                                normalClaimResponseMapper.toResponse(claimApplication.getNormalClaimDetail()));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper
                                                .toResponse(claimApplication.getPartialWithdrawalDetail()));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper
                                                .toResponse(claimApplication.getBeneficiarySettlementDetail()));
                response.setCalculationSummary(
                                claimApplicationCalculationSummaryResponseMapper.toResponse(calculationEntity));
                response.setLegalRecoveryDetail(
                                legalRecoveryResponseMapper.toResponse(claimApplication.getLegalRecoveryDetail()));
                response.setDeductionDetail(
                                claimApplicationDeductionResponseMapper.toResponse(deductionEntity));

                response.setForfeitedComponents(
                                forfeitedComponents.stream()
                                                .map(claimApplicationForfeitedComponentResponseMapper::toResponse)
                                                .toList());
                return null;
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimResponse> patch(GeneralClaimPatchRequest request) {

                if (request == null || request.getClaimApplication() == null
                                || request.getClaimApplication().getApplicationId() == null) {
                        throw ClaimException.badRequest("Claim application id is required");
                }

                ClaimApplication claimApplication = claimApplicationService.update(request.getClaimApplication());

                NormalClaimDetail normalClaimDetail = null;
                PartialWithdrawalDetail partialWithdrawalDetail = null;
                BeneficiarySettlementDetail beneficiarySettlementDetail = null;

                List<ClaimApplicationBankDetail> bankDetails = new ArrayList<>();
                ClaimApplicationCalculationSummary calculationSummary = null;
                ClaimApplicationDeductionDetail deductionDetail = null;
                List<ClaimApplicationForfeitedComponent> forfeitedComponents = new ArrayList<>();

                if (request.getNormalClaim() != null) {
                        normalClaimDetail = normalClaimService.update(
                                        claimApplication,
                                        request.getNormalClaim());
                }

                if (request.getPartialWithdrawal() != null) {
                        partialWithdrawalDetail = partialWithdrawalService.update(
                                        request.getPartialWithdrawal());
                }

                if (request.getBeneficiarySettlementDetail() != null) {
                        beneficiarySettlementDetail = beneficiarySettlementDetailService.patch(
                                        request.getBeneficiarySettlementDetail());
                }

                if (request.getClaimApplicationBankDetail() != null
                                && !request.getClaimApplicationBankDetail().isEmpty()) {
                        bankDetails = claimApplicationBankDetailService.patch(
                                        claimApplication,
                                        request.getClaimApplicationBankDetail());
                }

                // if (request.getClaimApplicationCalculation() != null) {
                // calculationSummary = claimApplicationCalculationService.patch(
                // request.getClaimApplicationCalculation());
                // }

                if (request.getClaimApplicationDeduction() != null) {
                        deductionDetail = claimApplicationDeductionDetailService.patchDeductionDetail(
                                        request.getClaimApplicationDeduction());
                }

                if (request.getClaimApplicationForfeitedComponent() != null
                                && !request.getClaimApplicationForfeitedComponent().isEmpty()) {
                        forfeitedComponents = claimApplicationForfeitedComponentService.patchForfeitedComponent(
                                        request.getClaimApplicationForfeitedComponent());
                }

                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                .builder()
                                .fromStageId(request.getClaimApplication().getFromStageId())
                                .toStageId(request.getClaimApplication().getToStageId())
                                .toStageId(request.getClaimApplication().getToStageId())
                                .fromStatusId(request.getClaimApplication().getFromStatusId())
                                .toStatusId(request.getClaimApplication().getToStatusId())
                                .reason(request.getClaimApplication().getReason())
                                .actionBy(request.getClaimApplication().getCreatedBy())
                                .build();
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .create(claimApplication, workflowRequest);

                GeneralClaimResponse response = generalClaimResponseBuilderMapper.toResponse(claimApplication);
                response.setWorkflowDetails(workflowDetails);
                response.setBankDetails(
                                bankDetails.stream()
                                                .map(claimApplicationBankResponseMapper::toResponse)
                                                .toList());

                response.setDeductionDetail(
                                claimApplicationDeductionResponseMapper.toResponse(deductionDetail));

                response.setCalculationSummary(
                                claimApplicationCalculationSummaryResponseMapper.toResponse(calculationSummary));

                response.setNormalClaimDetails(
                                normalClaimResponseMapper.toResponse(normalClaimDetail));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper.toResponse(partialWithdrawalDetail));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper.toResponse(beneficiarySettlementDetail));

                response.setForfeitedComponents(
                                claimApplicationForfeitedComponentResponseMapper.toResponseList(
                                                forfeitedComponents));

                return ApiResponseDTO.success(
                                "Claim application patched successfully",
                                response);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> getAll() {

                List<ClaimApplication> claimApplications = claimApplicationService.getAll();

                List<GeneralClaimResponse> responses = claimApplications.stream()
                                .map(this::buildGeneralClaimResponse)
                                .toList();

                return ApiResponseDTO.success(
                                "Claim applications fetched successfully",
                                responses);
        }

        private GeneralClaimResponse buildGeneralClaimResponse(
                        ClaimApplication claimApplication) {

                if (claimApplication == null) {
                        return null;
                }

                GeneralClaimResponse response = generalClaimResponseBuilderMapper.toResponse(claimApplication);

                response.setNormalClaimDetails(
                                normalClaimResponseMapper.toResponse(
                                                claimApplication.getNormalClaimDetail() != null
                                                                ? claimApplication.getNormalClaimDetail()
                                                                : null));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper.toResponse(
                                                claimApplication.getPartialWithdrawalDetail() != null
                                                                ? claimApplication.getPartialWithdrawalDetail()
                                                                : null));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper.toResponse(
                                                claimApplication.getBeneficiarySettlementDetail() != null
                                                                ? claimApplication.getBeneficiarySettlementDetail()
                                                                : null));

                response.setBankDetails(
                                claimApplication.getBankDetails() == null
                                                ? List.of()
                                                : claimApplication.getBankDetails()
                                                                .stream()
                                                                .map(claimApplicationBankResponseMapper::toResponse)
                                                                .toList());

                response.setDeductionDetail(
                                claimApplication.getDeductionDetail() == null
                                                ? null
                                                : claimApplicationDeductionResponseMapper.toResponse(
                                                                claimApplication.getDeductionDetail()));

                response.setCalculationSummary(
                                claimApplication.getCalculationSummary() == null
                                                ? null
                                                : claimApplicationCalculationSummaryResponseMapper.toResponse(
                                                                claimApplication.getCalculationSummary()));

                response.setForfeitedComponents(
                                claimApplication.getForfeitedComponents() == null
                                                ? List.of()
                                                : claimApplication.getForfeitedComponents()
                                                                .stream()
                                                                .map(claimApplicationForfeitedComponentResponseMapper::toResponse)
                                                                .toList());
                response.setWorkflowDetails(
                                claimApplicationWorkflowService.getByApplicationId(claimApplication.getId()) != null
                                                ? claimApplicationWorkflowService
                                                                .getByApplicationId(claimApplication.getId())
                                                : List.of());

                response.setVerificationDetail(
                                claimApplicationVerificationService
                                                .getByApplicationNumber(claimApplication.getApplicationNumber()) != null
                                                                ? (claimApplicationVerificationService
                                                                                .getByApplicationNumber(claimApplication
                                                                                                .getApplicationNumber()) != null)
                                                                                                                ? claimApplicationVerificationService
                                                                                                                                .getByApplicationNumber(
                                                                                                                                                claimApplication.getApplicationNumber())
                                                                                                                                .getData()
                                                                                                                : null
                                                                : null);

                response.setApprovalDetail(
                                claimApplicationApprovalService
                                                .getByApplicationNumber(claimApplication.getApplicationNumber()) != null
                                                                ? (claimApplicationApprovalService
                                                                                .getByApplicationNumber(claimApplication
                                                                                                .getApplicationNumber()) != null)
                                                                                                                ? claimApplicationApprovalService
                                                                                                                                .getByApplicationNumber(
                                                                                                                                                claimApplication.getApplicationNumber())
                                                                                                                                .getData()
                                                                                                                : null
                                                                : null);
                response.setLegalRecoveryDetail(
                                claimApplication.getLegalRecoveryDetail() != null
                                                ? legalRecoveryResponseMapper.toResponse(
                                                                claimApplication.getLegalRecoveryDetail())
                                                : null);
                return response;
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedClaimAndClaimedBy(String claimedBy) {
                List<ClaimApplicationApprovalResponseDto> claimApplications = claimApplicationApprovalService
                                .getVerifiedClaimAndClaimedBy(claimedBy).getData();
                if (claimApplications == null || claimApplications.isEmpty()) {
                        return ApiResponseDTO.success(
                                        "No verified claim applications found",
                                        List.of());
                }

                List<GeneralClaimResponse> responses = claimApplications.stream()
                                .map(verificationResponse -> {
                                        ClaimApplication claimApplication = claimApplicationService
                                                        .getById(verificationResponse.getClaimApplicationId());
                                        return buildGeneralClaimResponse(claimApplication);
                                })
                                .toList();
                return ApiResponseDTO.success(
                                "Verified claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<GeneralClaimResponse> findByApplicationNumber(String applicationNumber) {

                if (applicationNumber == null) {
                        throw ClaimException.badRequest("Application number is required");
                }

                ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);

                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);

                return ApiResponseDTO.success(
                                "Claim application fetched successfully",
                                response);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> getByAgencyCodeAndClaimTypeId(String agencyCode,
                        Long claimTypeId) {

                if (agencyCode == null) {
                        throw ClaimException.badRequest("Agency code is required");
                }

                if (claimTypeId == null) {
                        throw ClaimException.badRequest("Claim type ID is required");
                }

                List<ClaimApplication> claimApplications = claimApplicationService
                                .getByAgencyCodeAndClaimTypeId(agencyCode, claimTypeId);

                List<GeneralClaimResponse> responses = claimApplications.stream()
                                .map(this::buildGeneralClaimResponse)
                                .toList();

                return ApiResponseDTO.success(
                                "Claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> findByNppfNumber(String nppfNumber) {

                if (nppfNumber == null) {
                        throw ClaimException.badRequest("NPPF number is required");
                }

                List<ClaimApplication> claimApplications = claimApplicationService.getByNppfNumber(nppfNumber);

                List<GeneralClaimResponse> responses = claimApplications.stream()
                                .map(this::buildGeneralClaimResponse)
                                .toList();

                return ApiResponseDTO.success(
                                "Claim applications fetched successfully",
                                responses);
        }

        @Override
@Transactional(readOnly = true)
public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> findByInitiatedByAndIsSpecialCase(
        String initiatedBy, 
        ActivityEnum isSpecialCase, 
        Pageable page) {

    if (initiatedBy == null || initiatedBy.isBlank()) {
        throw ClaimException.badRequest("Agency code is required");
    }

    // Get Page from service
    Page<ClaimApplication> claimPage = claimApplicationService.findByInitiatedByAndIsSpecialCase(
            initiatedBy, isSpecialCase, page);

    // Map content to response DTOs
    List<GeneralClaimResponse> responses = claimPage.getContent().stream()
            .map(this::buildGeneralClaimResponse)
            .toList();

    // Add pagination headers
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(claimPage.getTotalElements()));
    headers.add("X-Total-Pages", String.valueOf(claimPage.getTotalPages()));
    headers.add("X-Page-Number", String.valueOf(claimPage.getNumber()));
    headers.add("X-Page-Size", String.valueOf(claimPage.getSize()));

    return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponseDTO.success("Claim applications fetched successfully", responses));
}

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimResponse> claimedBy(String applicationId, String claimedBy) {
                if (applicationId == null) {
                        throw ClaimException.badRequest("Application id is required");
                }

                if (claimedBy == null) {
                        throw ClaimException.badRequest("Claimed by is required");
                }

                ClaimApplication claimApplication = claimApplicationService.claimedBy(applicationId, claimedBy);
                claimApplicationVerificationService.verifiedClaimApplicationClaimedBy(applicationId, claimedBy);

                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);

                return ApiResponseDTO.success(
                                "Claim application fetched successfully",
                                response);

        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimResponse> unClaimedBy(String applicationId, String unclaimedBy) {
                if (applicationId == null) {
                        throw ClaimException.badRequest("Application id is required");
                }

                if (unclaimedBy == null) {
                        throw ClaimException.badRequest("Unclaimed by is required");
                }

                ClaimApplication claimApplication = claimApplicationService.unClaimedBy(applicationId, unclaimedBy);

                List<ClaimApplicationWorkflowResponseDto> workflowResponse = claimApplicationWorkflowService
                                .getByApplicationId(claimApplication.getId());
                if (workflowResponse == null || workflowResponse.isEmpty()) {
                        ClaimApplicationWorkflowResponseDto workFlow = workflowResponse.get(0);
                        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                        .builder()
                                        .fromStageId(workFlow.getFromStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .fromStatusId(workFlow.getFromStatusId())
                                        .toStatusId(4L)
                                        .reason(workFlow.getReason())
                                        .actionBy(workFlow.getActionBy())
                                        .build();
                        claimApplicationWorkflowService
                                        .create(claimApplication, workflowRequest);
                }
                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);
                return ApiResponseDTO.success(
                                "Claim application fetched successfully",
                                response);
        }

        @Override
        @Transactional
        public ApiResponseDTO<List<GeneralClaimResponse>> findByUserCode(String userCode, Long statusId) {
                if (userCode == null) {
                        throw ClaimException.badRequest("User code is required");
                }

                List<ClaimApplication> claimApplications = claimApplicationService.getByUserCodeAndStatusId(userCode,
                                statusId);
                if (claimApplications == null || claimApplications.isEmpty()) {
                        return ApiResponseDTO.success(
                                        "No claim applications found",
                                        List.of());
                }
                List<GeneralClaimResponse> responses = claimApplications.stream()
                                .map(this::buildGeneralClaimResponse)
                                .toList();
                return ApiResponseDTO.success(
                                "Claim application fetched successfully",
                                responses);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationWorkflowResponseDto>> getWorkflowDetails(String applicationNumber) {
                List<ClaimApplicationWorkflowResponseDto> workFlow = claimApplicationWorkflowService
                                .getByApplicationNumber(applicationNumber);
                return ApiResponseDTO.success(
                                "Workflow details fetched successfully",
                                workFlow);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedApplication() {
                List<String> applicationIds = claimApplicationWorkflowService.getVerifiedApplication();

                List<ClaimApplication> claimApplications = applicationIds.stream()
                                .map(claimApplicationService::getByApplicationNumber)
                                .toList();
                List<GeneralClaimResponse> responses = claimApplications.stream()
                                .map(this::buildGeneralClaimResponse)
                                .toList();
                return ApiResponseDTO.success(
                                "Verified claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimResponse> verifiedClaimActionRejectedByApprover(String applicationNumber,
                        String rejectedBy, String rejectedRemarks) {
                ClaimApplicationApprovalResponseDto approveClaim = claimApplicationApprovalService
                                .verifiedClaimActionRejectedByApprover(applicationNumber, rejectedBy, rejectedRemarks)
                                .getData();

                ClaimApplication claimApplication = claimApplicationService
                                .getByApplicationNumber(approveClaim.getApplicationNumber());
                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);

                return ApiResponseDTO.success(
                                "Successfully rejected the claim by approver",
                                response);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> getClaimApplicationWhichClaimedBy(String claimedBy) {
                if (claimedBy == null || claimedBy.isBlank()) {
                        throw ClaimException.badRequest("Claimed by is required");
                }

                List<ClaimApplicationVerificationResponseDto> verificationResponses = claimApplicationVerificationService
                                .getClaimApplicationWhichClaimedBy(claimedBy).getData();
                if (verificationResponses == null || verificationResponses.isEmpty()) {
                        return ApiResponseDTO.success(
                                        "No verified claim applications found",
                                        List.of());
                }
                List<GeneralClaimResponse> responses = verificationResponses.stream()
                                .map(verificationResponse -> {
                                        ClaimApplication claimApplication = claimApplicationService
                                                        .getById(verificationResponse.getClaimApplicationId());
                                        return buildGeneralClaimResponse(claimApplication);
                                })
                                .toList();
                return ApiResponseDTO.success(
                                "Verified claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedClaim() {
                List<ClaimApplicationVerificationResponseDto> verificationResponses = claimApplicationVerificationService
                                .getVerifiedClaim().getData();
                if (verificationResponses == null || verificationResponses.isEmpty()) {
                        return ApiResponseDTO.success(
                                        "No verified claim applications found",
                                        List.of());
                }
                List<GeneralClaimResponse> responses = verificationResponses.stream()
                                .map(verificationResponse -> {
                                        ClaimApplication claimApplication = claimApplicationService
                                                        .getById(verificationResponse.getClaimApplicationId());
                                        return buildGeneralClaimResponse(claimApplication);
                                })
                                .toList();
                return ApiResponseDTO.success(
                                "Verified claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedClaimButRejectedClaim() {
                ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> claimApplications = claimApplicationVerificationService
                                .getVerifiedClaimButRejectedClaim();
                List<GeneralClaimResponse> responses = claimApplications.getData().stream()
                                .map(verificationResponse -> {
                                        ClaimApplication claimApplication = claimApplicationService
                                                        .getById(verificationResponse.getClaimApplicationId());
                                        return buildGeneralClaimResponse(claimApplication);
                                })
                                .toList();
                return ApiResponseDTO.success(
                                "Verified claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional
        public ApiResponseDTO<List<GeneralClaimResponse>> verifiedClaimApplicationClaimedBy(String applicationNumber,
                        String claimedBy) {
                if (applicationNumber == null || applicationNumber.isBlank()) {
                        throw ClaimException.badRequest("Application number is required");
                }
                if (claimedBy == null || claimedBy.isBlank()) {
                        throw ClaimException.badRequest("Claimed by is required");
                }

                List<ClaimApplicationVerificationResponseDto> verificationResponses = claimApplicationVerificationService
                                .verifiedClaimApplicationClaimedBy(applicationNumber, claimedBy).getData();

                List<GeneralClaimResponse> responses = verificationResponses.stream()
                                .map(verificationResponse -> {
                                        ClaimApplication claimApplication = claimApplicationService
                                                        .getById(verificationResponse.getClaimApplicationId());
                                        return buildGeneralClaimResponse(claimApplication);
                                })
                                .toList();
                return ApiResponseDTO.success(
                                "Verified claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional
        public ApiResponseDTO<List<GeneralClaimResponse>> verifiedClaimApplicationUnClaimedBy(String applicationNumber,
                        String unClaimedBy) {
                if (applicationNumber == null || applicationNumber.isBlank()) {
                        throw ClaimException.badRequest("Application number is required");
                }
                if (unClaimedBy == null || unClaimedBy.isBlank()) {
                        throw ClaimException.badRequest("Unclaimed by is required");
                }

                List<ClaimApplicationVerificationResponseDto> verificationResponses = claimApplicationVerificationService
                                .verifiedClaimApplicationUnClaimedBy(applicationNumber, unClaimedBy).getData();

                List<GeneralClaimResponse> responses = verificationResponses.stream()
                                .map(verificationResponse -> {
                                        ClaimApplication claimApplication = claimApplicationService
                                                        .getById(verificationResponse.getClaimApplicationId());
                                        return buildGeneralClaimResponse(claimApplication);
                                })
                                .toList();
                return ApiResponseDTO.success(
                                "Verified claim applications fetched successfully",
                                responses);
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimResponse> rejectedClaimApplication(String applicationNumber, String rejectedBy, String remarks) {
                if (applicationNumber == null || applicationNumber.isBlank()) {
                        throw ClaimException.badRequest("Application number is required");
                }

                ClaimApplicationVerificationResponseDto verificationResponse = claimApplicationVerificationService
                                .rejectedClaimApplication(applicationNumber, rejectedBy, remarks).getData();

                ClaimApplication claimApplication = claimApplicationService
                                .getById(verificationResponse.getClaimApplicationId());
                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);

                return ApiResponseDTO.success(
                                "Claim application rejected successfully",
                                response);
        }

        public ApiResponseDTO<List<GeneralClaimResponse>> getLegalRecoveryWithUserCode(String userCode) {
                if (userCode == null || userCode.isBlank()) {
                        throw ClaimException.badRequest("User code is required");
                }

                List<ClaimApplication> claimApplications = claimApplicationService
                                .getLegalRecoveryWithUserCode(userCode);

                List<GeneralClaimResponse> responses = claimApplications.stream()
                                .map(this::buildGeneralClaimResponse)
                                .toList();

                return ApiResponseDTO.success(
                                "Claim applications fetched successfully",
                                responses);
        }
}