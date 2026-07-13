package com.claim.claim_processing.application.service.claimDetail.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
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
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimAccountingEvent;
import com.claim.claim_processing.common.entities.claim.ClaimLedgerEntry;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.common.CoaMainAccount;
import com.claim.claim_processing.common.entities.common.CoaSubAccount;
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
import com.claim.claim_processing.common.repository.claim.ClaimAccountingEventRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaMainAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaSubAccountRepository;
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

    private final CoaMainAccountRepository coaMainAccountRepository;
    private final ClaimAccountingEventRepository claimAccountingEventRepository;
    private final CoaSubAccountRepository coaSubAccountRepository;

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

    // public Page<GeneralSpecialCaseResponse> getAllApprovedSpecialCases(Pageable pageable) {

    // }

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


    @Override
    public ApiResponseDTO<Page<GeneralSpecialCaseResponse>> getAllApprovedSpecialCases(Pageable pageable) {

        Page<ClaimSpecialCase> specialCasePage = specialCaseRepository.findAll(pageable);

        List<GeneralSpecialCaseResponse> responses = specialCasePage.getContent().stream()
                    .map(specialCase -> {
                        try {
                            // Get bank details
                            List<ClaimBankDetail> bankDetails = claimBankDetailRepository
                                    .findByClaimDetail_Id(specialCase.getClaimDetail().getId());
                            ClaimBankDetail bankDetail = bankDetails.isEmpty() ? null : bankDetails.get(0);
                            
                            // Map to response
                            GeneralSpecialCaseResponse response =  generalSpecialCaseMapper.mapToGeneralSpecialCaseResponse(
                                    specialCase.getClaimDetail(), 
                                    specialCase, 
                                    bankDetail
                            );
                            response.setAccountingEventDetail(specialCase.getClaimDetail() != null ? mapAccountingEvent(specialCase.getClaimDetail()) : null);
                            return response;
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            // Create page with responses
            Page<GeneralSpecialCaseResponse> responsePage = new PageImpl<>(
                    responses,
                    pageable,
                    specialCasePage.getTotalElements()
            );
            
            return ApiResponseDTO.success(responsePage);
    }

    private AccountingEventResponseDto mapAccountingEvent(ClaimDetail claimDetail) {
    ClaimAccountingEvent accountingEvent = claimAccountingEventRepository.findByClaimDetailId(claimDetail.getId()).orElse(null);
if (accountingEvent == null) {
        return null;
    }
    return AccountingEventResponseDto.builder()
            .id(accountingEvent.getId())
            .eventType(accountingEvent.getEventType())
            .claimDetailId(accountingEvent.getClaimDetailId())
            .claimApplicationNumber(accountingEvent.getClaimApplicationNumber())
            .nppfNumber(accountingEvent.getNppfNumber())
            .identityNumber(accountingEvent.getIdentityNumber())
            .memberName(accountingEvent.getMemberName())
            .agencyCategoryId(accountingEvent.getAgencyCategoryId())
            .agencyCode(accountingEvent.getAgencyCode())
            .agencyName(accountingEvent.getAgencyName())
            .tranCode(accountingEvent.getTranCode())
            .status(accountingEvent.getStatus())
            .totalDr(accountingEvent.getTotalDr())
            .totalCr(accountingEvent.getTotalCr())
            .narration(accountingEvent.getNarration())
            .postedBy(accountingEvent.getPostedBy())
            .postedAt(accountingEvent.getPostedAt())
            .createdBy(accountingEvent.getCreatedBy())
            .createdAt(accountingEvent.getCreatedAt())
            .updatedBy(accountingEvent.getUpdatedBy())
            .updatedAt(accountingEvent.getUpdatedAt())
            .ledgerEntries(mapLedgerEntries(accountingEvent.getLedgerEntries()))
            .build();
}

private List<AccountingEventResponseDto.LedgerEntryResponseDto> mapLedgerEntries(List<ClaimLedgerEntry> ledgerEntries) {
    if (ledgerEntries == null || ledgerEntries.isEmpty()) {
        return null;
    }
    return ledgerEntries.stream()
            .filter(Objects::nonNull)
            .map(entry -> {
                CoaMainAccount main = coaMainAccountRepository.findByAccountCode(entry.getMainAccountCode()).orElse(null);
                CoaSubAccount sub = coaSubAccountRepository.findBySubAccountCode(entry.getSubAccountCode()).orElse(null);
                return AccountingEventResponseDto.LedgerEntryResponseDto.builder()
                        .id(entry.getId())
                        .seqNo(entry.getSeqNo())
                        .mainAccountCode(entry.getMainAccountCode())
                        .mainAccountName(main.getAccountName()) // Will need to fetch from COA table if needed
                        .subAccountCode(entry.getSubAccountCode())
                        .subAccountName(sub.getSubAccountName()) // Will need to fetch from COA table if needed
                        .drcr(entry.getDrcr())
                        .amount(entry.getAmount())
                        .entryRole(entry.getEntryRole())
                        .componentCode(entry.getComponentCode())
                        .narration(entry.getNarration())
                        .createdBy(entry.getCreatedBy())
                        .createdAt(entry.getCreatedAt())
                        .build();
            })
            .collect(Collectors.toList());
}
    

    private StatusMaster getStatusMaster(Long statusId) {
        return statusMasterRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));
    }
}
