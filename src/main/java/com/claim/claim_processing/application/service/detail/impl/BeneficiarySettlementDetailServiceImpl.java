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

    //added the claimant detail
    private List<BeneficiaryClaimantDetail> createClaimantDetails(
        BeneficiarySettlementDetail settlementDetail, 
        List<BeneficiaryClaimantRequestDto> requests) {
    
    if (requests == null || requests.isEmpty()) {
        return List.of();
    }
    
    List<BeneficiaryClaimantDetail> claimantDetails = new ArrayList<>();
    
    for (int i = 0; i < requests.size(); i++) {
        BeneficiaryClaimantRequestDto request = requests.get(i);
        
        try {
            // ✅ VALIDATE: Check if dependentId is null
            if (request.getDependentId() == null) {
                throw ClaimException.singleValidationError(
                    String.format("beneficiaryClaimants[%d].dependentId", i),
                    "Dependent ID is required for claimant detail"
                );
            }
            
            BeneficiaryClaimantDetail detail = beneficiaryClaimantDetailMapper.toEntity(request);
            detail.setBeneficiarySettlementDetail(settlementDetail);
            if (request.getDependentId() != null && request.getDependentId() > 0) {
                detail.setDependent(getMemberFamily(request.getDependentId()));
            }
            
            // ✅ Handle optional fields
            if (request.getNomineeId() != null && request.getNomineeId() > 0) {
                detail.setNominee(getMemberNominee(request.getNomineeId()));
            }
            
            if (request.getClaimantTypeId() != null) {
                detail.setClaimantType(getClaimantType(request.getClaimantTypeId()));
            }
            
            if (request.getPayeeTypeId() != null) {
                detail.setPayeeType(getPayeeType(request.getPayeeTypeId()));
            }
            
            if (request.getRelationshipTypeId() != null) {
                detail.setRelationshipType(getRelationshipType(request.getRelationshipTypeId()));
            }
            
            beneficiaryClaimantDetailRepository.saveAndFlush(detail);
            claimantDetails.add(detail);
            
        } catch (Exception e) {
            log.error("Error creating claimant at index {}: {}", i, e.getMessage());
            log.error("Request data: {}", request);
            throw ClaimException.badRequest(
                String.format("Error creating claimant at index %d: %s", i, e.getMessage())
            );
        }
    }
    
    return claimantDetails;
}

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

                } else {
                    log.info("Creating new claimant at index {} (no ID provided)", i);

                    detail = beneficiaryClaimantDetailMapper.toEntity(request);
                    detail.setBeneficiarySettlementDetail(settlementDetail);
                    detail.setDependent(getMemberFamily(request.getDependentId()));
                    detail.setNominee(getMemberNominee(request.getNomineeId()));
                    detail.setClaimantType(getClaimantType(request.getClaimantTypeId()));
                    detail.setPayeeType(getPayeeType(request.getPayeeTypeId()));
                    detail.setRelationshipType(getRelationshipType(request.getRelationshipTypeId()));
                }

                claimantDetails.add(beneficiaryClaimantDetailRepository.saveAndFlush(detail));

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

    // ✅ FIXED: Returns null for invalid IDs, fetches for valid ones
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

    // ✅ FIXED: Returns null for invalid IDs, fetches for valid ones
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


    

    private ClaimantTypeMaster getClaimantType(Long claimantTypeId) {
        return claimantTypeMasterRepository.findById(claimantTypeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Claimant type",
                        String.valueOf(claimantTypeId)));
    }
}