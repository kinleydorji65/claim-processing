package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
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
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Map;

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
    public ClaimApplicationApprovalResponseDto patch(
            Long claimApplicationId,
            ClaimApplicationApprovalRequestDto request
    ) {

        ClaimApplication claimApplication = getClaimApplication(claimApplicationId);

        ClaimApplicationApproval approval =
                approvalRepository.findByClaimApplication_Id(claimApplicationId)
                        .orElseGet(() -> ClaimApplicationApproval.builder()
                                .claimApplication(claimApplication)
                                .isActive(ActivityEnum.Y)
                                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "sys")
                                .build()
                        );

        applyRequest(approval, request);

        approval.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "sys");

        ClaimApplicationApproval saved = approvalRepository.save(approval);

        return approvalMapper.toResponse(saved);
    }

    @Override
    public ClaimApplicationApprovalResponseDto approve(
            Long claimApplicationId,
            ClaimApplicationApprovalRequestDto request
    ) {

        ClaimApplication claimApplication = getClaimApplication(claimApplicationId);

        ClaimApplicationApproval approval =
                approvalRepository.findByClaimApplication_Id(claimApplicationId)
                        .orElseThrow(() -> ClaimException.notFound(
                                "Approval record not found for claim application id: " + claimApplicationId
                        ));

        applyRequest(approval, request);

        if (request.getApprovedBy() == null || request.getApprovedBy().isBlank()) {
            throw ClaimException.badRequest("Approved By is required");
        }

        approval.setApprovedBy(request.getApprovedBy());
        approval.setApprovedByRole(request.getApprovedByRole());
        approval.setApprovedAt(new Timestamp(System.currentTimeMillis()));
        approval.setUpdatedBy(request.getApprovedBy());

        ClaimApplicationApproval saved = approvalRepository.save(approval);

        Map<String, Long> workflowStage =
                resolveFromStageAndToStageAndAction(claimApplication.getId(), request);

        ClaimApplicationWorkflowRequestDto workflowRequest =
                ClaimApplicationWorkflowRequestDto.builder()
                        .fromStageId(workflowStage.get("fromStage"))
                        .toStageId(workflowStage.get("toStage"))
                        .fromStatusId(workflowStage.get("fromStatus"))
                        .toStatusId(workflowStage.get("toStatus"))
                        .actionId(request.getActionId())
                        .reason(request.getApproverRemarks())
                        .actionBy(request.getApprovedBy())
                        .build();

        workflowService.create(claimApplication, workflowRequest);
        GeneralClaimResponse response = generalClaimResponseBuilderMapper.toResponse(claimApplication);

        response.setBankDetails(
                        claimApplication.getBankDetails().stream()
                                        .map(claimApplicationBankResponseMapper::toResponse)
                                        .toList());

        response.setDeductionDetail(
                        claimApplicationDeductionResponseMapper.toResponse(claimApplication.getDeductionDetail()));

        response.setCalculationSummary(
                        claimApplicationCalculationSummaryResponseMapper.toResponse(claimApplication.getCalculationSummary()));

        response.setNormalClaimDetails(
                        normalClaimResponseMapper.toResponse(claimApplication.getNormalClaimDetail()));

        response.setPartialWithdrawalDetails(
                        partialWithdrawalResponseMapper.toResponse(claimApplication.getPartialWithdrawalDetail()));

        response.setBeneficiarySettlementDetails(
                        beneficiarySettlementResponseMapper.toResponse(claimApplication.getBeneficiarySettlementDetail()));

        response.setForfeitedComponents(
                                claimApplicationForfeitedComponentResponseMapper.toResponseList(claimApplication.getForfeitedComponents()));
        claimDetailService.create(response);
        return approvalMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimApplicationApprovalResponseDto getByClaimApplicationId(
            Long claimApplicationId
    ) {

        ClaimApplicationApproval approval =
                approvalRepository.findByClaimApplication_Id(claimApplicationId)
                        .orElse(null);
        if (approval == null) {
            return null;
        }
        return approvalMapper.toResponse(approval);
    }

    private void applyRequest(
            ClaimApplicationApproval approval,
            ClaimApplicationApprovalRequestDto request
    ) {

        if (request.getApprovalStatusId() != null) {
            approval.setApprovalStatus(
                    statusRepository.findById(request.getApprovalStatusId())
                            .orElseThrow(() -> ClaimException.notFound(
                                    "Approval status not found with id: " + request.getApprovalStatusId()
                            ))
            );
        }

        approval.setApprovedAmount(request.getApprovedAmount());
        approval.setApprovedPfAmount(request.getApprovedPfAmount());
        approval.setApprovedPensionAmount(request.getApprovedPensionAmount());
        approval.setApprovedWithdrawalAmount(request.getApprovedWithdrawalAmount());
        approval.setApprovedRefundAmount(request.getApprovedRefundAmount());
        approval.setApprovedDeductionAmount(request.getApprovedDeductionAmount());
        approval.setFinalNetPayableAmount(request.getFinalNetPayableAmount());

        if (request.getRequiresManualReview() != null) {
            approval.setRequiresManualReview(request.getRequiresManualReview());
        }

        if (request.getIsActive() != null) {
            approval.setIsActive(request.getIsActive());
        }

        approval.setApproverRemarks(request.getApproverRemarks());
    }

    private Map<String, Long> resolveFromStageAndToStageAndAction(
            Long applicationId,
            ClaimApplicationApprovalRequestDto request
    ) {

        ClaimApplicationWorkflowResponseDto stageMaster =
                workflowService.getByApplicationId(applicationId).get(0);

        if (isApproved(request.getApprovalStatusId())) {
            return Map.of(
                    "fromStage", 4L,
                    "toStage", 5L,
                    "fromStatus", 41L,
                    "toStatus", 6L
            );
        }

        return Map.of(
                "fromStage", stageMaster.getFromStageId(),
                "toStage", stageMaster.getToStageId(),
                "fromStatus", 7L,
                "toStatus", stageMaster.getFromStatusId()
        );
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
                        "Approval status not found with id: " + id
                ));

        return status.getStatusName();
    }

    private ClaimApplication getClaimApplication(Long claimApplicationId) {
        return claimApplicationRepository.findById(claimApplicationId)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application not found with id: " + claimApplicationId
                ));
    }
}