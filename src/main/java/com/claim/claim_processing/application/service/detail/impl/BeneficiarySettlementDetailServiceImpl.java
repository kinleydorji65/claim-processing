package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiaryClaimantRequestDto;
import com.claim.claim_processing.application.DTO.request.detail.BeneficiarySettlementDetailRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import com.claim.claim_processing.application.mapper.detail.BeneficiaryClaimantDetailMapper;
import com.claim.claim_processing.application.mapper.detail.BeneficiarySettlementDetailMapper;
import com.claim.claim_processing.application.repository.detail.BeneficiaryClaimantDetailRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiarySettlementDetailRepository;
import com.claim.claim_processing.application.service.detail.BeneficiarySettlementDetailService;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.others.RelationType;
import com.claim.claim_processing.common.entities.others.member.MemberFamily;
import com.claim.claim_processing.common.entities.others.member.MemberNominee;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.common.repository.others.MemberFamilyRepository;
import com.claim.claim_processing.common.repository.others.MemberNomineeRepository;
import com.claim.claim_processing.common.repository.others.RelationTypeRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BeneficiarySettlementDetailServiceImpl
        implements BeneficiarySettlementDetailService {

    private final BeneficiarySettlementDetailRepository repository;
    private final BeneficiaryClaimantDetailMapper beneficiaryClaimantDetailMapper;
    private final BeneficiaryClaimantDetailRepository beneficiaryClaimantDetailRepository;
    private final CessationTypeRepository cessationTypeMasterRepository;
    private final BeneficiarySettlementDetailMapper mapper;
    private final MemberFamilyRepository memberFamilyRepository;
    private final MemberNomineeRepository memberNomineeRepository;
    private final ClaimantTypeRepository claimantTypeMasterRepository;
    private final PayeeTypeRepository payeeTypeMasterRepository;
    private final RelationTypeRepository relationTypeRepository;

    @Override
    @Transactional
    public BeneficiarySettlementDetail create(
            ClaimApplication claimApplication,
            BeneficiarySettlementDetailRequestDto request) {
        if (claimApplication == null) {
            throw ClaimException.badRequest("Claim application is required");
        }

        validateCreateRequest(request);

        BeneficiarySettlementDetail entity = mapper.toEntity(request);

        entity.setClaimApplication(claimApplication);

        if (request.getCessationTypeId() != null && request.getCessationTypeId() > 0) {
            CessationTypeMaster cessationType = cessationTypeMasterRepository
                    .findById(request.getCessationTypeId())
                    .orElseThrow(() -> ClaimException.resourceNotFound(
                            "Cessation type",
                            String.valueOf(request.getCessationTypeId())));

            entity.setCessationType(cessationType);
        }

        entity.setCreatedBy(request.getCreatedBy());
        repository.saveAndFlush(entity);
        createClaimantDetails(entity, request.getBeneficiaryClaimants());
        return entity;
    }

    @Override
    @Transactional
    public BeneficiarySettlementDetail patch(
            BeneficiarySettlementDetailRequestDto request) {
        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        if (request.getBeneficiarySettlementDetailId() == null) {
            throw ClaimException.badRequest("Beneficiary settlement detail id is required");
        }

        BeneficiarySettlementDetail entity = repository.findById(
                request.getBeneficiarySettlementDetailId())
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Beneficiary settlement detail",
                        String.valueOf(request.getBeneficiarySettlementDetailId())));

        validatePatchRequest(request);

        mapper.patchEntity(entity, request);

        if (request.getCessationTypeId() != null && request.getCessationTypeId() > 0) {
            CessationTypeMaster cessationType = cessationTypeMasterRepository
                    .findById(request.getCessationTypeId())
                    .orElseThrow(() -> ClaimException.resourceNotFound(
                            "Cessation type",
                            String.valueOf(request.getCessationTypeId())));

            entity.setCessationType(cessationType);
        }

        if (request.getUpdatedBy() != null) {
            entity.setUpdatedBy(request.getUpdatedBy());
        }
        repository.saveAndFlush(entity);
        updateClaimantDetails(entity, request.getBeneficiaryClaimants());
        return entity;
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw ClaimException.badRequest("Beneficiary settlement detail id is required");
        }

        BeneficiarySettlementDetail entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Beneficiary settlement detail",
                        String.valueOf(id)));

        repository.delete(entity);
    }

    private void validateCreateRequest(BeneficiarySettlementDetailRequestDto request) {
        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        if (request.getCessationTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "cessationTypeId",
                    "Cessation type id is required");
        }

        if (request.getDateOfDeath() == null) {
            throw ClaimException.singleValidationError(
                    "dateOfDeath",
                    "Date of death is required");
        }

        validateDates(request);
    }

    private void validatePatchRequest(BeneficiarySettlementDetailRequestDto request) {
        validateDates(request);
    }

    private void validateDates(BeneficiarySettlementDetailRequestDto request) {
        LocalDate today = LocalDate.now();

        if (request.getDateOfDeath() != null
                && request.getDateOfDeath().isAfter(today)) {
            throw ClaimException.badRequest("Date of death cannot be in the future");
        }

        if (request.getLastContributionDate() != null
                && request.getDateOfDeath() != null
                && request.getLastContributionDate().isAfter(request.getDateOfDeath())) {
            throw ClaimException.badRequest(
                    "Last contribution date cannot be after date of death");
        }

        if (request.getNonContributionMonths() != null
                && request.getNonContributionMonths() < 0) {
            throw ClaimException.badRequest(
                    "Non contribution months cannot be negative");
        }
    }

    // =============================================
    // CREATE CLAIMANT DETAILS
    // =============================================
    private List<BeneficiaryClaimantDetail> createClaimantDetails(
            BeneficiarySettlementDetail settlementDetail,
            List<BeneficiaryClaimantRequestDto> requests) {

        if (requests == null || requests.isEmpty()) {
            log.info("No claimant details to create");
            return List.of();
        }

        List<BeneficiaryClaimantDetail> claimantDetails = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            BeneficiaryClaimantRequestDto request = requests.get(i);

            try {
                log.info("Creating claimant at index {} with claimantTypeId: {}", 
                    i, request.getClaimantTypeId());

                // ✅ Validate the request based on claimant type
                validateClaimantRequest(request, i);

                BeneficiaryClaimantDetail detail = beneficiaryClaimantDetailMapper.toEntity(request);
                detail.setBeneficiarySettlementDetail(settlementDetail);

                // ✅ Set relationships with proper null handling
                detail.setDependent(getMemberFamily(request.getDependentId()));
                detail.setNominee(getMemberNominee(request.getNomineeId()));
                detail.setClaimantType(getClaimantType(request.getClaimantTypeId()));
                detail.setPayeeType(getPayeeType(request.getPayeeTypeId()));
                detail.setRelationshipType(getRelationshipType(request.getRelationshipTypeId()));

                // Set audit fields
                if (settlementDetail.getCreatedBy() != null) {
                    detail.setCreatedBy(settlementDetail.getCreatedBy());
                }

                beneficiaryClaimantDetailRepository.saveAndFlush(detail);
                claimantDetails.add(detail);

                log.info("✅ Created claimant at index {} with ID: {}", i, detail.getId());

            } catch (ClaimException e) {
                // Re-throw ClaimException as is
                throw e;
            } catch (Exception e) {
                log.error("Error creating claimant at index {}: {}", i, e.getMessage(), e);
                log.error("Request data: {}", request);
                throw ClaimException.badRequest(
                        String.format("Error creating claimant at index %d: %s", i, e.getMessage())
                );
            }
        }

        log.info("✅ Successfully created {} claimant details", claimantDetails.size());
        return claimantDetails;
    }

    // =============================================
    // UPDATE CLAIMANT DETAILS
    // =============================================
    private List<BeneficiaryClaimantDetail> updateClaimantDetails(
            BeneficiarySettlementDetail settlementDetail,
            List<BeneficiaryClaimantRequestDto> requests) {

        log.info("Updating {} claimant details", requests != null ? requests.size() : 0);

        if (requests == null || requests.isEmpty()) {
            log.warn("No claimant details to update");
            return List.of();
        }

        List<BeneficiaryClaimantDetail> claimantDetails = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            BeneficiaryClaimantRequestDto request = requests.get(i);

            try {
                // ✅ Validate the request based on claimant type
                validateClaimantRequest(request, i);

                BeneficiaryClaimantDetail detail;

                if (request.getBeneficiaryClaimantDetailId() != null) {
                    log.info("Updating existing claimant with ID: {}",
                            request.getBeneficiaryClaimantDetailId());

                    detail = beneficiaryClaimantDetailRepository
                            .findById(request.getBeneficiaryClaimantDetailId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Beneficiary claimant detail",
                                    String.valueOf(request.getBeneficiaryClaimantDetailId())));

                    // ✅ Update fields with proper null handling
                    detail.setBeneficiarySettlementDetail(settlementDetail);
                    detail.setDependent(getMemberFamily(request.getDependentId()));
                    detail.setNominee(getMemberNominee(request.getNomineeId()));
                    detail.setClaimantType(getClaimantType(request.getClaimantTypeId()));
                    detail.setPayeeType(getPayeeType(request.getPayeeTypeId()));
                    detail.setRelationshipType(getRelationshipType(request.getRelationshipTypeId()));

                    // Update audit fields
                    if (settlementDetail.getUpdatedBy() != null) {
                        detail.setUpdatedBy(settlementDetail.getUpdatedBy());
                    }

                } else {
                    log.info("Creating new claimant at index {} (no ID provided)", i);

                    detail = beneficiaryClaimantDetailMapper.toEntity(request);
                    detail.setBeneficiarySettlementDetail(settlementDetail);
                    detail.setDependent(getMemberFamily(request.getDependentId()));
                    detail.setNominee(getMemberNominee(request.getNomineeId()));
                    detail.setClaimantType(getClaimantType(request.getClaimantTypeId()));
                    detail.setPayeeType(getPayeeType(request.getPayeeTypeId()));
                    detail.setRelationshipType(getRelationshipType(request.getRelationshipTypeId()));

                    if (settlementDetail.getCreatedBy() != null) {
                        detail.setCreatedBy(settlementDetail.getCreatedBy());
                    }
                }

                claimantDetails.add(beneficiaryClaimantDetailRepository.saveAndFlush(detail));

                log.info("✅ Processed claimant at index {} with ID: {}", i, detail.getId());

            } catch (ClaimException e) {
                // Re-throw ClaimException as is
                throw e;
            } catch (Exception e) {
                log.error("Error processing claimant at index {}: {}", i, e.getMessage(), e);
                log.error("Problematic request data: {}", request);
                throw ClaimException.internalError(
                        String.format("Error processing claimant at index %d: %s", i, e.getMessage()),
                        e
                );
            }
        }

        log.info("Successfully processed {} claimant details", claimantDetails.size());
        return claimantDetails;
    }

    // =============================================
    // VALIDATION METHODS
    // =============================================
    
    /**
     * Validate claimant request based on claimant type
     */
    private void validateClaimantRequest(BeneficiaryClaimantRequestDto request, int index) {
    // Common validations for all claimants
    if (request.getClaimantTypeId() == null) {
        throw ClaimException.singleValidationError(
            String.format("beneficiaryClaimants[%d].claimantTypeId", index),
            "Claimant type is required"
        );
    }

    if (request.getBeneficiaryIdentifier() == null || request.getBeneficiaryIdentifier().isEmpty()) {
        throw ClaimException.singleValidationError(
            String.format("beneficiaryClaimants[%d].beneficiaryIdentifier", index),
            "Beneficiary identifier is required"
        );
    }

    // ✅ Fixed BigDecimal validation
    if (request.getBeneficiarySharePercentage() != null) {
        BigDecimal sharePercentage = request.getBeneficiarySharePercentage();
        if (sharePercentage.compareTo(BigDecimal.ZERO) < 0 || 
            sharePercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw ClaimException.singleValidationError(
                String.format("beneficiaryClaimants[%d].beneficiarySharePercentage", index),
                "Share percentage must be between 0 and 100"
            );
        }
    }

    // Type-specific validations
    Long claimantTypeId = request.getClaimantTypeId();

    if (claimantTypeId == 2) { // Nominee
        if (request.getNomineeId() == null || request.getNomineeId() <= 0) {
            throw ClaimException.singleValidationError(
                String.format("beneficiaryClaimants[%d].nomineeId", index),
                "Nominee ID is required for nominee claimant type"
            );
        }
    } else if (claimantTypeId == 3) { // Dependent
        if (request.getDependentId() == null || request.getDependentId() <= 0) {
            throw ClaimException.singleValidationError(
                String.format("beneficiaryClaimants[%d].dependentId", index),
                "Dependent ID is required for dependent claimant type"
            );
        }
    }
}

    // =============================================
    // HELPER METHODS - FETCH ENTITIES
    // =============================================

    /**
     * Fetch MemberFamily by ID - returns null for invalid IDs
     */
    private MemberFamily getMemberFamily(Long memberFamilyId) {
        if (memberFamilyId == null || memberFamilyId <= 0) {
            log.debug("Invalid memberFamilyId: {}, returning null", memberFamilyId);
            return null;
        }
        return memberFamilyRepository.findById(memberFamilyId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Member family",
                        String.valueOf(memberFamilyId)));
    }

    /**
     * Fetch MemberNominee by ID - returns null for invalid IDs
     */
    private MemberNominee getMemberNominee(Long memberNomineeId) {
        if (memberNomineeId == null || memberNomineeId <= 0) {
            log.debug("Invalid memberNomineeId: {}, returning null", memberNomineeId);
            return null;
        }
        return memberNomineeRepository.findById(memberNomineeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Member nominee",
                        String.valueOf(memberNomineeId)));
    }

    /**
     * Fetch ClaimantTypeMaster by ID - returns null for invalid IDs
     */
    private ClaimantTypeMaster getClaimantType(Long claimantTypeId) {
        if (claimantTypeId == null || claimantTypeId <= 0) {
            log.debug("Invalid claimantTypeId: {}, returning null", claimantTypeId);
            return null;
        }
        return claimantTypeMasterRepository.findById(claimantTypeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Claimant type",
                        String.valueOf(claimantTypeId)));
    }

    /**
     * Fetch PayeeTypeMaster by ID - returns null for invalid IDs
     */
    private PayeeTypeMaster getPayeeType(Long payeeTypeId) {
        if (payeeTypeId == null || payeeTypeId <= 0) {
            log.debug("Invalid payeeTypeId: {}, returning null", payeeTypeId);
            return null;
        }
        return payeeTypeMasterRepository.findById(payeeTypeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Payee type",
                        String.valueOf(payeeTypeId)));
    }

    /**
     * Fetch RelationType by ID - returns null for invalid IDs
     */
    private RelationType getRelationshipType(Long relationshipTypeId) {
        if (relationshipTypeId == null || relationshipTypeId <= 0) {
            log.debug("Invalid relationshipTypeId: {}, returning null", relationshipTypeId);
            return null;
        }
        return relationTypeRepository.findById(relationshipTypeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Relationship type",
                        String.valueOf(relationshipTypeId)));
    }
}