package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationWorkflow;
import com.claim.claim_processing.application.mapper.workFlow.ClaimApplicationWorkflowMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.workFlow.ClaimApplicationWorkflowRepository;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.entities.common.*;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.common.*;
import com.claim.claim_processing.common.repository.others.NppfOfficeRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimApplicationWorkflowServiceImpl implements ClaimApplicationWorkflowService {

    private final ClaimApplicationWorkflowRepository workflowRepository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final StageRepository stageRepository;
    private final StatusMasterRepository statusMasterRepository;
    private final ActionMasterRepository actionMasterRepository;
    private final NppfOfficeRepository nppfOfficeMasterRepository;
    private final ClaimApplicationWorkflowMapper mapper;

    @Override
    @Transactional
    public List<ClaimApplicationWorkflowResponseDto> create(
            ClaimApplication claimApplication,
            ClaimApplicationWorkflowRequestDto request) {

        if (claimApplication == null || claimApplication.getId() == null) {
            throw new RuntimeException("Claim application is required.");
        }

        if (request == null) {
            throw new RuntimeException("Workflow request is required.");
        }

        request.setFromStageId("Y".equals(claimApplication.getOnBehalfOfMember()) ? 2L : 1L);
        request.setToStageId((request.getFromStageId() == 1L || request.getFromStageId() == 2L) ? 3L : 4L);
        request.setActionBy(claimApplication.getUpdatedBy());
        ClaimApplicationWorkflow workflow = buildWorkflow(claimApplication, request);

        workflowRepository.save(workflow);

        updateClaimApplicationCurrentState(claimApplication, workflow);

        List<ClaimApplicationWorkflow> workflows = workflowRepository
                .findByClaimApplication_IdOrderByActionAtDescCreatedAtDesc(
                        claimApplication.getId());

        return mapper.toResponseList(workflows);
    }

    private ClaimApplicationWorkflow buildWorkflow(
            ClaimApplication claimApplication,
            ClaimApplicationWorkflowRequestDto request) {

        return ClaimApplicationWorkflow.builder()
                .claimApplication(claimApplication)
                .fromStage(getStage(request.getFromStageId(), "From stage"))
                .toStage(getStage(request.getToStageId(), "To stage"))

                .fromStatus(getStatus(request.getFromStatusId(), "From status"))
                .toStatus(getStatus(request.getToStatusId(), "To status"))

                .action(getAction(request.getActionId()))

                .reason(request.getReason())

                .office(getOffice(request.getOfficeId()))
                .actionBy(request.getActionBy())
                .actionAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplicationWorkflowResponseDto> getByApplicationNumber(String applicationNumber) {

        if (applicationNumber == null || applicationNumber.isEmpty()) {
            throw new RuntimeException("Claim application number is required.");
        }

        ClaimApplication claimApplication = claimApplicationRepository
                .findByApplicationNumber(applicationNumber)
                .orElse(null);
        if (claimApplication == null) {
            return null;
        }

        List<ClaimApplicationWorkflow> workflows = workflowRepository
                .findByClaimApplication_IdOrderByActionAtDescCreatedAtDesc(
                        claimApplication.getId());

        return mapper.toResponseList(workflows);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getVerifiedApplication() {
        try {
            log.debug("Fetching verified applications");
            
            // ✅ Using JOIN FETCH - claim application is already loaded
            List<ClaimApplicationWorkflow> workflows = workflowRepository
                .findWorkflowsByActionAndNotAction(2L, 3L);

            if (workflows.isEmpty()) {
                log.debug("No workflows found with action 2 and not 3, trying action 2 only");
                workflows = workflowRepository.findWorkflowsByActionWithClaimApplication(2L);
                if (workflows.isEmpty()) {
                    log.debug("No workflows found with action 2");
                    return List.of();
                }
            }
            
            // ✅ Safely map - no lazy loading needed
            return workflows.stream()
                .map(workflow -> {
                    try {
                        if (workflow.getClaimApplication() == null) {
                            log.warn("Workflow {} has null claim application", workflow.getId());
                            return null;
                        }
                        return workflow.getClaimApplication().getApplicationNumber();
                    } catch (Exception e) {
                        log.error("Error getting application number for workflow: {}", workflow.getId(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct() // Remove any duplicates
                .collect(java.util.stream.Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error in getVerifiedApplication", e);
            return List.of();
        }
    }

    private StageMaster getStage(Long stageId, String label) {
        if (stageId == null || stageId <= 0) {
            return null;
        }

        return stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException(
                        label + " not found with id: " + stageId));
    }

    private StatusMaster getStatus(Long statusId, String label) {
        if (statusId == null || statusId <= 0) {
            return null;
        }

        return statusMasterRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException(
                        label + " not found with id: " + statusId));
    }

    private ActionMaster getAction(Long actionId) {
        if (actionId == null || actionId <= 0) {
            return null;
        }

        return actionMasterRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException(
                        "Action not found with id: " + actionId));
    }

    private NppfOfficeMaster getOffice(Long officeId) {
        if (officeId == null || officeId <= 0) {
            return null;
        }

        return nppfOfficeMasterRepository.findById(officeId)
                .orElseThrow(() -> new RuntimeException(
                        "Office not found with id: " + officeId));
    }

    private void updateClaimApplicationCurrentState(
            ClaimApplication claimApplication,
            ClaimApplicationWorkflow workflow) {

        if (workflow.getToStage() != null) {
            claimApplication.setCurrentStage(workflow.getToStage());
        }

        if (workflow.getToStatus() != null) {
            claimApplication.setStatus(workflow.getToStatus());
        }

        claimApplication.setUpdatedBy(workflow.getActionBy());

        claimApplicationRepository.saveAndFlush(claimApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplicationWorkflowResponseDto> getByApplicationId(Long applicationId) {

        if (applicationId == null) {
            throw new RuntimeException("Claim application id is required.");
        }

        // ❌ REMOVE THIS - It's triggering the JOIN FETCH
        // boolean claimExists = claimApplicationRepository.existsById(applicationId);
        // if (!claimExists) {
        // throw new RuntimeException("Claim application not found with id: " +
        // applicationId);
        // }

        // ✅ Just fetch workflows directly
        List<ClaimApplicationWorkflow> workflows = workflowRepository
                .findByClaimApplication_IdOrderByActionAtDescCreatedAtDesc(applicationId);

        return mapper.toResponseList(workflows);
    }
}