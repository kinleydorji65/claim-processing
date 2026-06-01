package com.claim.claim_processing.application.service.application.impl;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.mapper.application.ClaimApplicationMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.*;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.common.*;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseAuthorityRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.client.MasterCodeGenClient;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimApplicationServiceImpl implements ClaimApplicationService {

    private final ClaimApplicationRepository claimApplicationRepository;
    private final ClaimApplicationMapper claimApplicationMapper;

    private final ClaimTypeMasterRepository claimTypeMasterRepository;
    private final ClaimSourceRepository claimSourceMasterRepository;
    private final SubmissionChannelRepository submissionChannelMasterRepository;
    private final SchemeTypeRepository schemeMasterRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;
    private final SpecialCaseAuthorityRepository specialCaseRefundAuthorityMasterRepository;
    private final StageRepository stageMasterRepository;
    private final StatusMasterRepository statusMasterRepository;
    private final ActionMasterRepository actionMasterRepository;
    private final MasterCodeGenClient masterCodeGenClient;
    
    @Value("${app.codegen.application.code-type}")
    private String applicationCodeType;

    @Value("${app.codegen.application.claim-prefix}")
    private String claimPrefix;

    @Override
    public ApiResponseDTO<ClaimApplicationResponseDto> create(ClaimApplicationRequestDto request) {

        validateCreateRequest(request);

        ClaimApplication entity = claimApplicationMapper.toEntity(request);


        resolveAndSetForeignKeys(entity, request);
        entity.setApplicationNumber(
                generateApplicationNumber(entity.getClaimType())
        );
        entity.setApplicationDate(
                request.getApplicationDate() != null
                        ? request.getApplicationDate()
                        : LocalDate.now()
        );


        applyCreateDefaults(entity, request);

        ClaimApplication saved = claimApplicationRepository.save(entity);

        return ApiResponseDTO.created(claimApplicationMapper.toResponseDto(saved));
    }

    @Override
    public ApiResponseDTO<ClaimApplicationResponseDto> update(Long id, ClaimApplicationRequestDto request) {

        ClaimApplication existing = claimApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim application not found with id: " + id));

        claimApplicationMapper.updateEntityFromDto(request, existing);

        resolveAndSetForeignKeysForUpdate(existing, request);

        ClaimApplication updated = claimApplicationRepository.save(existing);

        return ApiResponseDTO.success(
                "Claim application updated successfully",
                claimApplicationMapper.toResponseDto(updated)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ClaimApplicationResponseDto> getById(Long id) {

        ClaimApplication entity = claimApplicationRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Claim application fetched successfully",
                claimApplicationMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ClaimApplicationResponseDto> getByApplicationNumber(
            String applicationNumber
    ) {

        ClaimApplication entity = claimApplicationRepository
                .findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application not found with application number: "
                                + applicationNumber
                ));

        return ApiResponseDTO.success(
                "Claim application fetched successfully",
                claimApplicationMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<ClaimApplicationResponseDto>> getAll() {

        List<ClaimApplicationResponseDto> response = claimApplicationRepository.findAll()
                .stream()
                .map(claimApplicationMapper::toResponseDto)
                .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound("No claim applications found");
        }

        return ApiResponseDTO.success(
                "Claim applications fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<ClaimApplicationResponseDto>> getByMemberCode(
            String memberCode
    ) {

        List<ClaimApplicationResponseDto> response =
                claimApplicationRepository.findByMemberCode(memberCode)
                        .stream()
                        .map(claimApplicationMapper::toResponseDto)
                        .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound(
                    "No claim applications found for member code: " + memberCode
            );
        }

        return ApiResponseDTO.success(
                "Claim applications fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<ClaimApplicationResponseDto>> getByNppfNumber(
            String nppfNumber
    ) {

        List<ClaimApplicationResponseDto> response =
                claimApplicationRepository.findByNppfNumber(nppfNumber)
                        .stream()
                        .map(claimApplicationMapper::toResponseDto)
                        .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound(
                    "No claim applications found for NPPF number: " + nppfNumber
            );
        }

        return ApiResponseDTO.success(
                "Claim applications fetched successfully",
                response
        );
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

    private void resolveAndSetForeignKeys(ClaimApplication entity, ClaimApplicationRequestDto request) {

        entity.setClaimType(getClaimType(request.getClaimTypeId()));

        if (request.getClaimSourceId() != null) {
            entity.setClaimSource(getClaimSource(request.getClaimSourceId()));
        }

        if (request.getSubmissionChannelId() != null) {
            entity.setSubmissionChannel(getSubmissionChannel(request.getSubmissionChannelId()));
        }

        if (request.getSchemeTypeId() != null) {
            entity.setSchemeType(getSchemeType(request.getSchemeTypeId()));
        }

        if (request.getMemberCategoryId() != null && !request.getMemberCategoryId().isBlank()) {
            entity.setMemberCategory(getMemberCategory(request.getMemberCategoryId()));
        }

        if (hasValidId(request.getParentClaimApplicationId())) {
            entity.setParentClaimApplication(getClaimApplication(request.getParentClaimApplicationId()));
        }

        if (request.getSpecialCaseAuthorityId() != null) {
            entity.setSpecialCaseAuthority(getSpecialCaseAuthority(request.getSpecialCaseAuthorityId()));
        }

        if (request.getCurrentStageId() != null) {
            entity.setCurrentStage(getStage(request.getCurrentStageId()));
        }

        if (request.getStatusId() != null) {
            entity.setStatus(getStatus(request.getStatusId()));
        }

        if (request.getActionId() != null) {
            entity.setAction(getAction(request.getActionId()));
        }
    }

    private boolean hasValidId(Long id) {
        return id != null && id > 0;
    }

    private void resolveAndSetForeignKeysForUpdate(
            ClaimApplication entity,
            ClaimApplicationRequestDto request
    ) {

        if (request.getClaimTypeId() != null) {
            entity.setClaimType(getClaimType(request.getClaimTypeId()));
        }

        if (request.getClaimSourceId() != null) {
            entity.setClaimSource(getClaimSource(request.getClaimSourceId()));
        }

        if (request.getSubmissionChannelId() != null) {
            entity.setSubmissionChannel(getSubmissionChannel(request.getSubmissionChannelId()));
        }

        if (request.getSchemeTypeId() != null) {
            entity.setSchemeType(getSchemeType(request.getSchemeTypeId()));
        }

        if (request.getMemberCategoryId() != null && !request.getMemberCategoryId().isBlank()) {
            entity.setMemberCategory(getMemberCategory(request.getMemberCategoryId()));
        }

        if (request.getParentClaimApplicationId() != null) {

            if (entity.getId().equals(request.getParentClaimApplicationId())) {
                throw ClaimException.badRequest(
                        "Parent claim application cannot be same as current application"
                );
            }

            entity.setParentClaimApplication(
                    getClaimApplication(request.getParentClaimApplicationId())
            );
        }

        if (request.getSpecialCaseAuthorityId() != null) {
            entity.setSpecialCaseAuthority(getSpecialCaseAuthority(request.getSpecialCaseAuthorityId()));
        }

        if (request.getCurrentStageId() != null) {
            entity.setCurrentStage(getStage(request.getCurrentStageId()));
        }

        if (request.getStatusId() != null) {
            entity.setStatus(getStatus(request.getStatusId()));
        }

        if (request.getActionId() != null) {
            entity.setAction(getAction(request.getActionId()));
        }
    }

    private void applyCreateDefaults(
            ClaimApplication entity,
            ClaimApplicationRequestDto request
    ) {

        entity.setIsSpecialCase(defaultFlag(request.getIsSpecialCase()));
        entity.setIsActive(defaultFlagY(request.getIsActive()));

        if (entity.getCurrencyCode() == null || entity.getCurrencyCode().isBlank()) {
            entity.setCurrencyCode("BTN");
        }

        if (entity.getCreatedBy() == null || entity.getCreatedBy().isBlank()) {

            if (request.getInitiatedBy() == null || request.getInitiatedBy().isBlank()) {
                throw ClaimException.badRequest(
                        "Initiated by is required for createdBy defaulting"
                );
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

    private String generateApplicationNumber(ClaimTypeMaster claimType) {

        if (claimType == null || claimType.getCode() == null || claimType.getCode().isBlank()) {
            throw ClaimException.badRequest("Claim type is required to generate application number");
        }

        String datePart = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String generatedApplicationCode = masterCodeGenClient.generateCode(applicationCodeType, claimPrefix);

        String baseApplicationNumber = "CLM-" + datePart + "-" + generatedApplicationCode;

        String applicationNumber = baseApplicationNumber;
        int counter = 1;

        while (claimApplicationRepository.existsByApplicationNumber(applicationNumber)) {
            applicationNumber = baseApplicationNumber + "-" + String.format("%03d", counter);
            counter++;
        }

        return applicationNumber;
    }

    private ClaimApplication getClaimApplication(Long id) {
        return claimApplicationRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Parent claim application not found with id: " + id
                ));
    }

    private ClaimTypeMaster getClaimType(Long id) {
        return claimTypeMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim type not found with id: " + id
                ));
    }

    private ClaimSourceMaster getClaimSource(Long id) {
        return claimSourceMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim source not found with id: " + id
                ));
    }

    private SubmissionChannelMaster getSubmissionChannel(Long id) {
        return submissionChannelMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Submission channel not found with id: " + id
                ));
    }

    private SchemeType getSchemeType(Long id) {
        return schemeMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Scheme type not found with id: " + id
                ));
    }

    private AgencyCategory getMemberCategory(String categoryId) {
        return agencyCategoryRepository.findById(categoryId)
                .orElseThrow(() -> ClaimException.notFound(
                        "Member category not found with id: " + categoryId
                ));
    }

    private SpecialCaseRefundAuthorityMaster getSpecialCaseAuthority(Long id) {
        return specialCaseRefundAuthorityMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case authority not found with id: " + id
                ));
    }

    private StageMaster getStage(Long id) {
        return stageMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Stage not found with id: " + id
                ));
    }

    private StatusMaster getStatus(Long id) {
        return statusMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Status not found with id: " + id
                ));
    }

    private ActionMaster getAction(Long id) {
        return actionMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Action not found with id: " + id
                ));
    }
}