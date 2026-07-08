package com.claim.claim_processing.application.service.claimDetail.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCase;
import com.claim.claim_processing.application.mapper.claimDetail.ClaimSpecialCaseMapper;
import com.claim.claim_processing.application.mapper.claimDetail.GeneralSpecialCaseMapper;
import com.claim.claim_processing.application.repository.claimDetail.ClaimBankDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.SpecialCaseRepository;
import com.claim.claim_processing.application.service.claimDetail.SpecialCaseService;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.BankType;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.pension.PensionDetail;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.repository.common.SubmissionChannelRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.repository.others.BankTypeRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.repository.pension.PensionDetailRepository;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseAuthorityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialCaseServiceImpl implements SpecialCaseService {
    private final ClaimSpecialCaseMapper claimSpecialCaseMapper;
    private final ClaimDetailRepository claimDetailRepository;
    private final SpecialCaseRepository specialCaseRepository;
    private final ClaimBankDetailRepository claimBankDetailRepository;


    private final ClaimTypeMasterRepository claimTypeMasterRepository;
    private final SubmissionChannelRepository submissionChannelMasterRepository;
    private final SpecialCaseAuthorityRepository specialCaseAuthorityRepository;
    private final SchemeTypeRepository schemeTypeRepository;
    private final StatusMasterRepository statusMasterRepository;


    private final ClaimantTypeRepository claimantTypeRepository;
    private final BankTypeRepository bankTypeRepository;
    private final StageRepository stageRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;
    private final PensionDetailRepository pensionDetailRepository;
    private final ReserveAccountRepository reserveAccountRepository;
    private final GeneralSpecialCaseMapper generalSpecialCaseMapper;

    @Override
    public GeneralSpecialCaseResponse createSpecialCase(
            GeneralSpecialCaseApplicationResponseDTO request) {
        ClaimDetail claimDetail = claimSpecialCaseMapper.toClaimDetailEntity(request);
        setClaimDetailReferences(claimDetail, request);
        claimDetail = claimDetailRepository.saveAndFlush(claimDetail);
        ClaimSpecialCase specialCase = claimSpecialCaseMapper.toEntity(request.getClaimSpecialCaseApplicationResponseDto());
        specialCase.setClaimDetail(claimDetail);
        if (request.getClaimSpecialCaseApplicationResponseDto().getPensionAccountId() != null && request.getClaimSpecialCaseApplicationResponseDto().getPensionAccountId() > 0) {
            PensionDetail pensionDetail = pensionDetailRepository
                    .findById(request.getClaimSpecialCaseApplicationResponseDto().getPensionAccountId())
                    .orElseThrow(() -> new RuntimeException("Pension Detail not found with ID: "
                            + request.getClaimSpecialCaseApplicationResponseDto().getPensionAccountId()));
                            specialCase.setPensionType(pensionDetail.getPensionType());
        }
        if (request.getClaimSpecialCaseApplicationResponseDto().getReserveAccountId() != null && request.getClaimSpecialCaseApplicationResponseDto().getReserveAccountId() > 0) {
            ReserveAccount reserveAccount = reserveAccountRepository
                    .findById(request.getClaimSpecialCaseApplicationResponseDto().getReserveAccountId())
                    .orElseThrow(() -> new RuntimeException("Reserve Account not found with ID: "
                            + request.getClaimSpecialCaseApplicationResponseDto().getReserveAccountId()));
            specialCase.setReserveAccount(reserveAccount);
        }
        specialCaseRepository.saveAndFlush(specialCase);
        List<ClaimBankDetail> bankDetails = saveBankDetails(List.of(request.getBankDetail()), claimDetail);
        GeneralSpecialCaseResponse generalSpecialCaseResponse = generalSpecialCaseMapper.mapToGeneralSpecialCaseResponse(claimDetail, specialCase, bankDetails.get(0));
        return generalSpecialCaseResponse;
    }

    private void setClaimDetailReferences(ClaimDetail claimDetail,
            GeneralSpecialCaseApplicationResponseDTO requestResponse) {

        if (requestResponse.getClaimTypeId() != null && requestResponse.getClaimTypeId() > 0) {
            ClaimTypeMaster claimTypeMaster = claimTypeMasterRepository.findById(requestResponse.getClaimTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Claim Type not found with ID: " + requestResponse.getClaimTypeId()));
            claimDetail.setClaimType(claimTypeMaster);
        }

        // 2. Set references (FIXED: Don't throw exceptions for null IDs)
        if (requestResponse.getSubmissionChannelId() != null) {
            SubmissionChannelMaster submissionChannelMaster = submissionChannelMasterRepository
                    .findById(requestResponse.getSubmissionChannelId())
                    .orElseThrow(() -> new RuntimeException(
                            "Submission Channel not found with ID: " + requestResponse.getSubmissionChannelId()));
            claimDetail.setSubmissionChannel(submissionChannelMaster);
        }
        // Set Agency Category
        if (requestResponse.getMemberCategoryId() != null) {
            AgencyCategory agencyCategory = agencyCategoryRepository.findById(requestResponse.getMemberCategoryId())
                    .orElseThrow(() -> new RuntimeException(
                            "Agency Category not found with ID: " + requestResponse.getMemberCategoryId()));
            claimDetail.setMemberCategory(agencyCategory);
        }

        // Set Special Case Authority (handle null)
        if (requestResponse.getSpecialCaseAuthorityId() != null) {
            SpecialCaseRefundAuthorityMaster specialCaseRefundAuthorityMaster = specialCaseAuthorityRepository
                    .findById(requestResponse.getSpecialCaseAuthorityId())
                    .orElseThrow(() -> new RuntimeException("Special Case Refund Authority not found with ID: "
                            + requestResponse.getSpecialCaseAuthorityId()));
            claimDetail.setSpecialCaseAuthority(specialCaseRefundAuthorityMaster);
        }

        // Set Stage
        if (requestResponse.getCurrentStageId() != null) {
            StageMaster stageMaster = stageRepository.findById(requestResponse.getCurrentStageId())
                    .orElseThrow(() -> new RuntimeException(
                            "Stage not found with ID: " + requestResponse.getCurrentStageId()));
            claimDetail.setCurrentStage(stageMaster);
        }

        // Set Scheme Type
        if (requestResponse.getSchemeTypeId() != null) {
            SchemeType schemeType = schemeTypeRepository.findById(requestResponse.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Scheme Type not found with ID: " + requestResponse.getSchemeTypeId()));
            claimDetail.setSchemeType(schemeType);
        }

        // Set Status
        if (requestResponse.getStatusId() != null) {
            claimDetail.setStatus(getStatusMaster(requestResponse.getStatusId()));
        }
    }

    private List<ClaimBankDetail> saveBankDetails(List<ClaimApplicationBankResponseDto> bankDetails,
            ClaimDetail claimDetail) {
        if (bankDetails == null || bankDetails.isEmpty()) {
            return List.of();
        }

        List<ClaimBankDetail> claimBankDetails = bankDetails.stream()
                .filter(Objects::nonNull)
                .map(bankDetailResponse -> {
                    ClaimBankDetail bankDetail = claimSpecialCaseMapper.toBankDetailEntity(bankDetailResponse);
                    BankType bankType = bankTypeRepository.findByBankTypeId(bankDetailResponse.getBankTypeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Bank Type not found with ID: " + bankDetailResponse.getBankTypeId()));
                    if(bankDetailResponse.getClaimantTypeId() != null && bankDetailResponse.getClaimantTypeId() > 0) {
                        ClaimantTypeMaster claimantTypeMaster = claimantTypeRepository
                            .findById(bankDetailResponse.getClaimantTypeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Claimant Type not found with ID: " + bankDetailResponse.getClaimantTypeId()));
                        bankDetail.setClaimantType(claimantTypeMaster);
                    }
                    
                    bankDetail.setClaimDetail(claimDetail);
                    bankDetail.setBankType(bankType);
                    return bankDetail;
                })
                .toList();

        if (!claimBankDetails.isEmpty()) {
            claimBankDetailRepository.saveAllAndFlush(claimBankDetails);
        }
        return claimBankDetails;
    }



    private StatusMaster getStatusMaster(Long statusId) {
        return statusMasterRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));
    }
}
