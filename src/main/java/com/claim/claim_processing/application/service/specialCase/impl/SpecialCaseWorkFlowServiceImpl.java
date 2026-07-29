package com.claim.claim_processing.application.service.specialCase.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.request.application.GeneralSpecialCaseApplicationRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto.SpecialCaseComponentBalanceResponseDTO;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseComponent;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;
import com.claim.claim_processing.application.mapper.application.SpecialCaseApplicationGeneralResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationBankResponseMapper;
import com.claim.claim_processing.application.mapper.workFlow.ClaimApplicationApprovalMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.application.ClaimSpecialCaseComponentRepository;
import com.claim.claim_processing.application.repository.workFlow.ClaimApplicationApprovalRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationBankDetailService;
import com.claim.claim_processing.application.service.application.ClaimApplicationService;
import com.claim.claim_processing.application.service.application.ValidateComponentService;
import com.claim.claim_processing.application.service.claimDetail.SpecialCaseService;
import com.claim.claim_processing.application.service.specialCase.ClaimSpecialCaseApplicationService;
import com.claim.claim_processing.application.service.specialCase.SpecialCaseLedgerService;
import com.claim.claim_processing.application.service.specialCase.SpecialCaseWorkFlowService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import com.claim.claim_processing.document.service.DocumentMasterService;
import com.claim.claim_processing.exceptions.ClaimException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialCaseWorkFlowServiceImpl implements SpecialCaseWorkFlowService {
    
    private final ClaimApplicationService claimApplicationService;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final ClaimApplicationWorkflowService claimApplicationWorkflowService;
    private final ClaimSpecialCaseApplicationService claimSpecialCaseApplicationService;
    private final SpecialCaseApplicationGeneralResponseMapper specialCaseGeneralResponseMapper;
    private final ClaimApplicationBankDetailService claimApplicationBankDetailService;
    private final StatusMasterRepository statusRepository;
    private final ClaimApplicationVerificationService claimApplicationVerificationService;
    private final ClaimApplicationApprovalService claimApplicationApprovalService;
    private final ClaimApplicationBankResponseMapper claimApplicationBankResponseMapper;
    private final SpecialCaseService specialCaseService;
    private final ClaimSpecialCaseComponentRepository claimSpecialCaseComponentRepository;
    private final SpecialCaseLedgerService specialCaseLedgerService;
    private final DocumentMasterService documentMasterService;
    private final ComponentMasterRepository componentMasterRepository;
    
    // ADD THESE MISSING DEPENDENCIES
    private final ClaimApplicationApprovalRepository approvalRepository;
    private final ClaimApplicationApprovalMapper approvalMapper;
    private final ValidateComponentService validateComponentService;

    @Override
    @Transactional
    public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> createSpecialCaseWithApplication(
            GeneralSpecialCaseApplicationRequest request) {
        // Implementation logic for creating a special case with application
        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        ClaimApplication claimApplication = claimApplicationService.create(request.getClaimApplication());
        claimApplication.setStatus(getStatusById(41L));
        claimApplicationRepository.save(claimApplication);
        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                .create(request.getClaimSpecialCaseApplicationRequestDto(), claimApplication);
        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService.create(
                claimApplication,
                List.of(request.getBankDetail()));

        ClaimApplicationVerificationRequestDto verificationRequest = ClaimApplicationVerificationRequestDto.builder()
                .claimApplicationId(claimApplication.getId())
                .verifiedBy(claimApplication.getCreatedBy())
                .build();
        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                .patch(claimApplication.getApplicationNumber(), verificationRequest);
        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                .fromStageId(request.getClaimApplication().getFromStageId())
                .toStageId(request.getClaimApplication().getToStageId())
                .fromStatusId(request.getClaimApplication().getFromStatusId())
                .toStatusId(request.getClaimApplication().getToStatusId())
                .reason(request.getClaimApplication().getReason())
                .actionBy(request.getClaimApplication().getCreatedBy())
                .build();
        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                .create(claimApplication, workflowRequest);
        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                .mapToGeneralClaimResponse(claimApplication);
        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
        responseDTO.setVerificationDetail(claimVerifier.getData());
        responseDTO.setWorkflowDetails(workflowDetails);
        responseDTO.setBankDetail(bankDetails.stream()
                .map(claimApplicationBankResponseMapper::toResponse)
                .findFirst()
                .orElse(null));
        return ApiResponseDTO.success(responseDTO);
    }

    @Override
    @Transactional
    public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> patchSpecialCaseWithApplication(
            GeneralSpecialCaseApplicationRequest request) {
        // Implementation logic for creating a special case with application
        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        ClaimApplication claimApplication = claimApplicationService.update(request.getClaimApplication());
        claimApplication.setStatus(getStatusById(41L));
        claimApplicationRepository.save(claimApplication);
        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                .patch(request.getClaimSpecialCaseApplicationRequestDto(), claimApplication);
        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService.patch(claimApplication,
                List.of(request.getBankDetail()));

        ClaimApplicationVerificationRequestDto verificationRequest = ClaimApplicationVerificationRequestDto.builder()
                .claimApplicationId(claimApplication.getId())
                .verifiedBy(claimApplication.getCreatedBy())
                .build();

        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                .patch(claimApplication.getApplicationNumber(), verificationRequest);
        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                .fromStageId(request.getClaimApplication().getFromStageId())
                .toStageId(request.getClaimApplication().getToStageId())
                .fromStatusId(request.getClaimApplication().getFromStatusId())
                .toStatusId(request.getClaimApplication().getToStatusId())
                .reason(request.getClaimApplication().getReason())
                .actionBy(request.getClaimApplication().getCreatedBy())
                .build();
        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                .create(claimApplication, workflowRequest);
        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                .mapToGeneralClaimResponse(claimApplication);
        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
        responseDTO.setVerificationDetail(claimVerifier.getData());
        responseDTO.setWorkflowDetails(workflowDetails);
        responseDTO.setBankDetail(bankDetails.stream()
                .map(claimApplicationBankResponseMapper::toResponse)
                .findFirst()
                .orElse(null));
        return ApiResponseDTO.success(responseDTO);
    }

    @Override
    @Transactional
    public ApiResponseDTO<GeneralSpecialCaseResponse> approveSpecialCase(
            String applicationNumber,
            ClaimApplicationApprovalRequestDto request) {

        log.info("========== START: approveSpecialCase for application: {} ==========", applicationNumber);
        long startTime = System.currentTimeMillis();

        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("Approval request cannot be null");
            }
            if (request.getApprovedBy() == null || request.getApprovedBy().trim().isEmpty()) {
                throw new IllegalArgumentException("Approved By is required");
            }

            // 1. Get and update claim application
            ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
            claimApplication.setStatus(getStatusById(6L)); // APPROVED status
            claimApplicationRepository.save(claimApplication);

            // 2. Create/Update approval DIRECTLY - NOT calling the problematic approve method
            ClaimApplicationApproval approval = approvalRepository
                    .findByClaimApplication_ApplicationNumber(applicationNumber)
                    .orElse(new ClaimApplicationApproval());

            approval.setClaimApplication(claimApplication);
            approval.setApprovedBy(request.getApprovedBy());
            approval.setApprovedAt(new Timestamp(System.currentTimeMillis()));
            approval.setUpdatedBy(request.getApprovedBy());
            approval.setApprovalStatus(getStatusById(6L));


            approvalRepository.saveAndFlush(approval);

            // Get approval response
            ClaimApplicationApprovalResponseDto approvalDetail = approvalMapper.toResponse(approval);

            // 3. Get special case response
            ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                    .getByApplicationNumber(applicationNumber);

            // 4. Get bank details
            List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                    .getByApplicationNumber(applicationNumber);

            // 5. Get workflow details
            List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                    .getByApplicationNumber(applicationNumber);

            // 6. Get verification details
            ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                    .getByApplicationNumber(applicationNumber);
            List<ClaimSpecialCaseComponent> components = claimSpecialCaseComponentRepository.findBySpecialCaseApplication_Id(specialCaseResponse.getData().getId());
                        List<SpecialCaseComponentBalanceResponseDTO> componentsDto = components.stream()
                        .filter(Objects::nonNull)
                        .filter(m -> m.getComponentMaster() != null)
                        .map(m -> SpecialCaseComponentBalanceResponseDTO.builder()
                                .id(m.getId())
                                .code(m.getComponentMaster().getCode())
                                .name(m.getComponentMaster().getName())
                                .amount(m.getAmount() != null ? m.getAmount() : BigDecimal.ZERO)
                                .build()
                        )
                        .collect(Collectors.toList());
            // 7. Build response DTO
            GeneralSpecialCaseApplicationResponseDTO responseDTO = buildResponseDTO(
                    claimApplication,
                    specialCaseResponse,
                    bankDetails,
                    workflowDetails,
                    claimVerifier,
                    approvalDetail,
                    componentsDto);

            // 8. Create special case
            GeneralSpecialCaseResponse specialCaseResponseDTO = specialCaseService.createSpecialCase(responseDTO);
            System.out.println("i am here bro: " + specialCaseResponse.getData().getApplicationNumber());
            if (specialCaseResponseDTO == null) {
                log.error("ERROR: specialCaseResponseDTO is null for application: {}", applicationNumber);
                throw new RuntimeException("Failed to create special case response");
            }

            specialCaseResponseDTO.setApprovalDetail(approvalDetail);
            specialCaseResponseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails : new ArrayList<>());
            specialCaseResponseDTO.setVerificationDetail(claimVerifier != null ? claimVerifier.getData() : null);

            // 9. CREATE LEDGER ENTRIES
            if ("Approved".equalsIgnoreCase(specialCaseResponseDTO.getStatusName())) {
                try {
                    AccountingEventResponseDto accountingEvent = specialCaseLedgerService
                            .createSpecialCaseLedgerEntries(specialCaseResponseDTO, request.getApprovedBy());
                    specialCaseResponseDTO.setAccountingEventDetail(accountingEvent);

                    log.info("Ledger entries created successfully for special case: {}", applicationNumber);
                    if (accountingEvent != null) {
                        log.info("Accounting Event ID: {}, Total DR: {}, Total CR: {}",
                                accountingEvent.getId());
                    }
                } catch (Exception e) {
                    log.error("Failed to create ledger entries for special case: {}", applicationNumber, e);
                    throw new RuntimeException("Failed to create ledger entries: " + e.getMessage(), e);
                }
            } else {
                log.info("Special case is not Approved. Status: {}, Skipping ledger creation.",
                        specialCaseResponseDTO.getStatusName());
            }

            // 10. TRANSFER DOCUMENTS
            try {
                if (specialCaseResponseDTO.getNppfNumber() != null) {
                    documentMasterService.transferDocumentsForApproval(
                            claimApplication.getApplicationNumber(),
                            specialCaseResponseDTO.getNppfNumber(),
                            "MEMBER",
                            request.getApprovedBy());
                    log.info("Documents transferred successfully for NPPF: {}",
                            specialCaseResponseDTO.getNppfNumber());
                } else {
                    log.warn("Cannot transfer documents - NPPF number is null for application: {}",
                            applicationNumber);
                }
            } catch (Exception e) {
                log.error("Failed to transfer documents for application: {}", applicationNumber, e);
                throw new RuntimeException("Failed to transfer documents: " + e.getMessage(), e);
            }

            // 11. CREATE WORKFLOW ENTRY FOR APPROVAL
            createApprovalWorkflowEntry(claimApplication, request);
            validateComponentService.updateComponents(specialCaseResponseDTO.getNppfNumber(), specialCaseResponseDTO.getSpecialCaseDetail().getComponents());
            return ApiResponseDTO.success(specialCaseResponseDTO);

        } catch (Exception e) {
            log.error("========== ERROR: approveSpecialCase FAILED for application: {} ==========",
                    applicationNumber, e);
            throw new RuntimeException("Failed to approve special case: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the response DTO
     */
    private GeneralSpecialCaseApplicationResponseDTO buildResponseDTO(
            ClaimApplication claimApplication,
            ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse,
            List<ClaimApplicationBankDetail> bankDetails,
            List<ClaimApplicationWorkflowResponseDto> workflowDetails,
            ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier,
            ClaimApplicationApprovalResponseDto approvalDetail,
            List<SpecialCaseComponentBalanceResponseDTO> componentsDto
            ) {

        try {
            // Map to general response
            GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                    .mapToGeneralClaimResponse(claimApplication);

            if (responseDTO == null) {
                throw new RuntimeException("Failed to map claim application to response DTO");
            }

            // Set all data
            if (specialCaseResponse != null && specialCaseResponse.getData() != null) {
                responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
            }
            if (componentsDto != null && !componentsDto.isEmpty()) {
                responseDTO.getClaimSpecialCaseApplicationResponseDto().setComponents(componentsDto);
            }

            if (claimVerifier != null && claimVerifier.getData() != null) {
                responseDTO.setVerificationDetail(claimVerifier.getData());
            }

            responseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails : new ArrayList<>());
            responseDTO.setApprovalDetail(approvalDetail);

            // Set bank detail (first one or null)
            if (bankDetails != null && !bankDetails.isEmpty()) {
                ClaimApplicationBankDetail firstBankDetail = bankDetails.get(0);
                ClaimApplicationBankResponseDto bankResponseDto = claimApplicationBankResponseMapper
                        .toResponse(firstBankDetail);
                responseDTO.setBankDetail(bankResponseDto);
            } else {
                log.warn("No bank details found for application: {}", claimApplication.getApplicationNumber());
                responseDTO.setBankDetail(null);
            }

            log.info("Built response DTO for application: {}", claimApplication.getApplicationNumber());
            return responseDTO;

        } catch (Exception e) {
            log.error("Failed to build response DTO for application: {}", claimApplication.getApplicationNumber(), e);
            throw new RuntimeException("Failed to build response DTO: " + e.getMessage(), e);
        }
    }

    /**
     * Creates workflow entry for approval
     */
    private void createApprovalWorkflowEntry(
            ClaimApplication claimApplication,
            ClaimApplicationApprovalRequestDto request) {

        try {
            log.info("Creating workflow entry for application: {}", claimApplication.getApplicationNumber());

            // Resolve workflow stages and statuses
            Map<String, Long> workflowStage = resolveFromStageAndToStageAndActionForSpecialCase(
                    claimApplication.getId(), request);

            // Build workflow request
            ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                    .fromStageId(workflowStage.get("fromStage"))
                    .toStageId(workflowStage.get("toStage"))
                    .fromStatusId(workflowStage.get("fromStatus"))
                    .toStatusId(workflowStage.get("toStatus"))
                    .actionId(2L)
                    .reason(request.getRemarks() != null ? request.getRemarks() : "Approved")
                    .actionBy(request.getApprovedBy())
                    .build();

            // Create workflow entry
            claimApplicationWorkflowService.create(claimApplication, workflowRequest);

            log.info("Workflow entry created for application: {}", claimApplication.getApplicationNumber());

        } catch (Exception e) {
            log.error("Failed to create workflow entry for application: {}", claimApplication.getApplicationNumber(), e);
            // Don't throw - workflow creation failure shouldn't stop approval
        }
    }

    /**
     * Resolves from stage, to stage, and action for special case approval
     */
    private Map<String, Long> resolveFromStageAndToStageAndActionForSpecialCase(
            Long applicationId,
            ClaimApplicationApprovalRequestDto request) {

        try {
            // Get current workflow details
            List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                    .getByApplicationId(applicationId);

            if (workflowDetails != null && !workflowDetails.isEmpty()) {
                ClaimApplicationWorkflowResponseDto currentWorkflow = workflowDetails.get(0);

                return Map.of(
                        "fromStage", currentWorkflow.getToStageId() != null ? currentWorkflow.getToStageId() : 3L,
                        "toStage", 5L,
                        "fromStatus", currentWorkflow.getToStatusId() != null ? currentWorkflow.getToStatusId() : 41L,
                        "toStatus", 6L);
            }

            // Default workflow mapping
            return Map.of(
                    "fromStage", 3L,
                    "toStage", 5L,
                    "fromStatus", 41L,
                    "toStatus", 6L);

        } catch (Exception e) {
            log.error("Error resolving workflow stages for application: {}", applicationId, e);
            return Map.of(
                    "fromStage", 3L,
                    "toStage", 5L,
                    "fromStatus", 41L,
                    "toStatus", 6L);
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> rejectSpecialCase(String applicationNumber,
            String rejectedBy, String rejectedRemarks) {
        ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
        claimApplication.setStatus(getStatusById(63L));
        claimApplicationRepository.save(claimApplication);
        claimApplicationApprovalService
                .verifiedClaimActionRejectedByApprover(applicationNumber, rejectedBy, rejectedRemarks)
                .getData();
        ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                .getByApplicationNumber(applicationNumber).getData();
        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                .getByApplicationNumber(applicationNumber);
        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                .getByApplicationNumber(applicationNumber);
        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                .getByApplicationNumber(applicationNumber);
        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                .getByApplicationNumber(applicationNumber);
        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                .mapToGeneralClaimResponse(claimApplication);
        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
        responseDTO.setVerificationDetail(claimVerifier.getData());
        responseDTO.setWorkflowDetails(workflowDetails);
        responseDTO.setBankDetail(bankDetails.stream()
                .map(claimApplicationBankResponseMapper::toResponse)
                .findFirst()
                .orElse(null));
        responseDTO.setApprovalDetail(getApproval);
        return ApiResponseDTO.success(responseDTO);
    }

    @Override
    public ApiResponseDTO<Page<GeneralSpecialCaseResponse>> getAllApprovedSpecialCases(Pageable pageable) {
        return specialCaseService.getAllApprovedSpecialCases(pageable);
    }

    @Override
    @Transactional
    public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> verifiedClaimActionClaimedBy(
            String applicationNumber, String claimedBy) {
        ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
        claimApplication.setStatus(getStatusById(61L));
        claimApplicationRepository.save(claimApplication);
        claimApplicationApprovalService.verifiedClaimActionClaimedBy(applicationNumber, claimedBy).getData();
        ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                .verifiedClaimActionClaimedBy(applicationNumber, claimedBy).getData();
        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                .getByApplicationNumber(applicationNumber);
        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                .getByApplicationNumber(applicationNumber);
        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                .getByApplicationNumber(applicationNumber);
        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                .getByApplicationNumber(applicationNumber);
        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                .mapToGeneralClaimResponse(claimApplication);
        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
        responseDTO.setVerificationDetail(claimVerifier.getData());
        responseDTO.setWorkflowDetails(workflowDetails);
        responseDTO.setBankDetail(bankDetails.stream()
                .map(claimApplicationBankResponseMapper::toResponse)
                .findFirst()
                .orElse(null));
        responseDTO.setApprovalDetail(getApproval);
        return ApiResponseDTO.success(responseDTO);
    }

    @Override
    @Transactional
    public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> verifiedClaimActionUnClaimedBy(
            String applicationNumber, String unClaimedBy) {
        ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
        claimApplication.setStatus(getStatusById(62L));
        claimApplicationRepository.save(claimApplication);
        claimApplicationApprovalService.verifiedClaimActionUnClaimedBy(applicationNumber, unClaimedBy)
                .getData();
        ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                .verifiedClaimActionUnClaimedBy(applicationNumber, unClaimedBy).getData();
        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                .getByApplicationNumber(applicationNumber);
        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                .getByApplicationNumber(applicationNumber);
        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                .getByApplicationNumber(applicationNumber);
        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                .getByApplicationNumber(applicationNumber);
        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                .mapToGeneralClaimResponse(claimApplication);
        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
        responseDTO.setVerificationDetail(claimVerifier.getData());
        responseDTO.setWorkflowDetails(workflowDetails);
        responseDTO.setBankDetail(bankDetails.stream()
                .map(claimApplicationBankResponseMapper::toResponse)
                .findFirst()
                .orElse(null));
        responseDTO.setApprovalDetail(getApproval);
        return ApiResponseDTO.success(responseDTO);
    }

    @Override
    @Transactional
    public ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getSpecialCaseUserCode(String userCode) {
        List<ClaimApplication> claimApplications = claimApplicationService
                .getByUserCodeAndSpecialClaim(userCode);

        if (claimApplications == null || claimApplications.isEmpty()) {
            log.info("No special case applications found for user code: {}", userCode);
            return ApiResponseDTO.success("No special case applications found for user code: " + userCode, null);
        }

        List<GeneralSpecialCaseApplicationResponseDTO> response = claimApplications.stream()
                .map(claimApplication -> {
                    try {
                        // Get special case details
                        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        if (specialCaseResponse == null || specialCaseResponse.getData() == null) {
                            log.debug("Skipping claim - No special case found: {}",
                                    claimApplication.getApplicationNumber());
                            return null;
                        }

                        // Get approval details
                        ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                .getByApplicationNumber(claimApplication.getApplicationNumber())
                                .getData();

                        // Get bank details
                        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Get workflow details
                        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Get verification details
                        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Build response
                        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);

                        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                        responseDTO.setVerificationDetail(claimVerifier != null ? claimVerifier.getData() : null);
                        responseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails : new ArrayList<>());

                        if (bankDetails != null && !bankDetails.isEmpty()) {
                            responseDTO.setBankDetail(
                                    claimApplicationBankResponseMapper.toResponse(bankDetails.get(0)));
                        } else {
                            responseDTO.setBankDetail(null);
                        }

                        responseDTO.setApprovalDetail(getApproval);

                        return responseDTO;

                    } catch (Exception e) {
                        log.error("Error processing claim: {}", claimApplication.getApplicationNumber(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (response.isEmpty()) {
            return ApiResponseDTO.success("No special case applications found for user code: " + userCode, response);
        }

        return ApiResponseDTO.success(response);
    }

    @Override
    public ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getAllSpecialCase() {
        List<ClaimApplication> claimApplications = claimApplicationService.getAllSpecialCase();

        if (claimApplications == null || claimApplications.isEmpty()) {
            log.info("No special case applications found.");
            return ApiResponseDTO.success("No special case applications found.", null);
        }

        List<GeneralSpecialCaseApplicationResponseDTO> response = claimApplications.stream()
                .map(claimApplication -> {
                    try {
                        // Get special case details
                        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        if (specialCaseResponse == null || specialCaseResponse.getData() == null) {
                            log.debug("Skipping claim - No special case found: {}",
                                    claimApplication.getApplicationNumber());
                            return null;
                        }

                        // Get approval details
                        ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                .getByApplicationNumber(claimApplication.getApplicationNumber())
                                .getData();

                        // Get bank details
                        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Get workflow details
                        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Get verification details
                        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Build response
                        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);
                        List<ClaimSpecialCaseComponent> components = claimSpecialCaseComponentRepository.findBySpecialCaseApplication_Id(specialCaseResponse.getData().getId());
                        List<SpecialCaseComponentBalanceResponseDTO> componentsDto = components.stream()
                        .filter(Objects::nonNull)
                        .filter(m -> m.getComponentMaster() != null)
                        .map(m -> SpecialCaseComponentBalanceResponseDTO.builder()
                                .id(m.getId())
                                .code(m.getComponentMaster().getCode())
                                .name(m.getComponentMaster().getName())
                                .amount(m.getAmount() != null ? m.getAmount() : BigDecimal.ZERO)
                                .build()
                        )
                        .collect(Collectors.toList());
                        
                        
                        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                        responseDTO.getClaimSpecialCaseApplicationResponseDto().setComponents(componentsDto);
                        responseDTO.setVerificationDetail(claimVerifier != null ? claimVerifier.getData() : null);
                        responseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails : new ArrayList<>());

                        if (bankDetails != null && !bankDetails.isEmpty()) {
                            responseDTO.setBankDetail(
                                    claimApplicationBankResponseMapper.toResponse(bankDetails.get(0)));
                        } else {
                            responseDTO.setBankDetail(null);
                        }

                        responseDTO.setApprovalDetail(getApproval);

                        return responseDTO;

                    } catch (Exception e) {
                        log.error("Error processing claim: {}", claimApplication.getApplicationNumber(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (response.isEmpty()) {
            return ApiResponseDTO.success("No special case applications found.", response);
        }

        return ApiResponseDTO.success(response);
    }

    @Override
    public ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getAllSpecialCaseWithClaimedBy(
            String claimedBy) {

        List<ClaimApplication> claimApplications = claimApplicationService
                .getAllSpecialCaseWithClaimedBy(claimedBy);

        if (claimApplications == null || claimApplications.isEmpty()) {
            log.info("No special case applications found for claimedBy: {}", claimedBy);
            return ApiResponseDTO.success("No special case applications found.", null);
        }

        List<GeneralSpecialCaseApplicationResponseDTO> response = claimApplications.stream()
                .map(claimApplication -> {
                    try {
                        // Get special case details
                        ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        if (specialCaseResponse == null || specialCaseResponse.getData() == null) {
                            log.debug("Skipping claim - No special case found: {}",
                                    claimApplication.getApplicationNumber());
                            return null;
                        }

                        // Get approval details
                        ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                .getByApplicationNumber(claimApplication.getApplicationNumber())
                                .getData();

                        // Get bank details
                        List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Get workflow details
                        List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Get verification details
                        ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .getByApplicationNumber(claimApplication.getApplicationNumber());

                        // Build response
                        GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);

                        responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                        responseDTO.setVerificationDetail(claimVerifier != null ? claimVerifier.getData() : null);
                        responseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails : new ArrayList<>());

                        if (bankDetails != null && !bankDetails.isEmpty()) {
                            responseDTO.setBankDetail(
                                    claimApplicationBankResponseMapper.toResponse(bankDetails.get(0)));
                        } else {
                            responseDTO.setBankDetail(null);
                        }

                        responseDTO.setApprovalDetail(getApproval);

                        return responseDTO;

                    } catch (Exception e) {
                        log.error("Error processing claim: {}", claimApplication.getApplicationNumber(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (response.isEmpty()) {
            return ApiResponseDTO.success("No special case applications found.", response);
        }

        return ApiResponseDTO.success(response);
    }

    private StatusMaster getStatusById(Long statusId) {
        return statusRepository.findById(statusId)
                .orElseThrow(() -> ClaimException.notFound("Status not found with id: " + statusId));
    }
}