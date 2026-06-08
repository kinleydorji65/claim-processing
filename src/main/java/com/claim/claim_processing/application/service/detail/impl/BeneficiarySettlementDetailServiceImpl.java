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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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

        if (request.getCessationTypeId() != null) {
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

        if (request.getCessationTypeId() != null) {
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
    private List<BeneficiaryClaimantDetail> createClaimantDetails(BeneficiarySettlementDetail settlementDetail, List<BeneficiaryClaimantRequestDto> requests) {
        List<BeneficiaryClaimantDetail> claimantDetails = requests.stream()
                .map(request -> {
                    BeneficiaryClaimantDetail detail = beneficiaryClaimantDetailMapper.toEntity(request);
                    detail.setBeneficiarySettlementDetail(settlementDetail);
                    detail.setDependent(getMemberFamily(request.getDependentId()));
                    detail.setNominee(getMemberNominee(request.getNomineeId()));
                    detail.setClaimantType(getClaimantType(request.getClaimantTypeId()));
                    detail.setPayeeType(getPayeeType(request.getPayeeTypeId()));
                    detail.setRelationshipType(getRelationshipType(request.getRelationshipTypeId()));
                    beneficiaryClaimantDetailRepository.saveAndFlush(detail);
                    return detail;
                })
                .toList();
        return claimantDetails;
    }

    private List<BeneficiaryClaimantDetail> updateClaimantDetails(BeneficiarySettlementDetail settlementDetail, List<BeneficiaryClaimantRequestDto> requests) {
        List<BeneficiaryClaimantDetail> claimantDetails = requests.stream()
                .map(request -> {
                    BeneficiaryClaimantDetail detail = beneficiaryClaimantDetailRepository.findById(request.getBeneficiaryClaimantDetailId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Beneficiary claimant detail",
                                    String.valueOf(request.getBeneficiaryClaimantDetailId())));
                    detail.setBeneficiarySettlementDetail(settlementDetail);
                    detail.setDependent(getMemberFamily(request.getDependentId()));
                    detail.setNominee(getMemberNominee(request.getNomineeId()));
                    detail.setClaimantType(getClaimantType(request.getClaimantTypeId()));
                    detail.setPayeeType(getPayeeType(request.getPayeeTypeId()));
                    detail.setRelationshipType(getRelationshipType(request.getRelationshipTypeId()));
                    beneficiaryClaimantDetailRepository.saveAndFlush(detail);
                    return detail;
                })
                .toList();
        return claimantDetails;
    }

    private MemberFamily getMemberFamily(Long memberFamilyId) {
        return memberFamilyRepository.findById(memberFamilyId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Member family",
                        String.valueOf(memberFamilyId)));
    }

    private MemberNominee getMemberNominee(Long memberNomineeId) {
        return memberNomineeRepository.findById(memberNomineeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Member nominee",
                        String.valueOf(memberNomineeId)));
    }

    private ClaimantTypeMaster getClaimantType(Long claimantTypeId) {
        return claimantTypeMasterRepository.findById(claimantTypeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Claimant type",
                        String.valueOf(claimantTypeId)));
    }

    private PayeeTypeMaster getPayeeType(Long payeeTypeId) {
        return payeeTypeMasterRepository.findById(payeeTypeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Payee type",
                        String.valueOf(payeeTypeId)));
    }

    private RelationType getRelationshipType(Long relationshipTypeId) {
        return relationTypeRepository.findById(relationshipTypeId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Relationship type",
                        String.valueOf(relationshipTypeId)));
    }
}