package com.claim.claim_processing.application.service.application.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationOtherRequestDto;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimPatchRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
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
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

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
                ClaimApplicationDeductionDetail deductionEntity = null;
                List<ClaimApplicationForfeitedComponent> forfeitedComponents = new ArrayList<>();

                ClaimCalculationResponseDTO calculationResponse = null;

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

                        ClaimApplicationOtherRequestDto otherRequest = request.getClaimApplicationOther();

                        ClaimInitialPreviewRequest benefitRequest = ClaimInitialPreviewRequest.builder()
                                        .cessationDate(otherRequest.getCessationDate())
                                        .cessationTypeId(otherRequest.getCessationTypeId())
                                        .claimTypeId(claimApplication.getClaimType().getId())
                                        .nppfNumber(claimApplication.getNppfNumber())
                                        .isSpecialCase(claimApplication.getIsSpecialCase() == ActivityEnum.Y)
                                        .reasonTypeId(otherRequest.getReasonTypeId())
                                        .build();

                        calculationResponse = benefitCalculationService
                                        .calculateBenefit(benefitRequest)
                                        .getData();

                        calculationEntity = claimApplicationCalculationService.create(
                                        claimApplication,
                                        calculationResponse,
                                        otherRequest.getFinalPayableAmount());

                        deductionEntity = claimApplicationDeductionDetailService.saveCalculationDeductions(
                                        claimApplication,
                                        calculationResponse,
                                        claimApplication.getCreatedBy());
                        if (calculationResponse.getForfeitedComponents() != null
                                        && !calculationResponse.getForfeitedComponents().isEmpty()) {
                                forfeitedComponents = claimApplicationForfeitedComponentService.saveForfeitedComponents(
                                                claimApplication,
                                                calculationResponse,
                                                claimApplication.getCreatedBy());
                        }
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

                response.setDeductionDetail(
                                claimApplicationDeductionResponseMapper.toResponse(deductionEntity));

                response.setCalculationSummary(
                                claimApplicationCalculationSummaryResponseMapper.toResponse(calculationEntity));

                response.setNormalClaimDetails(
                                normalClaimResponseMapper.toResponse(normalClaimDetail));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper.toResponse(partialWithdrawalDetail));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper.toResponse(beneficiarySettlementDetail));

                response.setForfeitedComponents(
                                forfeitedComponents.stream()
                                                .map(claimApplicationForfeitedComponentResponseMapper::toResponse)
                                                .toList());
                response.setLegalRecoveryDetail(legalRecoveryResponseMapper.toResponse(legalRecoveryDetail));
                return ApiResponseDTO.created(response);
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

                if (request.getClaimApplicationCalculation() != null) {
                        calculationSummary = claimApplicationCalculationService.patch(
                                        request.getClaimApplicationCalculation());
                }

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
                                                claimApplication.getNormalClaimDetail()));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper.toResponse(
                                                claimApplication.getPartialWithdrawalDetail()));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper.toResponse(
                                                claimApplication.getBeneficiarySettlementDetail()));

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
                                claimApplicationWorkflowService.getByApplicationId(claimApplication.getId()));

                response.setVerificationDetail(
                                claimApplicationVerificationService.getByClaimApplicationId(claimApplication.getId()));

                response.setApprovalDetail(
                                claimApplicationApprovalService.getByClaimApplicationId(claimApplication.getId()));

                return response;
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<GeneralClaimResponse> findByApplicationId(String applicationId) {

                if (applicationId == null) {
                        throw ClaimException.badRequest("Application id is required");
                }

                ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationId);

                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);

                return ApiResponseDTO.success(
                                "Claim application fetched successfully",
                                response);
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
                                "Claim application fetched successfully",
                                responses);
        }

}