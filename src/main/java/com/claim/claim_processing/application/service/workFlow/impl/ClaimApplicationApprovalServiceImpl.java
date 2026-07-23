package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationDeductionRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.GeneralClaimApproverRequestDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto.LedgerEntryResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimBankResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationCalculationComponentRepository;
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
import com.claim.claim_processing.application.service.application.ClaimApplicationCalculationService;
import com.claim.claim_processing.application.service.application.ClaimApplicationDeductionDetailService;
import com.claim.claim_processing.application.service.application.ClaimApplicationForfeitedComponentService;
import com.claim.claim_processing.application.service.application.ClaimLedgerService;
import com.claim.claim_processing.application.service.claimDetail.ClaimDetailService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.others.MemberDetailRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import com.claim.claim_processing.document.service.DocumentMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.integration.client.PensionServiceClient;
import com.claim.claim_processing.integration.dto.PensionAutoTriggerRequestDto;
import com.claim.claim_processing.integration.dto.PensionAutoTriggerResponseDto;
import com.claim.claim_processing.rule.pension.dto.PensionDetailRequestDto;
import com.claim.claim_processing.rule.pension.dto.PensionDetailResponseDTO;
import com.claim.claim_processing.rule.pension.service.PensionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        private final MemberDetailRepository memberRepository;
        private final ClaimApplicationWorkflowService workflowService;

        private final ClaimApplicationApprovalMapper approvalMapper;
        private final ClaimDetailService claimDetailService;
        private final ClaimLedgerService claimLedgerService;

        private final ReserveAccountService reserveAccountService;
        private final PensionService pensionService;
        private final PensionServiceClient pensionServiceClient;
        private final ClaimApplicationCalculationComponentRepository calculationComponentRepository;
        private final DocumentMasterService documentMasterService;
        private final ClaimApplicationCalculationService claimApplicationCalculationService;

        private final ClaimApplicationDeductionDetailService claimApplicationDeductionDetailService;
        private final ClaimApplicationForfeitedComponentService claimApplicationForfeitedComponentService;

        @Override
        public ApiResponseDTO<ClaimApplicationApprovalResponseDto> patch(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(claimApplication.getApplicationNumber())
                                .orElseGet(() -> ClaimApplicationApproval.builder()
                                                .claimApplication(claimApplication)
                                                .createdBy(request.getApprovedBy() != null ? request.getApprovedBy()
                                                                : "sys")
                                                .build());

                applyRequest(approval, claimApplication, request);

                approval.setUpdatedBy(request.getApprovedBy() != null ? request.getApprovedBy() : "sys");

                ClaimApplicationApproval saved = approvalRepository.save(approval);

                return ApiResponseDTO.success(approvalMapper.toResponse(saved));
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralClaimDetailResponse> approve(
                        String applicationNumber,
                        GeneralClaimApproverRequestDto request) {

                ClaimApplicationApprovalRequestDto approvalRequest = request.getRequest();
                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                claimApplication.setStatus(getStatus(6L));
                claimApplicationRepository.save(claimApplication);

                ClaimApplicationApproval approval = approvalRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(new ClaimApplicationApproval());

                // Apply all updates from request
                applyRequest(approval, claimApplication, approvalRequest);

                // Validate required fields for approval
                if (approvalRequest.getApprovedBy() == null || approvalRequest.getApprovedBy().isBlank()) {
                        throw ClaimException.badRequest("Approved By is required");
                }

                // Set approval specific fields
                approval.setApprovedBy(approvalRequest.getApprovedBy());
                approval.setApprovedAt(new Timestamp(System.currentTimeMillis()));
                approval.setUpdatedBy(approvalRequest.getApprovedBy());
                approval.setClaimApplication(claimApplication);

                // Save approval
                ClaimApplicationApproval saved = approvalRepository.saveAndFlush(approval);

                // Create workflow entry
                Map<String, Long> workflowStage = resolveFromStageAndToStageAndAction(claimApplication.getId(), 6L);

                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                                .fromStageId(workflowStage.get("fromStage"))
                                .toStageId(workflowStage.get("toStage"))
                                .fromStatusId(workflowStage.get("fromStatus"))
                                .toStatusId(workflowStage.get("toStatus"))
                                .actionId(3L)
                                .reason(approvalRequest.getRemarks())
                                .actionBy(approvalRequest.getApprovedBy())
                                .build();

                workflowService.create(claimApplication, workflowRequest);
                claimApplicationRepository.findById(claimApplication.getId()).orElse(null);

                // ✅ 1. Save Calculation Summary (ELIGIBLE components)
                if (request.getCalculationSummary() != null) {
                        claimApplicationCalculationService.createForCalculation(claimApplication,
                                        request.getCalculationSummary());
                }

                // ✅ 2. Save DEDUCTION DETAIL (LOAN, RENTAL, TAX, etc.)
                if (request.getCalculationSummary() != null &&
                                request.getCalculationSummary().getDeductionDetail() != null) {

                        ClaimApplicationDeductionRequestDto deductionRequest = request.getCalculationSummary()
                                        .getDeductionDetail();

                        // If createdBy is null, use the approvedBy
                        if (deductionRequest.getCreatedBy() == null) {
                                deductionRequest.setCreatedBy(approvalRequest.getApprovedBy());
                        }

                        // Save deduction details
                        claimApplicationDeductionDetailService.saveCalculationDeductions(claimApplication,
                                        deductionRequest);
                        log.info("Saved deduction details for claim: {}", claimApplication.getApplicationNumber());
                }

                // ✅ 3. Save FORFEITED COMPONENTS (if any)
                if (request.getCalculationSummary() != null &&
                                request.getCalculationSummary().getForFeitedComponents() != null &&
                                !request.getCalculationSummary().getForFeitedComponents().isEmpty()) {

                        claimApplicationForfeitedComponentService.saveForfeitedComponents(
                                        claimApplication,
                                        request.getCalculationSummary().getForFeitedComponents());
                        log.info("Saved forfeited components for claim: {}", claimApplication.getApplicationNumber());
                }
                
                // Build response
                GeneralClaimResponse response = null;
                GeneralClaimDetailResponse claimDetailResponse = null;

                if (claimApplication.getIsSpecialCase().toString().equals("N")) {
                        response = buildGeneralClaimResponse(claimApplication);
                        System.out.println("here is claim build response: " + response.getAgencyCode());
                        MemberDetail member = memberRepository.findByNppfNumber(claimApplication.getNppfNumber()).orElse(null);
                        member.setRoleId(32L);
                        memberRepository.saveAndFlush(member);
                        // Create claim detail
                        claimDetailResponse = claimDetailService.create(response);

                        // ✅ 4. Create ledger entries (this will use the saved data)
                        AccountingEventResponseDto accountingEventResponse = claimLedgerService.createLedgerEntries(
                                        claimDetailResponse,
                                        approvalRequest.getApprovedBy());
                        claimDetailResponse.setAccountingEventDetail(accountingEventResponse);

                        if (claimApplication.getIsSpecialCase().toString().equals("N")) {
                                saveToReserveAccount(claimDetailResponse, approvalRequest.getApprovedBy());
                                triggerPensionAutoInitiation(claimApplication, approvalRequest.getApprovedBy());
                        }
                }

                // documentMasterService.transferDocumentsForApproval(claimApplication.getApplicationNumber(),
                // claimDetailResponse.getApplicationNumber(),
                // "MEMBER", request.getApprovedBy());

                return ApiResponseDTO.success(claimDetailResponse);
        }

        @Override
        public ApiResponseDTO<Page<GeneralClaimDetailResponse>> getAllApprovedDetails(Pageable pageable) {
                return claimDetailService.getAllApprovedDetails(pageable);
        }

        private void saveToReserveAccount(GeneralClaimDetailResponse claimDetailResponse, String createdBy) {
                try {
                        // Get forfeited components
                        List<ClaimForfeitedComponentResponseDto> forfeitedComponents = claimDetailResponse
                                        .getForfeitedComponents();

                        // Get lapse amount from accounting event
                        BigDecimal lapseAmount = BigDecimal.ZERO;
                        AccountingEventResponseDto accountingEvent = claimDetailResponse.getAccountingEventDetail();

                        System.out.println("========== DEBUG: saveToReserveAccount ==========");
                        System.out.println("Accounting Event is null: " + (accountingEvent == null));

                        if (forfeitedComponents != null && !forfeitedComponents.isEmpty()) {
                                System.out.println(
                                                "Ledger entries count: " + accountingEvent.getLedgerEntries().size());

                                // Find LAPSE entry by checking entryRole
                                for (ClaimForfeitedComponentResponseDto forfeitedComponent : forfeitedComponents) {
                                        ReserveAccountRequestDto reserveRequest = ReserveAccountRequestDto.builder()
                                        .memberCode(claimDetailResponse.getMemberCode())
                                        .nppfNumber(claimDetailResponse.getNppfNumber())
                                        .identityNumber(claimDetailResponse.getIdentityNumber())
                                        .agencyCategoryId(claimDetailResponse.getMemberCategoryId())
                                        .agencyCode(claimDetailResponse.getAgencyCode())
                                        .reserveType("FORFEITED")
                                        .totalAmount(forfeitedComponent.getAmount())
                                        .forfeitedAmount(forfeitedComponent.getAmount()) // For individual component, both are the same
                                        .componentCode(forfeitedComponent.getComponentCode())
                                        .build();

                        ApiResponseDTO<ReserveAccountResponseDto> response = reserveAccountService
                                        .create(reserveRequest);
                                }

                        }

                } catch (Exception e) {
                        System.out.println("❌ Error saving to reserve account: " + e.getMessage());
                        e.printStackTrace();
                        // Don't throw - reserve account save failure shouldn't rollback the transaction
                }
        }

        private void triggerPensionAutoInitiation(ClaimApplication claimApplication, String approvedBy) {
                try {

                        System.out.println("i am here jangtha: ");
                        if (claimApplication.getPensionApplicationRef() != null) {
                                log.info("Pension already auto-initiated for claim {} (ref {}), skipping",
                                                claimApplication.getApplicationNumber(),
                                                claimApplication.getPensionApplicationRef());
                                return;
                        }

                        boolean isNormalClaim = claimApplication.getClaimType() != null
                                        && "NORMAL_CLAIM".equalsIgnoreCase(claimApplication.getClaimType().getCode());
                        boolean isTierOne = claimApplication.getSchemeType() != null
                                        && "T1".equalsIgnoreCase(claimApplication.getSchemeType().getCode());

                        if (!isNormalClaim || !isTierOne) {
                                return;
                        }

                        NormalClaimDetail detail = claimApplication.getNormalClaimDetail();
                        if (detail == null) {
                                log.error("NORMAL_CLAIM {} has no NormalClaimDetail — cannot auto-trigger pension",
                                                claimApplication.getApplicationNumber());
                                claimApplication.setPensionTriggerStatus("FAILED");
                                claimApplicationRepository.save(claimApplication);
                                return;
                        }

                        String pensionType = resolvePensionType(detail.getCessationType() != null
                                        ? detail.getCessationType().getCode() : null);
                        if (pensionType == null) {
                                log.info("Cessation type {} has no auto-trigger mapping for claim {} — skipping",
                                                detail.getCessationType() != null ? detail.getCessationType().getCode() : "null",
                                                claimApplication.getApplicationNumber());
                                return;
                        }

                        LocalDate exitDate = detail.getCessationEffectiveDate();
                        if (exitDate == null) {
                                log.error("No usable exit date on claim {} — cannot auto-trigger pension",
                                                claimApplication.getApplicationNumber());
                                claimApplication.setPensionTriggerStatus("FAILED");
                                claimApplicationRepository.save(claimApplication);
                                return;
                        }

                        ClaimApplicationBankDetail bankDetail = null;
                        if (claimApplication.getBankDetails() != null && !claimApplication.getBankDetails().isEmpty()) {
                                bankDetail = claimApplication.getBankDetails().stream()
                                                .filter(b -> b.getIsDefaultBank() == ActivityEnum.Y)
                                                .findFirst()
                                                .orElse(claimApplication.getBankDetails().get(0));
                        }

                        List<ClaimApplicationCalculationComponent> calculationComponents =
                                        calculationComponentRepository
                                                        .findByRuleEvaluation_CalculationSummary_ClaimApplication_Id(
                                                                        claimApplication.getId());
                        List<PensionAutoTriggerRequestDto.ComponentDto> componentDtos = calculationComponents.stream()
                                        .map(c -> PensionAutoTriggerRequestDto.ComponentDto.builder()
                                                        .id(c.getId())
                                                        .code(c.getComponentCode())
                                                        .name(c.getComponentMaster() != null
                                                                        ? c.getComponentMaster().getName() : null)
                                                        .amount(c.getAmount())
                                                        .build())
                                        .toList();

                        PensionAutoTriggerRequestDto pensionRequest = PensionAutoTriggerRequestDto.builder()
                                        .memberCode(claimApplication.getMemberCode())
                                        .agencyCode(claimApplication.getAgencyCode())
                                        .pensionType(pensionType)
                                        .exitDate(exitDate)
                                        .exitReason(detail.getCessationType() != null ? detail.getCessationType().getName() : null)
                                        .pfSettlementClaimId(claimApplication.getId())
                                        .remarks("Auto-initiated on claim approval by " + approvedBy)
                                        .finalBasicSalary(detail.getFinalBasicSalary())
                                        .dateOfServiceJoining(detail.getDateOfServiceJoining())
                                        .bankTypeId(bankDetail != null && bankDetail.getBankType() != null
                                                        ? bankDetail.getBankType().getBankTypeId() : null)
                                        .bankName(bankDetail != null && bankDetail.getBankType() != null
                                                        ? bankDetail.getBankType().getBankTypeName() : null)
                                        .bankAccountNumber(bankDetail != null ? bankDetail.getAccountNumber() : null)
                                        .accountHolderName(bankDetail != null ? bankDetail.getAccountHolderName() : null)
                                        .ifscOrRoutingCode(bankDetail != null ? bankDetail.getIfscOrRoutingCode() : null)
                                        .components(componentDtos)
                                        .build();

                        PensionAutoTriggerResponseDto response = pensionServiceClient.triggerPfClaimApproved(pensionRequest);

                        if (response != null && response.getApplicationNo() != null) {
                                claimApplication.setPensionApplicationRef(response.getApplicationNo());
                                claimApplication.setPensionTriggerStatus("SENT");
                                log.info("Pension application {} auto-initiated for claim {}",
                                                response.getApplicationNo(), claimApplication.getApplicationNumber());
                        } else {
                                claimApplication.setPensionTriggerStatus("FAILED");
                                log.error("Pension auto-initiation failed for claim {} — added to retry worklist",
                                                claimApplication.getApplicationNumber());
                        }
                        claimApplicationRepository.save(claimApplication);

                } catch (Exception e) {
                        System.out.println("Unexpected error auto-initiating pension for claim: " + e.getMessage());
                        log.error("Unexpected error auto-initiating pension for claim {}: {}",
                                        claimApplication.getApplicationNumber(), e.getMessage(), e);
                }
        }

        private String resolvePensionType(String cessationTypeCode) {
                if (cessationTypeCode == null) {
                        return null;
                }
                return switch (cessationTypeCode.toUpperCase()) {
                        case "RETIREMENT", "SUPERANNUATION" -> "MEMBER_NORMAL";
                        case "EARLY_RETIREMENT" -> "MEMBER_EARLY";
                        case "MEDICAL_GROUND" -> "MEMBER_DISABILITY";
                        default -> null;
                };
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

                approval.setApprovalStatus(
                                statusRepository.findById(6L)
                                                .orElseThrow(() -> ClaimException.notFound(
                                                                "Approval status not found with id: " + 6L)));

                approval.setRemarks(request.getRemarks());
        }

        private Map<String, Long> resolveFromStageAndToStageAndAction(
                        Long applicationId,
                        Long statusId) {

                ClaimApplicationWorkflowResponseDto stageMaster = workflowService.getByApplicationId(applicationId)
                                .get(0);

                if (isApproved(statusId)) {
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