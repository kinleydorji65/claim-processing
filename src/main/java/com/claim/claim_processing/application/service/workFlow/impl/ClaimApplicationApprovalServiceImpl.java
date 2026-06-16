package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationRuleEvaluation;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;
import com.claim.claim_processing.application.mapper.application.GeneralClaimResponseBuilderMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.BeneficiarySettlementResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationBankResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationCalculationSummaryResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationDeductionResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationForfeitedComponentResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.NormalClaimResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.PartialWithdrawalResponseMapper;
import com.claim.claim_processing.application.mapper.workFlow.ClaimApplicationApprovalMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.workFlow.ClaimApplicationApprovalRepository;
import com.claim.claim_processing.application.service.claimDetail.ClaimDetailService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimApplicationApprovalServiceImpl implements ClaimApplicationApprovalService {

        private final ClaimApplicationApprovalRepository approvalRepository;
        private final ClaimApplicationRepository claimApplicationRepository;
        private final ClaimApplicationBankResponseMapper claimApplicationBankResponseMapper;
        private final GeneralClaimResponseBuilderMapper generalClaimResponseBuilderMapper;
        private final ClaimApplicationDeductionResponseMapper claimApplicationDeductionResponseMapper;
        private final ClaimApplicationCalculationSummaryResponseMapper claimApplicationCalculationSummaryResponseMapper;
        private final NormalClaimResponseMapper normalClaimResponseMapper;
        private final PartialWithdrawalResponseMapper partialWithdrawalResponseMapper;
        private final BeneficiarySettlementResponseMapper beneficiarySettlementResponseMapper;
        private final ClaimApplicationForfeitedComponentResponseMapper claimApplicationForfeitedComponentResponseMapper;

        private final StatusMasterRepository statusRepository;
        private final ClaimApplicationWorkflowService workflowService;

        private final ClaimApplicationApprovalMapper approvalMapper;
        private final ClaimDetailService claimDetailService;

        @Override
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> patch(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(claimApplication.getApplicationNumber())
                                .orElseGet(() -> ClaimApplicationApproval.builder()
                                                .claimApplication(claimApplication)
                                                .isActive(ActivityEnum.Y)
                                                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy()
                                                                : "sys")
                                                .build());

                applyRequest(approval, claimApplication, request);

                approval.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "sys");

                ClaimApplicationApproval saved = approvalRepository.save(approval);

                return ApiResponseDTO.success(approvalMapper.toResponse(saved));
        }

        @Override
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> approve(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Approval record not found for claim application number: "
                                                                + applicationNumber));

                // Apply all updates from request
                applyRequest(approval, claimApplication, request);

                // Validate required fields for approval
                if (request.getApprovedBy() == null || request.getApprovedBy().isBlank()) {
                        throw ClaimException.badRequest("Approved By is required");
                }

                // Set approval specific fields
                approval.setApprovedBy(request.getApprovedBy());

                // FIXED: Set proper role instead of hardcoded "1l"
                String approvedByRole = "APPROVER"; // Implement this method
                approval.setApprovedByRole(approvedByRole);

                approval.setApprovedAt(new Timestamp(System.currentTimeMillis()));
                approval.setUpdatedBy(request.getApprovedBy());

                // Save approval
                ClaimApplicationApproval saved = approvalRepository.save(approval);

                // Create workflow entry
                Map<String, Long> workflowStage = resolveFromStageAndToStageAndAction(claimApplication.getId(),
                                request);

                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                                .fromStageId(workflowStage.get("fromStage"))
                                .toStageId(workflowStage.get("toStage"))
                                .fromStatusId(workflowStage.get("fromStatus"))
                                .toStatusId(workflowStage.get("toStatus"))
                                .actionId(request.getActionId())
                                .reason(request.getApproverRemarks())
                                .actionBy(request.getApprovedBy())
                                .build();

                workflowService.create(claimApplication, workflowRequest);

                // Build response
                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);

                // Create claim detail
                claimDetailService.create(response);

                return ApiResponseDTO.success(approvalMapper.toResponse(saved));
        }

        // Helper method to build response
        private GeneralClaimResponse buildGeneralClaimResponse(ClaimApplication claimApplication) {
                GeneralClaimResponse response = generalClaimResponseBuilderMapper.toResponse(claimApplication);

                response.setBankDetails(
                                claimApplication.getBankDetails().stream()
                                                .map(claimApplicationBankResponseMapper::toResponse)
                                                .toList());

                response.setDeductionDetail(
                                claimApplicationDeductionResponseMapper
                                                .toResponse(claimApplication.getDeductionDetail()));

                response.setCalculationSummary(
                                claimApplicationCalculationSummaryResponseMapper
                                                .toResponse(claimApplication.getCalculationSummary()));

                response.setNormalClaimDetails(
                                normalClaimResponseMapper.toResponse(claimApplication.getNormalClaimDetail()));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper
                                                .toResponse(claimApplication.getPartialWithdrawalDetail()));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper
                                                .toResponse(claimApplication.getBeneficiarySettlementDetail()));

                response.setForfeitedComponents(
                                claimApplicationForfeitedComponentResponseMapper
                                                .toResponseList(claimApplication.getForfeitedComponents()));

                return response;
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> getByApplicationNumber(
                        String applicationNumber) {

                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(null);
                if (approval == null) {
                        return null;
                }
                return ApiResponseDTO.success(approvalMapper.toResponse(approval));
        }

        private void applyRequest(
                        ClaimApplicationApproval approval,
                        ClaimApplication claimApplication,
                        ClaimApplicationApprovalRequestDto request) {

                if (request.getApprovalStatusId() != null) {
                        approval.setApprovalStatus(
                                        statusRepository.findById(request.getApprovalStatusId())
                                                        .orElseThrow(() -> ClaimException.notFound(
                                                                        "Approval status not found with id: " + request
                                                                                        .getApprovalStatusId())));
                }

                // Get calculation summary
                ClaimApplicationCalculationSummary calculationSummary = claimApplication.getCalculationSummary();

                if (calculationSummary != null) {
                        // Set final payable amount from calculation summary
                        approval.setApprovedAmount(calculationSummary.getFinalPayableAmount());

                        // Calculate PF and Pension totals from rule evaluations
                        BigDecimal totalPfAmount = calculateTotalPfAmount(calculationSummary.getRuleEvaluations());
                        BigDecimal totalPensionAmount = calculateTotalPensionAmount(
                                        calculationSummary.getRuleEvaluations());

                        // Set the calculated amounts
                        approval.setApprovedPfAmount(totalPfAmount);
                        approval.setApprovedPensionAmount(totalPensionAmount);

                        // Set other amounts (adjust based on your actual fields)
                        approval.setApprovedWithdrawalAmount(claimApplication.getPartialWithdrawalDetail() != null
                                        ? claimApplication.getPartialWithdrawalDetail().getRequestedWithdrawalAmount()
                                        : BigDecimal.ZERO);
                        approval.setApprovedRefundAmount(BigDecimal.ZERO); // Calculate if needed
                        approval.setApprovedDeductionAmount(claimApplication.getDeductionDetail() != null
                                        ? claimApplication.getDeductionDetail().getDeductedAmount()
                                        : BigDecimal.ZERO); // Calculate if needed
                        approval.setFinalNetPayableAmount(totalPensionAmount);
                }

                approval.setFinalNetPayableAmount(calculationSummary.getFinalPayableAmount());

                if (request.getRequiresManualReview() != null) {
                        approval.setRequiresManualReview(request.getRequiresManualReview());
                }

                if (request.getIsActive() != null) {
                        approval.setIsActive(request.getIsActive());
                }

                approval.setApproverRemarks(request.getApproverRemarks());
        }

        // Helper methods
        private BigDecimal calculateTotalPfAmount(List<ClaimApplicationRuleEvaluation> ruleEvaluations) {
                if (ruleEvaluations == null || ruleEvaluations.isEmpty()) {
                        return BigDecimal.ZERO;
                }

                return ruleEvaluations.stream()
                                .filter(Objects::nonNull)
                                .flatMap(ruleEvaluation -> ruleEvaluation.getComponents().stream())
                                .filter(Objects::nonNull)
                                .filter(component -> component.getComponentMaster() != null)
                                .filter(component -> component.getComponentMaster().getCode() != null)
                                .filter(component -> component.getComponentMaster().getCode().startsWith("PF_"))
                                .map(ClaimApplicationCalculationComponent::getAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private BigDecimal calculateTotalPensionAmount(List<ClaimApplicationRuleEvaluation> ruleEvaluations) {
                if (ruleEvaluations == null || ruleEvaluations.isEmpty()) {
                        return BigDecimal.ZERO;
                }

                return ruleEvaluations.stream()
                                .filter(Objects::nonNull)
                                .flatMap(ruleEvaluation -> ruleEvaluation.getComponents().stream())
                                .filter(Objects::nonNull)
                                .filter(component -> component.getComponentMaster() != null)
                                .filter(component -> component.getComponentMaster().getCode() != null)
                                .filter(component -> component.getComponentMaster().getCode().startsWith("P_"))
                                .map(ClaimApplicationCalculationComponent::getAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private Map<String, Long> resolveFromStageAndToStageAndAction(
                        Long applicationId,
                        ClaimApplicationApprovalRequestDto request) {

                ClaimApplicationWorkflowResponseDto stageMaster = workflowService.getByApplicationId(applicationId)
                                .get(0);

                if (isApproved(request.getApprovalStatusId())) {
                        return Map.of(
                                        "fromStage", 4L,
                                        "toStage", 5L,
                                        "fromStatus", 41L,
                                        "toStatus", 6L);
                }

                return Map.of(
                                "fromStage", stageMaster.getFromStageId(),
                                "toStage", stageMaster.getToStageId(),
                                "fromStatus", 7L,
                                "toStatus", stageMaster.getFromStatusId());
        }

        private boolean isApproved(Long statusId) {

                String statusName = getStatusName(statusId);

                return "APPROVED".equalsIgnoreCase(statusName);
        }

        private String getStatusName(Long id) {
                if (id == null) {
                        throw ClaimException.badRequest("Approval status is required");
                }

                StatusMaster status = statusRepository.findById(id)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Approval status not found with id: " + id));

                return status.getStatusName();
        }

        private ClaimApplication getClaimApplication(String applicationNumber) {
                return claimApplicationRepository.findByApplicationNumber(applicationNumber)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Claim application not found with application number: "
                                                                + applicationNumber));
        }
}