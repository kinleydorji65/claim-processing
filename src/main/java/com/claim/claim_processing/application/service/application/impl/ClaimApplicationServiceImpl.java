package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.mapper.application.ClaimApplicationMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationService;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.*;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.UserRegistrateredAgencyMapping;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.common.*;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.client.MasterCodeGenClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimApplicationServiceImpl implements ClaimApplicationService {

    private final ClaimApplicationRepository claimApplicationRepository;
    private final ClaimApplicationMapper claimApplicationMapper;

    private final ClaimTypeMasterRepository claimTypeMasterRepository;
    private final SubmissionChannelRepository submissionChannelMasterRepository;
    private final SchemeTypeRepository schemeMasterRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;
    private final StageRepository stageMasterRepository;
    private final StatusMasterRepository statusMasterRepository;
    private final MasterCodeGenClient masterCodeGenClient;
    private final UserRegistrateredAgencyMappingRepository userRegistrateredAgencyMappingRepository;

    @Value("${app.codegen.application.code-type}")
    private String applicationCodeType;

    @Value("${app.codegen.application.claim-prefix}")
    private String claimPrefix;

    @Override
    public ClaimApplication create(ClaimApplicationRequestDto request) {
        try {
        validateCreateRequest(request);

        ClaimApplication entity = claimApplicationMapper.toEntity(request);

        resolveAndSetForeignKeys(entity, request);
        entity.setApplicationNumber(
                masterCodeGenClient.generateCode(applicationCodeType, claimPrefix));
        entity.setApplicationDate(
                request.getApplicationDate() != null
                        ? request.getApplicationDate()
                        : LocalDate.now());

        applyCreateDefaults(entity, request);

        ClaimApplication saved = claimApplicationRepository.saveAndFlush(entity);

        return saved;
        } catch (Exception e) {
    // This will show you EXACTLY what's wrong
    log.error("=== SAVE FAILED ===");
    log.error("Exception Type: {}", e.getClass().getName());
    log.error("Message: {}", e.getMessage());
    
    // Get the root cause (most important!)
    Throwable rootCause = getRootCause(e);
    log.error("Root Cause: {}", rootCause.getMessage());
    
    // If it's a constraint violation, get details
    if (e instanceof DataIntegrityViolationException) {
        DataIntegrityViolationException dive = (DataIntegrityViolationException) e;
        log.error("SQL Error Code: {}", dive.getMostSpecificCause() instanceof SQLException ? 
            ((SQLException) dive.getMostSpecificCause()).getErrorCode() : "N/A");
        log.error("SQL State: {}", dive.getMostSpecificCause() instanceof SQLException ? 
            ((SQLException) dive.getMostSpecificCause()).getSQLState() : "N/A");
    }
    
    // Print full stack trace
    e.printStackTrace();
    throw new RuntimeException("Failed to save claim: " + rootCause.getMessage(), e);
}
    }

    private Throwable getRootCause(Throwable e) {
    Throwable cause = e;
    while (cause.getCause() != null && cause.getCause() != cause) {
        cause = cause.getCause();
    }
    return cause;
}

@Override
@Transactional(readOnly = true)
public Page<ClaimApplication> findByInitiatedByAndIsSpecialCase(String initiatedBy, ActivityEnum isSpecialCase, Pageable pageable) {
    log.info("Fetching claims for initiated by: {}, isSpecialCase: {}, page: {}, size: {}", 
            initiatedBy, isSpecialCase, pageable.getPageNumber(), pageable.getPageSize());
    
    return claimApplicationRepository.findByInitiatedByAndIsSpecialCase(initiatedBy, isSpecialCase, pageable);
}

    @Override
    public ClaimApplication update(ClaimApplicationRequestDto request) {

        ClaimApplication existing = claimApplicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException(
                        "Claim application not found with id: " + request.getApplicationId()));

        claimApplicationMapper.updateEntityFromDto(request, existing);

        resolveAndSetForeignKeysForUpdate(existing, request);

        ClaimApplication updated = claimApplicationRepository.save(existing);

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimApplication getById(Long id) {

        ClaimApplication entity = claimApplicationRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application not found with id: " + id));

        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimApplication getByApplicationNumber(
            String applicationNumber) {

        ClaimApplication entity = claimApplicationRepository
                .findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application not found with application number: "
                                + applicationNumber));

        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplication> getAll() {

        List<ClaimApplication> response = claimApplicationRepository.findAll();
        response.stream().map(m -> {
            m.getIsSpecialCase().toString().equals("N");
            return m;
        }).toString();
        if (response.isEmpty()) {
            throw ClaimException.notFound("No claim applications found");
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplication> getByMemberCode(
            String memberCode) {

        List<ClaimApplication> response = claimApplicationRepository.findByMemberCode(memberCode);
                response.stream().map(m -> {
            m.getIsSpecialCase().toString().equals("N");
            return m;
        }).toString();
        if (response.isEmpty()) {
            throw ClaimException.notFound(
                    "No claim applications found for member code: " + memberCode);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplication> getByNppfNumber(
            String nppfNumber) {

        List<ClaimApplication> response = claimApplicationRepository.findByNppfNumber(nppfNumber);
                response.stream().map(m -> {
            m.getIsSpecialCase().toString().equals("N");
            return m;
        }).toString();
        if (response.isEmpty()) {
            throw ClaimException.notFound(
                    "No claim applications found for NPPF number: " + nppfNumber);
        }

        return response;
    }

    private void validateCreateRequest(ClaimApplicationRequestDto request) {

        if (request.getClaimTypeId() == null) {
            throw ClaimException.badRequest("Claim type is required");
        }

        if (request.getMemberCode() == null || request.getMemberCode().isBlank()) {
            throw ClaimException.badRequest("Member code is required");
        }

        if (request.getNppfNumber() == null || request.getNppfNumber().isBlank()) {
            throw ClaimException.badRequest("NPPF number is required");
        }

        if (request.getInitiatedBy() == null || request.getInitiatedBy().isBlank()) {
            throw ClaimException.badRequest("Initiated by is required");
        }
    }

    @Override
    public ClaimApplication claimedBy(String applicationId, String claimedBy) {
        if (applicationId == null) {
            throw ClaimException.badRequest("Application id is required");
        }

        if (claimedBy == null) {
            throw ClaimException.badRequest("Claimed by is required");
        }

        ClaimApplication existingClaimApplication = claimApplicationRepository.findByApplicationNumber(applicationId)
                .orElseThrow(() -> ClaimException
                        .notFound("Claim Application not found with application number: " + applicationId));
        existingClaimApplication.setClaimedBy(claimedBy);
        existingClaimApplication.setUpdatedBy(claimedBy);
        existingClaimApplication.setStatus(getStatus(3L)); // Assuming you have a method to get the status by code
        claimApplicationRepository.saveAndFlush(existingClaimApplication);

        return existingClaimApplication;
    }

    @Override
    public ClaimApplication unClaimedBy(String applicationId, String unclaimedBy) {
        if (applicationId == null) {
            throw ClaimException.badRequest("Application id is required");
        }

        if (unclaimedBy == null) {
            throw ClaimException.badRequest("Unclaimed by is required");
        }

        ClaimApplication existingClaimApplication = claimApplicationRepository.findByApplicationNumber(applicationId)
                .orElseThrow(() -> ClaimException
                        .notFound("Claim Application not found with application number: " + applicationId));
        existingClaimApplication.setUnClaimedBy(null);
        existingClaimApplication.setUpdatedBy(unclaimedBy);
        existingClaimApplication.setClaimedBy(null);
        existingClaimApplication.setStatus(getStatus(4L)); // Assuming you have a method to get the status by code
        claimApplicationRepository.saveAndFlush(existingClaimApplication);

        return existingClaimApplication;
    }

    @Override
    public List<ClaimApplication> getByAgencyCodeAndClaimTypeId(String agencyCode, Long claimTypeId) {
        List<ClaimApplication> response = claimApplicationRepository.findByAgencyCodeAndClaimType_Id(agencyCode, claimTypeId);
        response.stream().map(m -> {
            m.getIsSpecialCase().toString().equals("N");
            return m;
        }).toString();
        if (response.isEmpty()) {
            throw ClaimException.notFound(
                    "No claim applications found for agency code: " + agencyCode + " and claim type ID: " + claimTypeId);
        }

        return response;
    }

    @Override
    public List<ClaimApplication> getByUserCodeAndStatusId(String userCode, Long statusId) {
        List<UserRegistrateredAgencyMapping> userMappings = userRegistrateredAgencyMappingRepository.findByUserCode(userCode);

        if (userMappings.isEmpty()) {
            return null;
        }
        List<ClaimApplication> claimApplication = userMappings.stream()
                .flatMap(mapping -> claimApplicationRepository.findByAgencyCodeAndStatus_StatusId(mapping.getAgencyCode(), statusId).stream())
                .toList();
        claimApplication.stream().map(m -> {
            m.getIsSpecialCase().toString().equals("N");
            return m;
        }).toString();
        return claimApplication;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplication> getVerifiedApplication() {
        List<ClaimApplication> response = claimApplicationRepository.findByStatus_StatusId(41L);

        response.stream().map(m -> {
            m.getIsSpecialCase().toString().equals("N");
            return m;
        }).toString();
        if (response.isEmpty()) {
            throw ClaimException.notFound("No verified claim applications found");
        }

        return response;
     }

    private void resolveAndSetForeignKeys(ClaimApplication entity, ClaimApplicationRequestDto request) {
        if (request.getClaimTypeId() != null && request.getClaimTypeId() > 0) {
            entity.setClaimType(getClaimType(request.getClaimTypeId()));
        }

        if (request.getSubmissionChannelId() != null && request.getSubmissionChannelId() > 0) {
            entity.setSubmissionChannel(getSubmissionChannel(request.getSubmissionChannelId()));
        }

        if (request.getSchemeTypeId() != null && request.getSchemeTypeId() > 0) {
            entity.setSchemeType(getSchemeType(request.getSchemeTypeId()));
        }

        if (request.getMemberCategoryId() != null && !request.getMemberCategoryId().isBlank()) {
            entity.setMemberCategory(getMemberCategory(request.getMemberCategoryId()));
        }

        if (request.getCurrentStageId() != null && request.getCurrentStageId() > 0) {
            entity.setCurrentStage(getStage(request.getCurrentStageId()));
        }

        if (request.getStatusId() != null && request.getStatusId() > 0) {
            entity.setStatus(getStatus(request.getStatusId()));
        }
    }

    private void resolveAndSetForeignKeysForUpdate(
            ClaimApplication entity,
            ClaimApplicationRequestDto request) {

        if (request.getClaimTypeId() != null && request.getClaimTypeId() > 0) {
            entity.setClaimType(getClaimType(request.getClaimTypeId()));
        }

        if (request.getSubmissionChannelId() != null && request.getSubmissionChannelId() > 0) {
            entity.setSubmissionChannel(getSubmissionChannel(request.getSubmissionChannelId()));
        }

        if (request.getSchemeTypeId() != null && request.getSchemeTypeId() > 0) {
            entity.setSchemeType(getSchemeType(request.getSchemeTypeId()));
        }

        if (request.getMemberCategoryId() != null && !request.getMemberCategoryId().isBlank()) {
            entity.setMemberCategory(getMemberCategory(request.getMemberCategoryId()));
        }

        if (request.getCurrentStageId() != null && request.getCurrentStageId() > 0) {
            entity.setCurrentStage(getStage(request.getCurrentStageId()));
        }

        if (request.getStatusId() != null && request.getStatusId() > 0) {
            entity.setStatus(getStatus(request.getStatusId()));
        }
    }

    private void applyCreateDefaults(
            ClaimApplication entity,
            ClaimApplicationRequestDto request) {

        entity.setIsSpecialCase(defaultFlag(request.getIsSpecialCase()));
        entity.setIsActive(defaultFlagY(request.getIsActive()));

        if (entity.getCurrencyCode() == null || entity.getCurrencyCode().isBlank()) {
            entity.setCurrencyCode("BTN");
        }

        if (entity.getCreatedBy() == null || entity.getCreatedBy().isBlank()) {

            if (request.getInitiatedBy() == null || request.getInitiatedBy().isBlank()) {
                throw ClaimException.badRequest(
                        "Initiated by is required for createdBy defaulting");
            }

            entity.setCreatedBy(request.getInitiatedBy());
        }
    }

    private ActivityEnum defaultFlag(ActivityEnum value) {
        return value != null ? value : ActivityEnum.N;
    }

    private ActivityEnum defaultFlagY(ActivityEnum value) {
        return value != null ? value : ActivityEnum.Y;
    }

    private ClaimTypeMaster getClaimType(Long id) {
        return claimTypeMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim type not found with id: " + id));
    }

    private SubmissionChannelMaster getSubmissionChannel(Long id) {
        return submissionChannelMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Submission channel not found with id: " + id));
    }

    private SchemeType getSchemeType(Long id) {
        return schemeMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Scheme type not found with id: " + id));
    }

    private AgencyCategory getMemberCategory(String categoryId) {
        return agencyCategoryRepository.findById(categoryId)
                .orElseThrow(() -> ClaimException.notFound(
                        "Member category not found with id: " + categoryId));
    }

    private StageMaster getStage(Long id) {
        return stageMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Stage not found with id: " + id));
    }

    private StatusMaster getStatus(Long id) {
        return statusMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Status not found with id: " + id));
    }

    @Override
    public List<ClaimApplication> getLegalRecoveryWithUserCode(String userCode){
        List<UserRegistrateredAgencyMapping> userMappings = userRegistrateredAgencyMappingRepository.findByUserCode(userCode);

        if (userMappings.isEmpty()) {
            return null;
        }
        List<ClaimApplication> claimApplication = userMappings.stream()
                .flatMap(mapping -> claimApplicationRepository.findByAgencyCodeAndClaimType_Id(mapping.getAgencyCode(), 5L).stream())
                .toList();
        claimApplication.stream().map(m -> {
            m.getIsSpecialCase().toString().equals("N");
            return m;
        }).toString();
        return claimApplication;
    }

    @Override
    public List<ClaimApplication> getByUserCodeAndSpecialClaim(String userCode) {
        List<UserRegistrateredAgencyMapping> userMappings = userRegistrateredAgencyMappingRepository.findByUserCode(userCode);
        userMappings.forEach(mapping -> System.out.println("User Mapping: " + mapping.getUserCode() + ", Agency Code: " + mapping.getAgencyCode()));

        if (userMappings.isEmpty()) {
            return null;
        }
        List<ClaimApplication> claimApplication = userMappings.stream()
                .flatMap(mapping -> claimApplicationRepository.findByAgencyCodeAndIsSpecialCase(mapping.getAgencyCode(), ActivityEnum.Y).stream())
                .toList();

        return claimApplication;
    }

    @Override
    public List<ClaimApplication> getAllSpecialCase() {
        List<ClaimApplication> response = claimApplicationRepository.findByIsSpecialCase(ActivityEnum.Y);

        if (response.isEmpty()) {
            return null;
        }

        return response;
    }

    @Override
    public List<ClaimApplication> getAllSpecialCaseWithClaimedBy(String claimedBy) {
        List<ClaimApplication> response = claimApplicationRepository.findByIsSpecialCaseAndClaimedBy(ActivityEnum.Y, claimedBy);

        if (response.isEmpty()) {
            return null;
        }

        return response;
    }
}