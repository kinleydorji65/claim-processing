package com.claim.claim_processing.application.service.claimDetail.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto.SpecialCaseComponentBalanceResponseDTO;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse;
import com.claim.claim_processing.application.entity.claimDetail.ClaimAccountingEvent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerEntry;
import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCase;
import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCaseComponentDetail;
import com.claim.claim_processing.application.mapper.claimDetail.ClaimSpecialCaseMapper;
import com.claim.claim_processing.application.mapper.claimDetail.GeneralSpecialCaseMapper;
import com.claim.claim_processing.application.repository.claimDetail.ClaimAccountingEventRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimBankDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimSpecialCaseComponentDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.SpecialCaseRepository;
import com.claim.claim_processing.application.service.claimDetail.SpecialCaseService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.CoaMainAccount;
import com.claim.claim_processing.common.entities.common.CoaSubAccount;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.BankType;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaMainAccountRepository;
import com.claim.claim_processing.common.repository.common.CoaSubAccountRepository;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.repository.common.SubmissionChannelRepository;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.repository.others.BankTypeRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;

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
    private final SchemeTypeRepository schemeTypeRepository;
    private final StatusMasterRepository statusMasterRepository;

    private final ClaimantTypeRepository claimantTypeRepository;
    private final BankTypeRepository bankTypeRepository;
    private final StageRepository stageRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;
    private final ReserveAccountRepository reserveAccountRepository;
    private final GeneralSpecialCaseMapper generalSpecialCaseMapper;

    private final CoaMainAccountRepository coaMainAccountRepository;
    private final ClaimAccountingEventRepository claimAccountingEventRepository;
    private final CoaSubAccountRepository coaSubAccountRepository;
    private final ClaimSpecialCaseComponentDetailRepository claimSpecialCaseComponentDetailRepository;
    private final ComponentMasterRepository componentMasterRepository;

    @Override
    @Transactional  // ✅ ADD THIS
    public GeneralSpecialCaseResponse createSpecialCase(
            GeneralSpecialCaseApplicationResponseDTO request) {
        
        System.out.println("========== START: createSpecialCase ==========");
        
        try {
            // 1. Create and save ClaimDetail
            ClaimDetail claimDetail = claimSpecialCaseMapper.toClaimDetailEntity(request);
            setClaimDetailReferences(claimDetail, request);
            claimDetail = claimDetailRepository.saveAndFlush(claimDetail);
            System.out.println("ClaimDetail saved with ID: " + claimDetail.getId());

            // 2. Create and save ClaimSpecialCase
            ClaimSpecialCase specialCase = claimSpecialCaseMapper
                    .toEntity(request.getClaimSpecialCaseApplicationResponseDto());
            specialCase.setClaimDetail(claimDetail);
            specialCase = specialCaseRepository.saveAndFlush(specialCase);
            System.out.println("ClaimSpecialCase saved with ID: " + specialCase.getId());

            // 3. ✅ CHECK COMPONENTS FROM REQUEST
            List<SpecialCaseComponentBalanceResponseDTO> requestComponents = null;
            if (request.getClaimSpecialCaseApplicationResponseDto() != null) {
                requestComponents = request.getClaimSpecialCaseApplicationResponseDto().getComponents();
            }
            
            System.out.println("========== COMPONENTS FROM REQUEST ==========");
            if (requestComponents == null) {
                System.out.println("❌ requestComponents is NULL!");
            } else if (requestComponents.isEmpty()) {
                System.out.println("❌ requestComponents is EMPTY!");
            } else {
                System.out.println("✅ Found " + requestComponents.size() + " components in request");
                for (SpecialCaseComponentBalanceResponseDTO comp : requestComponents) {
                    System.out.println("  Component: code=" + comp.getCode() + 
                        ", name=" + comp.getName() + 
                        ", amount=" + comp.getAmount());
                }
            }

            // 4. ✅ Save component details
            saveSpecialCaseComponentDetails(specialCase, requestComponents, specialCase.getCreatedBy());
            System.out.println("Component details saved");

            // 5. ✅ VERIFY COMPONENTS WERE SAVED
            List<ClaimSpecialCaseComponentDetail> savedComponents = 
                claimSpecialCaseComponentDetailRepository.findBySpecialCase_Id(specialCase.getId());
            
            System.out.println("========== VERIFY SAVED COMPONENTS ==========");
            if (savedComponents == null || savedComponents.isEmpty()) {
                System.out.println("❌ NO components saved in database for special case: " + specialCase.getId());
            } else {
                System.out.println("✅ Found " + savedComponents.size() + " components in database");
                for (ClaimSpecialCaseComponentDetail detail : savedComponents) {
                    System.out.println("  Component: code=" + 
                        (detail.getComponentMaster() != null ? detail.getComponentMaster().getCode() : "null") + 
                        ", amount=" + detail.getAmount());
                }
            }

            // 6. ✅ Save bank details
            List<ClaimBankDetail> bankDetails = saveBankDetails(List.of(request.getBankDetail()), claimDetail);
            System.out.println("Bank details saved");

            // 7. ✅ REFRESH specialCase to load components
            specialCase = specialCaseRepository.findById(specialCase.getId())
                    .orElseThrow(() -> new RuntimeException("Special case not found after saving components"));
            
            // 8. ✅ Load components and set them on specialCase
            List<ClaimSpecialCaseComponentDetail> componentDetails = 
                claimSpecialCaseComponentDetailRepository.findBySpecialCase_Id(specialCase.getId());
            
            System.out.println("Manually loaded " + componentDetails.size() + " components for special case: " + specialCase.getId());

            // Modify existing collection instead of replacing
            if (specialCase.getComponentDetails() == null) {
                specialCase.setComponentDetails(new ArrayList<>());
            } else {
                specialCase.getComponentDetails().clear();
            }
            specialCase.getComponentDetails().addAll(componentDetails);

            // 9. ✅ Map to response
            GeneralSpecialCaseResponse generalSpecialCaseResponse = generalSpecialCaseMapper
                    .mapToGeneralSpecialCaseResponse(claimDetail, specialCase, bankDetails.get(0));
            
            // 10. ✅ FORCE SET COMPONENTS IN RESPONSE
            if (generalSpecialCaseResponse.getSpecialCaseDetail() == null) {
                generalSpecialCaseResponse.setSpecialCaseDetail(new ClaimSpecialCaseResponse());
            }
            
            // Set other fields
            ClaimSpecialCaseResponse specialCaseDetail = generalSpecialCaseResponse.getSpecialCaseDetail();
            specialCaseDetail.setId(specialCase.getId());
            specialCaseDetail.setClaimDetailId(claimDetail.getId());
            // if (specialCase.getCaseReason() != null) {
            //     specialCaseDetail.setCaseReasonId(specialCase.getCaseReason().getId());
            // }
            specialCaseDetail.setApprovedBy(specialCase.getApprovedBy());
            specialCaseDetail.setApprovedDate(specialCase.getApprovedDate());
            specialCaseDetail.setIsActive(specialCase.getIsActive());
            specialCaseDetail.setCreatedBy(specialCase.getCreatedBy());
            specialCaseDetail.setCreatedAt(specialCase.getCreatedAt());
            specialCaseDetail.setUpdatedBy(specialCase.getUpdatedBy());
            specialCaseDetail.setUpdatedAt(specialCase.getUpdatedAt());
            
            // Convert components to DTOs
            List<ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO> componentDTOs = 
                componentDetails.stream()
                .map(detail -> {
                    ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO dto = 
                        new ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO();
                    dto.setId(detail.getId());
                    if (detail.getComponentMaster() != null) {
                        dto.setCode(detail.getComponentMaster().getCode());
                        dto.setName(detail.getComponentMaster().getName());
                    }
                    dto.setAmount(detail.getAmount());
                    return dto;
                })
                .filter(dto -> dto.getCode() != null)
                .collect(Collectors.toList());
            
            specialCaseDetail.setComponents(componentDTOs);
            
            // Calculate total
            BigDecimal total = componentDTOs.stream()
                .map(ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            specialCaseDetail.setTotalAmount(total);
            
            System.out.println("✅ FINAL RESPONSE HAS " + componentDTOs.size() + " COMPONENTS");
            
            System.out.println("========== END: createSpecialCase ==========");
            return generalSpecialCaseResponse;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR in createSpecialCase: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create special case: " + e.getMessage(), e);
        }
    }

    private void saveSpecialCaseComponentDetails(
            ClaimSpecialCase specialCase,
            List<SpecialCaseComponentBalanceResponseDTO> components,
            String createdBy) {

        System.out.println("========== saveSpecialCaseComponentDetails ==========");
        System.out.println("specialCase ID: " + (specialCase != null ? specialCase.getId() : "null"));
        System.out.println("components: " + (components != null ? components.size() : "null"));

        if (components == null || components.isEmpty()) {
            System.out.println("❌ No components to save for special case");
            return;
        }

        List<ClaimSpecialCaseComponentDetail> componentDetails = new ArrayList<>();

        for (SpecialCaseComponentBalanceResponseDTO componentDto : components) {
            System.out.println("  Processing component: code=" + componentDto.getCode() + 
                ", amount=" + componentDto.getAmount());
            
            if (componentDto == null || componentDto.getCode() == null) {
                System.out.println("  ⚠️ Skipping null component");
                continue;
            }

            ComponentMaster componentMaster = componentMasterRepository
                    .findByCode(componentDto.getCode())
                    .orElse(null);

            if (componentMaster == null) {
                System.out.println("  ⚠️ ComponentMaster not found for code: " + componentDto.getCode());
            } else {
                System.out.println("  ✅ Found ComponentMaster: " + componentMaster.getCode());
            }

            ClaimSpecialCaseComponentDetail componentDetail = ClaimSpecialCaseComponentDetail.builder()
                    .specialCase(specialCase)
                    .componentMaster(componentMaster)
                    .amount(componentDto.getAmount() != null ? componentDto.getAmount() : BigDecimal.ZERO)
                    .isActive("Y")
                    .createdBy(createdBy != null ? createdBy : specialCase.getCreatedBy())
                    .build();

            componentDetails.add(componentDetail);
            System.out.println("  ✅ Added component detail for: " + componentDto.getCode());
        }

            System.out.println("Saving " + componentDetails.size() + " component details");
            claimSpecialCaseComponentDetailRepository.saveAllAndFlush(componentDetails);
            
    }

    private void setClaimDetailReferences(ClaimDetail claimDetail,
            GeneralSpecialCaseApplicationResponseDTO requestResponse) {

        if (requestResponse.getClaimTypeId() != null && requestResponse.getClaimTypeId() > 0) {
            ClaimTypeMaster claimTypeMaster = claimTypeMasterRepository.findById(requestResponse.getClaimTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Claim Type not found with ID: " + requestResponse.getClaimTypeId()));
            claimDetail.setClaimType(claimTypeMaster);
        }

        if (requestResponse.getSubmissionChannelId() != null) {
            SubmissionChannelMaster submissionChannelMaster = submissionChannelMasterRepository
                    .findById(requestResponse.getSubmissionChannelId())
                    .orElseThrow(() -> new RuntimeException(
                            "Submission Channel not found with ID: " + requestResponse.getSubmissionChannelId()));
            claimDetail.setSubmissionChannel(submissionChannelMaster);
        }

        if (requestResponse.getMemberCategoryId() != null) {
            AgencyCategory agencyCategory = agencyCategoryRepository.findById(requestResponse.getMemberCategoryId())
                    .orElseThrow(() -> new RuntimeException(
                            "Agency Category not found with ID: " + requestResponse.getMemberCategoryId()));
            claimDetail.setMemberCategory(agencyCategory);
        }

        if (requestResponse.getCurrentStageId() != null) {
            StageMaster stageMaster = stageRepository.findById(requestResponse.getCurrentStageId())
                    .orElseThrow(() -> new RuntimeException(
                            "Stage not found with ID: " + requestResponse.getCurrentStageId()));
            claimDetail.setCurrentStage(stageMaster);
        }

        if (requestResponse.getSchemeTypeId() != null) {
            SchemeType schemeType = schemeTypeRepository.findById(requestResponse.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Scheme Type not found with ID: " + requestResponse.getSchemeTypeId()));
            claimDetail.setSchemeType(schemeType);
        }

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
                    if (bankDetailResponse.getClaimantTypeId() != null && bankDetailResponse.getClaimantTypeId() > 0) {
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
                        List<ClaimBankDetail> bankDetails = claimBankDetailRepository
                                .findByClaimDetail_Id(specialCase.getClaimDetail().getId());
                        ClaimBankDetail bankDetail = bankDetails.isEmpty() ? null : bankDetails.get(0);

                        GeneralSpecialCaseResponse response = generalSpecialCaseMapper.mapToGeneralSpecialCaseResponse(
                                specialCase.getClaimDetail(),
                                specialCase,
                                bankDetail);
                        response.setAccountingEventDetail(
                                specialCase.getClaimDetail() != null ? mapAccountingEvent(specialCase.getClaimDetail())
                                        : null);
                        return response;
                    } catch (Exception e) {
                        System.out.println("Error mapping special case: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Page<GeneralSpecialCaseResponse> responsePage = new PageImpl<>(
                responses,
                pageable,
                specialCasePage.getTotalElements());

        return ApiResponseDTO.success(responsePage);
    }

    private AccountingEventResponseDto mapAccountingEvent(ClaimDetail claimDetail) {
        ClaimAccountingEvent accountingEvent = claimAccountingEventRepository.findByClaimDetail_Id(claimDetail.getId())
                .orElse(null);
        if (accountingEvent == null) {
            return null;
        }
        return AccountingEventResponseDto.builder()
                .id(accountingEvent.getId())
                .eventType(accountingEvent.getEventType())
                .claimDetailId(claimDetail.getId())
                .claimApplicationNumber(accountingEvent.getClaimApplicationNumber())
                .nppfNumber(accountingEvent.getNppfNumber())
                .identityNumber(accountingEvent.getIdentityNumber())
                .memberName(accountingEvent.getMemberName())
                .agencyCategoryId(accountingEvent.getAgencyCategoryId())
                .agencyCode(accountingEvent.getAgencyCode())
                .agencyName(accountingEvent.getAgencyName())
                .status(accountingEvent.getStatus())
                .postedBy(accountingEvent.getPostedBy())
                .postedAt(accountingEvent.getPostedAt())
                .createdBy(accountingEvent.getCreatedBy())
                .createdAt(accountingEvent.getCreatedAt())
                .updatedBy(accountingEvent.getUpdatedBy())
                .updatedAt(accountingEvent.getUpdatedAt())
                .ledgerEntries(mapLedgerEntries(accountingEvent.getLedgerEntries()))
                .build();
    }

    private List<AccountingEventResponseDto.LedgerEntryResponseDto> mapLedgerEntries(
            List<ClaimLedgerEntry> ledgerEntries) {
        if (ledgerEntries == null || ledgerEntries.isEmpty()) {
            return null;
        }
        return ledgerEntries.stream()
                .filter(Objects::nonNull)
                .map(entry -> {
                    CoaMainAccount main = coaMainAccountRepository.findByAccountCode(entry.getMainAccountCode())
                            .orElse(null);
                    CoaSubAccount sub = coaSubAccountRepository.findBySubAccountCode(entry.getSubAccountCode())
                            .orElse(null);
                    return AccountingEventResponseDto.LedgerEntryResponseDto.builder()
                            .id(entry.getId())
                            .seqNo(entry.getSeqNo())
                            .mainAccountCode(entry.getMainAccountCode())
                            .mainAccountName(main != null ? main.getAccountName() : null)
                            .subAccountCode(entry.getSubAccountCode())
                            .subAccountName(sub != null ? sub.getSubAccountName() : null)
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