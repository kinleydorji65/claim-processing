package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiaryClaimantRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import com.claim.claim_processing.application.mapper.detail.BeneficiaryClaimantDetailMapper;
import com.claim.claim_processing.application.repository.detail.BeneficiaryClaimantDetailRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiarySettlementDetailRepository;
import com.claim.claim_processing.application.service.detail.BeneficiaryClaimantDetailService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.RelationType;
import com.claim.claim_processing.common.entities.others.member.MemberFamily;
import com.claim.claim_processing.common.entities.others.member.MemberNominee;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.common.repository.others.RelationTypeRepository;
import com.claim.claim_processing.common.repository.others.MemberFamilyRepository;
import com.claim.claim_processing.common.repository.others.MemberNomineeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficiaryClaimantDetailServiceImpl implements BeneficiaryClaimantDetailService {

    private final BeneficiaryClaimantDetailRepository beneficiaryClaimantDetailRepository;
    private final BeneficiaryClaimantDetailMapper beneficiaryClaimantDetailMapper;

    private final BeneficiarySettlementDetailRepository beneficiarySettlementDetailRepository;
    private final MemberNomineeRepository memberNomineeRepository;
    private final MemberFamilyRepository memberFamilyRepository;
    private final ClaimantTypeRepository claimantTypeMasterRepository;
    private final RelationTypeRepository relationTypeRepository;
    private final PayeeTypeRepository payeeTypeMasterRepository;

    @Override
    public ApiResponseDTO<BeneficiaryClaimantResponseDto> create(
            BeneficiaryClaimantRequestDto request) {

        BeneficiarySettlementDetail settlementDetail =
                beneficiarySettlementDetailRepository.findById(request.getBeneficiarySettlementDetailId())
                        .orElseThrow(() -> new RuntimeException(
                                "Beneficiary settlement detail not found with id: "
                                        + request.getBeneficiarySettlementDetailId()
                        ));

        ClaimantTypeMaster claimantType =
                claimantTypeMasterRepository.findById(request.getClaimantTypeId())
                        .orElseThrow(() -> new RuntimeException(
                                "Claimant type not found with id: " + request.getClaimantTypeId()
                        ));

        MemberNominee nominee = null;
        if (request.getNomineeId() != null) {
            nominee = memberNomineeRepository.findById(request.getNomineeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Nominee not found with id: " + request.getNomineeId()
                    ));
        }

        MemberFamily dependent = null;
        if (request.getDependentId() != null) {
            dependent = memberFamilyRepository.findById(request.getDependentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Dependent not found with id: " + request.getDependentId()
                    ));
        }

        RelationType relationshipType = null;
        if (request.getRelationshipTypeId() != null) {
            relationshipType = relationTypeRepository.findById(request.getRelationshipTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Relationship type not found with id: " + request.getRelationshipTypeId()
                    ));
        }

        PayeeTypeMaster payeeType = null;
        if (request.getPayeeTypeId() != null) {
            payeeType = payeeTypeMasterRepository.findById(request.getPayeeTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Payee type not found with id: " + request.getPayeeTypeId()
                    ));
        }

        BeneficiaryClaimantDetail entity = BeneficiaryClaimantDetail.builder()
                .beneficiarySettlementDetail(settlementDetail)
                .nominee(nominee)
                .dependent(dependent)
                .claimantType(claimantType)
                .relationshipType(relationshipType)
                .beneficiaryIdentifier(request.getBeneficiaryIdentifier())
                .beneficiaryName(request.getBeneficiaryName())
                .dateOfBirth(request.getDateOfBirth())
                .beneficiarySharePercentage(request.getBeneficiarySharePercentage())
                .payeeType(payeeType)
                .priorityOrder(request.getPriorityOrder())
                .isMemberFamily(defaultEnum(request.getIsMemberFamily(), ActivityEnum.N))
                .isMinor(defaultEnum(request.getIsMinor(), ActivityEnum.N))
                .isEligible(defaultEnum(request.getIsEligible(), ActivityEnum.Y))
                .isSelected(defaultEnum(request.getIsSelected(), ActivityEnum.Y))
                .guardianName(request.getGuardianName())
                .guardianIdentifier(request.getGuardianIdentifier())
                .benefitAmount(request.getBenefitAmount())
                .remarks(request.getRemarks())
                .build();

        BeneficiaryClaimantDetail saved = beneficiaryClaimantDetailRepository.save(entity);

        return ApiResponseDTO.created(
                beneficiaryClaimantDetailMapper.toDto(saved)
        );
    }

    private ActivityEnum defaultEnum(ActivityEnum value, ActivityEnum defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    public ApiResponseDTO<BeneficiaryClaimantResponseDto> update(
            Long id,
            BeneficiaryClaimantRequestDto request) {

        BeneficiaryClaimantDetail entity =
                beneficiaryClaimantDetailRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Beneficiary claimant detail not found with id: " + id
                        ));

        if (request.getBeneficiarySettlementDetailId() != null) {
            BeneficiarySettlementDetail settlementDetail =
                    beneficiarySettlementDetailRepository.findById(request.getBeneficiarySettlementDetailId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Beneficiary settlement detail not found with id: "
                                            + request.getBeneficiarySettlementDetailId()
                            ));
            entity.setBeneficiarySettlementDetail(settlementDetail);
        }

        if (request.getClaimantTypeId() != null) {
            ClaimantTypeMaster claimantType =
                    claimantTypeMasterRepository.findById(request.getClaimantTypeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Claimant type not found with id: " + request.getClaimantTypeId()
                            ));
            entity.setClaimantType(claimantType);
        }

        if (request.getNomineeId() != null) {
            MemberNominee nominee =
                    memberNomineeRepository.findById(request.getNomineeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Nominee not found with id: " + request.getNomineeId()
                            ));
            entity.setNominee(nominee);
        }

        if (request.getDependentId() != null) {
            MemberFamily dependent =
                    memberFamilyRepository.findById(request.getDependentId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Dependent not found with id: " + request.getDependentId()
                            ));
            entity.setDependent(dependent);
        }

        if (request.getRelationshipTypeId() != null) {
            RelationType relationshipType =
                    relationTypeRepository.findById(request.getRelationshipTypeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Relationship type not found with id: " + request.getRelationshipTypeId()
                            ));
            entity.setRelationshipType(relationshipType);
        }

        if (request.getPayeeTypeId() != null) {
            PayeeTypeMaster payeeType =
                    payeeTypeMasterRepository.findById(request.getPayeeTypeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Payee type not found with id: " + request.getPayeeTypeId()
                            ));
            entity.setPayeeType(payeeType);
        }

        entity.setBeneficiaryIdentifier(request.getBeneficiaryIdentifier());
        entity.setBeneficiaryName(request.getBeneficiaryName());
        entity.setDateOfBirth(request.getDateOfBirth());
        entity.setBeneficiarySharePercentage(request.getBeneficiarySharePercentage());
        entity.setPriorityOrder(request.getPriorityOrder());
        entity.setIsMemberFamily(defaultEnum(request.getIsMemberFamily(), ActivityEnum.N));
        entity.setIsMinor(defaultEnum(request.getIsMinor(), ActivityEnum.N));
        entity.setIsEligible(defaultEnum(request.getIsEligible(), ActivityEnum.Y));
        entity.setIsSelected(defaultEnum(request.getIsSelected(), ActivityEnum.Y));
        entity.setGuardianName(request.getGuardianName());
        entity.setGuardianIdentifier(request.getGuardianIdentifier());
        entity.setBenefitAmount(request.getBenefitAmount());
        entity.setRemarks(request.getRemarks());

        BeneficiaryClaimantDetail updated = beneficiaryClaimantDetailRepository.save(entity);

        return ApiResponseDTO.success(
                "Beneficiary claimant detail updated successfully",
                beneficiaryClaimantDetailMapper.toDto(updated)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<BeneficiaryClaimantResponseDto> getById(Long id) {

        BeneficiaryClaimantDetail entity =
                beneficiaryClaimantDetailRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Beneficiary claimant detail not found with id: " + id
                        ));

        return ApiResponseDTO.success(
                "Beneficiary claimant detail fetched successfully",
                beneficiaryClaimantDetailMapper.toDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BeneficiaryClaimantResponseDto>> getByBeneficiarySettlementDetailId(
            Long beneficiarySettlementDetailId) {

        List<BeneficiaryClaimantDetail> entities =
                beneficiaryClaimantDetailRepository
                        .findByBeneficiarySettlementDetail_Id(beneficiarySettlementDetailId);

        return ApiResponseDTO.success(
                "Beneficiary claimant details fetched successfully",
                beneficiaryClaimantDetailMapper.toDtoList(entities)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BeneficiaryClaimantResponseDto>> getAll() {

        List<BeneficiaryClaimantDetail> entities =
                beneficiaryClaimantDetailRepository.findAll();

        return ApiResponseDTO.success(
                "Beneficiary claimant details fetched successfully",
                beneficiaryClaimantDetailMapper.toDtoList(entities)
        );
    }

    @Override
    public ApiResponseDTO<Void> delete(Long id) {

        BeneficiaryClaimantDetail entity =
                beneficiaryClaimantDetailRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Beneficiary claimant detail not found with id: " + id
                        ));

        beneficiaryClaimantDetailRepository.delete(entity);

        return ApiResponseDTO.success(
                "Beneficiary claimant detail deleted successfully",
                null
        );
    }
}