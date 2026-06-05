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
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimApplicationWorkflowServiceImpl implements ClaimApplicationWorkflowService {

    private final ClaimApplicationWorkflowRepository workflowRepository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final StageRepository stageRepository;
    private final StatusMasterRepository statusMasterRepository;
    private final ActionMasterRepository actionMasterRepository;
    private final DecisionRepository decisionMasterRepository;
    private final WorkflowReasonRepository workflowReasonMasterRepository;
    private final NppfOfficeRepository nppfOfficeMasterRepository;
    private final ClaimApplicationWorkflowMapper mapper;

    @Override
    public ClaimApplicationWorkflowResponseDto create(ClaimApplicationWorkflowRequestDto request) {

        ClaimApplication claimApplication = claimApplicationRepository
                .findById(request.getClaimApplicationId())
                .orElseThrow(() -> new RuntimeException(
                        "Claim application not found with id: " + request.getClaimApplicationId()
                ));

        ClaimApplicationWorkflow workflow = ClaimApplicationWorkflow.builder()
                .claimApplication(claimApplication)
                .workflowLevel(request.getWorkflowLevel())
                .workflowStage(request.getWorkflowStageId() != null
                        ? stageRepository.findById(request.getWorkflowStageId())
                        .orElseThrow(() -> new RuntimeException(
                                "Workflow stage not found with id: " + request.getWorkflowStageId()
                        ))
                        : null)
                .fromStatus(request.getFromStatusId() != null
                        ? statusMasterRepository.findById(request.getFromStatusId())
                        .orElseThrow(() -> new RuntimeException(
                                "From status not found with id: " + request.getFromStatusId()
                        ))
                        : null)
                .toStatus(request.getToStatusId() != null
                        ? statusMasterRepository.findById(request.getToStatusId())
                        .orElseThrow(() -> new RuntimeException(
                                "To status not found with id: " + request.getToStatusId()
                        ))
                        : null)
                .action(request.getActionId() != null
                        ? actionMasterRepository.findById(request.getActionId())
                        .orElseThrow(() -> new RuntimeException(
                                "Action not found with id: " + request.getActionId()
                        ))
                        : null)
                .decision(request.getDecisionId() != null
                        ? decisionMasterRepository.findById(request.getDecisionId())
                        .orElseThrow(() -> new RuntimeException(
                                "Decision not found with id: " + request.getDecisionId()
                        ))
                        : null)
                .returnReason(request.getReturnReason())
                .rejectionReason(request.getRejectionReason())
                .approvalReason(request.getApprovalReasonId() != null
                        ? workflowReasonMasterRepository.findById(request.getApprovalReasonId())
                        .orElseThrow(() -> new RuntimeException(
                                "Approval reason not found with id: " + request.getApprovalReasonId()
                        ))
                        : null)
                .actionBy(request.getActionBy())
                .actionAt(new Timestamp(System.currentTimeMillis()))
                .office(request.getOfficeId() != null
                        ? nppfOfficeMasterRepository.findById(request.getOfficeId())
                        .orElseThrow(() -> new RuntimeException(
                                "Office not found with id: " + request.getOfficeId()
                        ))
                        : null)
                .referenceNumber(request.getReferenceNumber())
                .remarks(request.getRemarks())
                .build();

        return mapper.toResponse(workflowRepository.save(workflow));
    }

    @Override
    public ClaimApplicationWorkflowResponseDto getById(Long id) {
        return mapper.toResponse(
                workflowRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Workflow not found with id: " + id
                        ))
        );
    }

    @Override
    public List<ClaimApplicationWorkflowResponseDto> getByClaimApplicationId(Long claimApplicationId) {
        return mapper.toResponseList(
                workflowRepository.findByClaimApplication_IdOrderByActionAtAsc(claimApplicationId)
        );
    }

    @Override
    public List<ClaimApplicationWorkflowResponseDto> getAll() {
        return mapper.toResponseList(workflowRepository.findAll());
    }
}