package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimBankResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimRuleEvaluationListDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;
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
import com.claim.claim_processing.application.service.application.ClaimLedgerService;
import com.claim.claim_processing.application.service.claimDetail.ClaimDetailService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import com.claim.claim_processing.document.service.DocumentMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.rule.pension.dto.PensionDetailRequestDto;
import com.claim.claim_processing.rule.pension.dto.PensionDetailResponseDTO;
import com.claim.claim_processing.rule.pension.service.PensionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
        private final ClaimLedgerService claimLedgerService;

        private final ReserveAccountService reserveAccountService;
        private final PensionService pensionService;
        private final DocumentMasterService documentMasterService;

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
        @Transactional
        public ApiResponseDTO<GeneralClaimDetailResponse> approve(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                claimApplication.setStatus(getStatus(6L));
                claimApplicationRepository.save(claimApplication);
                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(new ClaimApplicationApproval());

                // Apply all updates from request
                applyRequest(approval, claimApplication, request);

                // Validate required fields for approval
                if (request.getApprovedBy() == null || request.getApprovedBy().isBlank()) {
                        throw ClaimException.badRequest("Approved By is required");
                }

                // Set approval specific fields
                approval.setApprovedBy(request.getApprovedBy());

                approval.setRoleId(request.getRoleId() != null ? request.getRoleId() : 27L); // Default to 1L if not
                                                                                             // provided

                approval.setApprovedAt(new Timestamp(System.currentTimeMillis()));
                approval.setUpdatedBy(request.getApprovedBy());
                approval.setClaimApplication(claimApplication);
                // Save approval
                ClaimApplicationApproval saved = approvalRepository.saveAndFlush(approval);

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
                claimApplicationRepository.findById(claimApplication.getId()).orElse(null);
                // Build response
                GeneralClaimResponse response = null;
                GeneralClaimDetailResponse claimDetailResponse = null;
                if (claimApplication.getIsSpecialCase() == null) {
                        response = buildGeneralClaimResponse(claimApplication);

                        // Create claim detail
                        claimDetailResponse = claimDetailService.create(response);

                        AccountingEventResponseDto accountingEventResponse = claimLedgerService.createLedgerEntries(
                                        claimDetailResponse,
                                        request.getApprovedBy());
                        claimDetailResponse.setAccountingEventDetail(accountingEventResponse);
                        if (claimApplication.getIsSpecialCase() == null) {
                                saveToReserveAccount(claimDetailResponse, request.getApprovedBy());
                                saveToPensionDetail(claimDetailResponse, request.getApprovedBy());
                        }
                }

                documentMasterService.transferDocumentsForApproval(claimApplication.getApplicationNumber(), claimApplication.getApplicationNumber(),
                                "APPROVER", request.getApprovedBy());

                return ApiResponseDTO.success(claimDetailResponse);

        }

        private void saveToReserveAccount(GeneralClaimDetailResponse claimDetailResponse, String createdBy) {
                try {
                        // Get forfeited components
                        List<ClaimForfeitedComponentResponseDto> forfeitedComponents = claimDetailResponse
                                        .getForfeitedComponents();

                        // Calculate total forfeited amount
                        BigDecimal totalForfeitedAmount = BigDecimal.ZERO;
                        StringBuilder componentCodes = new StringBuilder();

                        if (forfeitedComponents != null && !forfeitedComponents.isEmpty()) {
                                for (ClaimForfeitedComponentResponseDto forfeited : forfeitedComponents) {
                                        BigDecimal amount = forfeited.getAmount() != null ? forfeited.getAmount()
                                                        : BigDecimal.ZERO;
                                        if (amount.compareTo(BigDecimal.ZERO) > 0) {
                                                totalForfeitedAmount = totalForfeitedAmount.add(amount);
                                                if (componentCodes.length() > 0) {
                                                        componentCodes.append(",");
                                                }
                                                componentCodes.append(forfeited.getComponentCode());
                                        }
                                }
                        }

                        // Get lapse amount from accounting event
                        BigDecimal lapseAmount = BigDecimal.ZERO;
                        AccountingEventResponseDto accountingEvent = claimDetailResponse.getAccountingEventDetail();
                        if (accountingEvent != null) {
                                // Find LAPSE entry in ledger entries
                                for (LedgerEntryResponseDto entry : accountingEvent.getLedgerEntries()) {
                                        if ("LAPSE".equals(entry.getComponentCode())) {
                                                lapseAmount = entry.getAmount() != null ? entry.getAmount()
                                                                : BigDecimal.ZERO;
                                                break;
                                        }
                                }
                        }

                        BigDecimal totalReserveAmount = totalForfeitedAmount.add(lapseAmount);

                        if (totalReserveAmount.compareTo(BigDecimal.ZERO) <= 0) {
                                log.info("No reserve amount to save. Forfeited: {}, Lapse: {}", totalForfeitedAmount,
                                                lapseAmount);
                                return;
                        }

                        log.info("Saving to Reserve Account - Forfeited: {}, Lapse: {}, Total: {}",
                                        totalForfeitedAmount, lapseAmount, totalReserveAmount);

                        String agencyCategoryId = claimDetailResponse.getMemberCategoryId();

                        ReserveAccountRequestDto reserveRequest = ReserveAccountRequestDto.builder()
                                        .memberCode(claimDetailResponse.getMemberCode())
                                        .nppfNumber(claimDetailResponse.getNppfNumber())
                                        .identityNumber(claimDetailResponse.getNppfNumber())
                                        .agencyCategoryId(agencyCategoryId)
                                        .agencyCode(claimDetailResponse.getAgencyCode())
                                        .reserveType("LAPSE_FUND")
                                        .totalAmount(totalReserveAmount)
                                        .forfeitedAmount(totalForfeitedAmount)
                                        .componentCodes(componentCodes.toString())
                                        .build();

                        ApiResponseDTO<ReserveAccountResponseDto> response = reserveAccountService
                                        .create(reserveRequest);

                        if (response != null && response.getData() != null) {
                                log.info("Reserve account created successfully for NPPF: {}, Amount: {}",
                                                claimDetailResponse.getNppfNumber(), totalReserveAmount);
                        } else {
                                log.error("Failed to create reserve account: {}",
                                                response != null ? response.getMessage() : "No response");
                        }

                } catch (Exception e) {
                        log.error("Error saving to reserve account: {}", e.getMessage(), e);
                        // Don't throw - reserve account save failure shouldn't rollback the transaction
                }
        }

        private void saveToPensionDetail(GeneralClaimDetailResponse claimDetailResponse, String createdBy) {
                try {
                        // Get pension refund amount from accounting event
                        BigDecimal pensionRefund = BigDecimal.ZERO;
                        AccountingEventResponseDto accountingEvent = claimDetailResponse.getAccountingEventDetail();
                        if (accountingEvent != null) {
                                for (LedgerEntryResponseDto entry : accountingEvent.getLedgerEntries()) {
                                        if ("PENSION_REFUND".equals(entry.getComponentCode())) {
                                                pensionRefund = entry.getAmount() != null ? entry.getAmount()
                                                                : BigDecimal.ZERO;
                                                break;
                                        }
                                }
                        }

                        if (pensionRefund.compareTo(BigDecimal.ZERO) <= 0) {
                                log.info("No pension refund amount to save. Pension Refund: {}", pensionRefund);
                                return;
                        }

                        log.info("Saving Pension Detail - Pension Refund: {}", pensionRefund);

                        // Get monthly pension amount from calculation components
                        BigDecimal monthlyPension = BigDecimal.ZERO;
                        ClaimCalculationSummaryResponseDto summary = claimDetailResponse.getCalculationSummary();
                        if (summary != null && summary.getRuleEvaluations() != null) {
                                for (ClaimRuleEvaluationListDto ruleEval : summary.getRuleEvaluations()) {
                                        if (ruleEval.getComponents() != null) {
                                                for (ClaimCalculationComponentDto component : ruleEval
                                                                .getComponents()) {
                                                        if ("P_MONTHLY".equals(component.getComponentCode()) ||
                                                                        "MONTHLY_PENSION".equals(
                                                                                        component.getComponentCode())) {
                                                                monthlyPension = component.getAmount() != null
                                                                                ? component.getAmount()
                                                                                : BigDecimal.ZERO;
                                                                break;
                                                        }
                                                }
                                        }
                                }
                        }

                        // Get total contribution months from calculation summary
                        Integer totalMonths = 0;
                        Integer totalYears = 0;
                        if (summary != null && summary.getTotalContributionMonth() != null) {
                                totalMonths = summary.getTotalContributionMonth();
                                totalYears = totalMonths / 12;
                        }

                        // Get pension start date from normal claim details
                        LocalDateTime pensionStartDate = null;
                        if (claimDetailResponse.getNormalClaimDetails() != null) {
                                LocalDate pensionJoinDate = claimDetailResponse.getNormalClaimDetails()
                                                .getPensionJoiningDate();
                                if (pensionJoinDate != null) {
                                        pensionStartDate = pensionJoinDate.atStartOfDay();
                                }
                        }
                        ClaimBankResponseDto bankDetail = claimDetailResponse.getBankDetails().stream()
                                        .filter(bank -> 3 == bank.getClaimantTypeId())
                                        .findFirst()
                                        .orElse(null);

                        PensionDetailRequestDto requestForPesion = PensionDetailRequestDto
                                        .builder()
                                        .nppfNumber(claimDetailResponse.getNppfNumber())
                                        .memberIdentityNumber(claimDetailResponse.getIdentityNumber())
                                        .agencyCode(claimDetailResponse.getAgencyCode())
                                        .currencyCode(claimDetailResponse.getCurrencyCode())
                                        .pensionType("MONTHLY_PENSION")
                                        .totalPensionFund(pensionRefund)
                                        .totalContributionMonths(totalMonths)
                                        .totalContributionYears(totalYears)
                                        .pensionStartDate(pensionStartDate != null ? pensionStartDate.toLocalDate()
                                                        : null)
                                        .bankTypeId(bankDetail != null ? bankDetail.getBankTypeId() : null)
                                        .bankName(bankDetail != null ? bankDetail.getBankTypeName() : null)
                                        .bankAccountNumber(bankDetail != null ? bankDetail.getAccountNumber() : null)
                                        .accountHolderName(
                                                        bankDetail != null ? bankDetail.getAccountHolderName() : null)
                                        .ifscCode(bankDetail != null ? bankDetail.getIfscOrRoutingCode() : null)
                                        .createdBy(createdBy)
                                        .build();
                        // Call pension service to create or update
                        PensionDetailResponseDTO pensionResponse = pensionService
                                        .createOrUpdatePensionDetail(requestForPesion);

                        log.info("Pension detail saved successfully for NPPF: {}, ID: {}",
                                        claimDetailResponse.getNppfNumber(),
                                        pensionResponse != null ? pensionResponse.getPensionDetailId() : "null");

                } catch (Exception e) {
                        log.error("Error saving pension detail: {}", e.getMessage(), e);
                        // Don't throw - pension save failure shouldn't rollback the transaction
                }
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
        @Transactional
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> verifiedClaimActionClaimedBy(
                        String applicationNumber, String claimedBy) {
                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(new ClaimApplicationApproval());
                claimApplication.setStatus(getStatus(61L));
                claimApplication.setClaimedBy(claimedBy);
                claimApplication.setUpdatedBy(claimedBy);
                claimApplicationRepository.saveAndFlush(claimApplication);
                approval.setClaimedBy(claimedBy);
                approval.setApprovalStatus(getStatus(61L));
                approval.setUpdatedBy(claimedBy);
                approval.setClaimApplication(claimApplication);
                approvalRepository.saveAndFlush(approval);
                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService
                                .getByApplicationId(claimApplication.getId());
                if (workflowResponse == null || workflowResponse.isEmpty()) {
                        ClaimApplicationWorkflowResponseDto workFlow = workflowResponse.get(0);
                        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                        .builder()
                                        .fromStageId(workFlow.getFromStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .fromStatusId(workFlow.getFromStatusId())
                                        .toStatusId(61L)
                                        .reason(workFlow.getReason())
                                        .actionBy(workFlow.getActionBy())
                                        .build();
                        workflowService
                                        .create(claimApplication, workflowRequest);
                }

                return ApiResponseDTO.success(approvalMapper.toResponse(approval));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> verifiedClaimActionUnClaimedBy(
                        String applicationNumber, String unClaimedBy) {
                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Approval record not found for claim application number: "
                                                                + applicationNumber));
                claimApplication.setStatus(getStatus(62L));
                claimApplication.setClaimedBy(unClaimedBy);
                claimApplication.setUpdatedBy(unClaimedBy);
                claimApplicationRepository.saveAndFlush(claimApplication);
                approval.setClaimedBy(unClaimedBy);
                approval.setApprovalStatus(getStatus(62L));
                approval.setUpdatedBy(unClaimedBy);
                approvalRepository.saveAndFlush(approval);

                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService
                                .getByApplicationId(claimApplication.getId());
                if (workflowResponse == null || workflowResponse.isEmpty()) {
                        ClaimApplicationWorkflowResponseDto workFlow = workflowResponse.get(0);
                        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                        .builder()
                                        .fromStageId(workFlow.getFromStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .fromStatusId(workFlow.getFromStatusId())
                                        .toStatusId(62L)
                                        .reason(workFlow.getReason())
                                        .actionBy(workFlow.getActionBy())
                                        .build();
                        workflowService
                                        .create(claimApplication, workflowRequest);
                }

                return ApiResponseDTO.success(approvalMapper.toResponse(approval));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> verifiedClaimActionRejectedByApprover(
                        String applicationNumber, String rejectedBy, String rejectedRemarks) {
                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Approval record not found for claim application number: "
                                                                + applicationNumber));
                claimApplication.setStatus(getStatus(63L));
                claimApplication.setClaimedBy(rejectedBy);
                claimApplication.setUpdatedBy(rejectedBy);
                claimApplicationRepository.saveAndFlush(claimApplication);
                approval.setClaimedBy(rejectedBy);
                approval.setApprovalStatus(getStatus(63L));
                approval.setUpdatedBy(rejectedBy);
                approval.setRemarks(rejectedRemarks);
                approvalRepository.saveAndFlush(approval);

                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService
                                .getByApplicationId(claimApplication.getId());
                if (workflowResponse != null && !workflowResponse.isEmpty()) {
                        ClaimApplicationWorkflowResponseDto workFlow = workflowResponse.get(0);
                        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                        .builder()
                                        .fromStageId(4L)
                                        .toStageId(3L)
                                        .fromStatusId(workFlow.getToStatusId())
                                        .toStatusId(63L)
                                        .reason(workFlow.getReason())
                                        .actionId(5L)
                                        .actionBy(workFlow.getActionBy())
                                        .build();
                        workflowService
                                        .create(claimApplication, workflowRequest);
                }
                return ApiResponseDTO.success(approvalMapper.toResponse(approval));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationApprovalResponseDto>> getVerifiedClaimAndClaimedBy(
                        String claimedBy) {
                List<ClaimApplicationApproval> approvals = approvalRepository
                                .findByApprovalStatus_StatusIdAndClaimedBy(61L, claimedBy);
                return ApiResponseDTO.success(approvals.stream()
                                .map(approvalMapper::toResponse)
                                .toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> getByApplicationNumber(
                        String applicationNumber) {

                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(null);
                if (approval == null) {
                        return ApiResponseDTO.success(null);
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
                BigDecimal finalNetPayableAmount = null;
                if (claimApplication.getIsSpecialCase().toString().equalsIgnoreCase("N")) {
                        ClaimApplicationCalculationSummary calculationSummary = claimApplication
                                        .getCalculationSummary() != null ? claimApplication.getCalculationSummary()
                                                        : null;

                        if (calculationSummary != null) {
                                // Set final payable amount from calculation summary
                                approval.setApprovedAmount(calculationSummary.getFinalPayableAmount() != null
                                                ? calculationSummary.getFinalPayableAmount()
                                                : BigDecimal.ZERO);

                                // Calculate PF and Pension totals from rule evaluations
                                BigDecimal totalPfAmount = calculationSummary.getRuleEvaluations() != null
                                                ? calculateTotalPfAmount(calculationSummary.getRuleEvaluations())
                                                : BigDecimal.ZERO;
                                BigDecimal totalPensionAmount = calculationSummary.getRuleEvaluations() != null
                                                ? calculateTotalPensionAmount(calculationSummary.getRuleEvaluations())
                                                : BigDecimal.ZERO;

                                // Set the calculated amounts
                                approval.setApprovedPfAmount(totalPfAmount != null ? totalPfAmount : BigDecimal.ZERO);
                                approval.setApprovedPensionAmount(
                                                totalPensionAmount != null ? totalPensionAmount : BigDecimal.ZERO);

                                // Set other amounts (adjust based on your actual fields)
                                approval.setApprovedWithdrawalAmount(
                                                claimApplication.getPartialWithdrawalDetail() != null
                                                                ? claimApplication.getPartialWithdrawalDetail()
                                                                                .getRequestedWithdrawalAmount()
                                                                : BigDecimal.ZERO);
                                approval.setApprovedDeductionAmount(claimApplication.getDeductionDetail() != null
                                                ? claimApplication.getDeductionDetail().getDeductedAmount()
                                                : BigDecimal.ZERO); // Calculate if needed
                                finalNetPayableAmount = calculationSummary.getFinalPayableAmount() != null
                                                ? calculationSummary.getFinalPayableAmount()
                                                : BigDecimal.ZERO;
                        }

                } else {
                        ClaimSpecialCaseApplication specialCaseApplication = claimApplication
                                        .getClaimSpecialCaseApplication() != null
                                                        ? claimApplication.getClaimSpecialCaseApplication()
                                                        : null;
                        if (specialCaseApplication != null) {
                                // Set final payable amount from calculation summary
                                approval.setApprovedAmount(specialCaseApplication.getTotalPensionAmount() != null
                                                ? specialCaseApplication.getTotalPensionAmount()
                                                : specialCaseApplication.getTotalForfeitedAmount() != null
                                                                ? specialCaseApplication.getTotalForfeitedAmount()
                                                                : specialCaseApplication
                                                                                .getTotalForfeitedAmount() != null
                                                                                                ? specialCaseApplication
                                                                                                                .getTotalForfeitedAmount()
                                                                                                : BigDecimal.ZERO);

                                // Calculate PF and Pension totals from rule evaluations
                                BigDecimal totalPfAmount = BigDecimal.ZERO;
                                BigDecimal totalPensionAmount = specialCaseApplication != null
                                                ? specialCaseApplication.getTotalPensionAmount()
                                                : BigDecimal.ZERO;

                                // Set the calculated amounts
                                approval.setApprovedPfAmount(totalPfAmount != null ? totalPfAmount : BigDecimal.ZERO);
                                approval.setApprovedPensionAmount(
                                                totalPensionAmount != null ? totalPensionAmount : BigDecimal.ZERO);

                                // Set other amounts (adjust based on your actual fields)
                                approval.setApprovedWithdrawalAmount(
                                                claimApplication.getPartialWithdrawalDetail() != null
                                                                ? claimApplication.getPartialWithdrawalDetail()
                                                                                .getRequestedWithdrawalAmount()
                                                                : BigDecimal.ZERO);
                                approval.setApprovedRefundAmount(specialCaseApplication != null
                                                ? specialCaseApplication.getTotalForfeitedAmount() != null
                                                                ? specialCaseApplication.getTotalForfeitedAmount()
                                                                : BigDecimal.ZERO
                                                : BigDecimal.ZERO); // Calculate if needed
                                approval.setApprovedDeductionAmount(BigDecimal.ZERO); // Calculate if needed
                                approval.setFinalNetPayableAmount(totalPensionAmount != null ? totalPensionAmount
                                                : specialCaseApplication.getTotalForfeitedAmount() != null
                                                                ? specialCaseApplication.getTotalForfeitedAmount()
                                                                : BigDecimal.ZERO);
                                finalNetPayableAmount = totalPensionAmount != null ? totalPensionAmount
                                                : specialCaseApplication.getTotalForfeitedAmount() != null
                                                                ? specialCaseApplication.getTotalForfeitedAmount()
                                                                : BigDecimal.ZERO;
                        }
                }

                approval.setFinalNetPayableAmount(finalNetPayableAmount);

                if (request.getRequiresManualReview() != null) {
                        approval.setRequiresManualReview(request.getRequiresManualReview());
                }

                if (request.getIsActive() != null) {
                        approval.setIsActive(request.getIsActive());
                }

                approval.setRemarks(request.getApproverRemarks());
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

                StatusMaster status = getStatus(statusId);

                return "APPROVED".equalsIgnoreCase(status.getStatusName().toUpperCase());
        }

        private StatusMaster getStatus(Long id) {
                if (id == null) {
                        throw ClaimException.badRequest("Approval status is required");
                }

                StatusMaster status = statusRepository.findById(id)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Approval status not found with id: " + id));

                return status;
        }

        private ClaimApplication getClaimApplication(String applicationNumber) {
                return claimApplicationRepository.findByApplicationNumber(applicationNumber)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Claim application not found with application number: "
                                                                + applicationNumber));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<GeneralClaimDetailResponse> markAsSpecial(
                        String applicationNumber, String updatedBy, String remarks) {
                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElseThrow(() -> ClaimException.notFound(
                                                "Approval record not found for claim application number: "
                                                                + applicationNumber));
                claimApplication.setStatus(getStatus(81L));
                claimApplication.setUpdatedBy(updatedBy);
                claimApplicationRepository.saveAndFlush(claimApplication);
                approval.setApprovalStatus(getStatus(81L));
                approval.setUpdatedBy(updatedBy);
                approval.setRemarks(remarks);
                approvalRepository.saveAndFlush(approval);

                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService
                                .getByApplicationId(claimApplication.getId());
                if (workflowResponse != null && !workflowResponse.isEmpty()) {
                        ClaimApplicationWorkflowResponseDto workFlow = workflowResponse.get(0);
                        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                        .builder()
                                        .fromStageId(4L)
                                        .toStageId(3L)
                                        .fromStatusId(workFlow.getToStatusId())
                                        .toStatusId(81L)
                                        .reason(workFlow.getReason())
                                        .actionId(5L)
                                        .actionBy(workFlow.getActionBy())
                                        .build();
                        workflowService
                                        .create(claimApplication, workflowRequest);
                }
                claimApplicationRepository.findById(claimApplication.getId()).orElse(null);
                // Build response
                GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication);

                GeneralClaimDetailResponse claimDetailResponse = claimDetailService.create(response);

                return ApiResponseDTO.success(claimDetailResponse);
        }
}