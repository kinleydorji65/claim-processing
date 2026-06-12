package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;
import com.claim.claim_processing.application.mapper.detail.NormalClaimMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.detail.NormalClaimDetailRepository;
import com.claim.claim_processing.application.service.detail.NormalClaimService;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.TerminationReasonMaster;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.TerminationReasonRepository;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NormalClaimServiceImpl implements NormalClaimService {

    private final NormalClaimDetailRepository normalClaimDetailRepository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final CessationTypeRepository cessationTypeMasterRepository;
    private final PayeeTypeRepository payeeTypeMasterRepository;
    private final TerminationReasonRepository terminationReasonMasterRepository;
    private final NormalClaimMapper normalClaimMapper;

    @Override
    public NormalClaimDetail create(ClaimApplication claimApplication, NormalClaimRequestDto request) {

        validateRequired(request);

        if (normalClaimDetailRepository.existsByClaimApplication_Id(claimApplication.getId())) {
            throw ClaimException.conflict(
                    "Normal claim detail already exists for claim application id: "
                            + claimApplication.getId()
            );
        }

        CessationTypeMaster cessationType = getCessationType(request.getCessationTypeId());
        PayeeTypeMaster payeeType = getPayeeType(request.getPayeeTypeId());

        validateByCessationType(request, cessationType.getCode());

        NormalClaimDetail entity = normalClaimMapper.toEntity(request);

        entity.setClaimApplication(claimApplication);
        entity.setCessationType(cessationType);
        entity.setPayeeType(payeeType);
        entity.setTerminationReasonType(getTerminationReasonIfPresent(request.getTerminationReasonTypeId()));

        NormalClaimDetail saved = normalClaimDetailRepository.save(entity);

        return saved;
    }

    @Override
    public NormalClaimDetail update(ClaimApplication claimApplication, NormalClaimRequestDto request) {

        NormalClaimDetail existing = normalClaimDetailRepository.findById(request.getNormalClaimId())
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Normal claim detail",
                        request.getNormalClaimId().toString()
                ));

        if (claimApplication != null) {
            boolean duplicate = normalClaimDetailRepository
                    .existsByClaimApplication_IdAndIdNot(claimApplication.getId(), request.getNormalClaimId());

            if (duplicate) {
                throw ClaimException.conflict(
                        "Normal claim detail already exists for claim application id: "
                                + claimApplication.getId()
                );
            }
        }
        normalClaimMapper.updateEntityFromDto(request, existing);

        if (claimApplication != null) {
            existing.setClaimApplication(claimApplication);
        }

        if (request.getCessationTypeId() != null || request.getCessationTypeId() > 0) {
            CessationTypeMaster cessationType = getCessationType(request.getCessationTypeId());
            existing.setCessationType(cessationType);
            validateByCessationType(request, cessationType.getCode());
        } else if (existing.getCessationType() != null) {
            validateByCessationTypeForUpdate(existing);
        }

        if (request.getPayeeTypeId() != null || request.getPayeeTypeId() > 0) {
            existing.setPayeeType(getPayeeType(request.getPayeeTypeId()));
        }

        if (request.getTerminationReasonTypeId() != null || request.getTerminationReasonTypeId() > 0) {
            existing.setTerminationReasonType(getTerminationReasonIfPresent(request.getTerminationReasonTypeId()));
        }

        NormalClaimDetail updated = normalClaimDetailRepository.save(existing);

        return updated;
    }

    private void validateRequired(NormalClaimRequestDto request) {

        if (request.getCessationTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "cessationTypeId",
                    "Cessation type is required"
            );
        }

        if (request.getPayeeTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "payeeTypeId",
                    "Payee type is required"
            );
        }

        if (request.getLastPayMonth() == null
                || request.getLastPayMonth().isBlank()) {

            throw ClaimException.singleValidationError(
                    "lastPayMonth",
                    "Last pay month is required"
            );
        }
    }

    private void validateByCessationType(NormalClaimRequestDto request, String cessationTypeCode) {

        if (cessationTypeCode == null || cessationTypeCode.isBlank()) {
            throw ClaimException.singleValidationError(
                    "cessationTypeId",
                    "Cessation type code is missing"
            );
        }

        String code = cessationTypeCode.trim().toUpperCase();

        if ("TERMINATION".equals(code)) {

            if (request.getDateOfTermination() == null) {
                throw ClaimException.singleValidationError(
                        "dateOfTermination",
                        "Date of termination is required"
                );
            }

            if (request.getCessationEffectiveDate() == null) {
                throw ClaimException.singleValidationError(
                        "cessationEffectiveDate",
                        "Cessation effective date is required"
                );
            }

            if (request.getTerminationReasonTypeId() == null) {
                throw ClaimException.singleValidationError(
                        "terminationReasonTypeId",
                        "Termination reason is required"
                );
            }

            if (request.getTerminatedBy() == null || request.getTerminatedBy().isBlank()) {
                throw ClaimException.singleValidationError(
                        "terminatedBy",
                        "Terminated By / Issued By is required"
                );
            }

            return;
        }

        if (isRetirementLike(code)) {

            if (request.getRelievingOrderNumber() == null || request.getRelievingOrderNumber().isBlank()) {
                throw ClaimException.singleValidationError(
                        "relievingOrderNumber",
                        "Relieving order number is required"
                );
            }

            if (request.getRelievingOrderDate() == null) {
                throw ClaimException.singleValidationError(
                        "relievingOrderDate",
                        "Relieving order date is required"
                );
            }

            if (request.getCessationEffectiveDate() == null) {
                throw ClaimException.singleValidationError(
                        "cessationEffectiveDate",
                        "Cessation effective date is required"
                );
            }

            return;
        }

        if (isExitLike(code)) {

            if (request.getCessationEffectiveDate() == null) {
                throw ClaimException.singleValidationError(
                        "cessationEffectiveDate",
                        "Cessation effective date is required"
                );
            }

            if (request.getRelievingReferenceNumber() == null || request.getRelievingReferenceNumber().isBlank()) {
                throw ClaimException.singleValidationError(
                        "relievingReferenceNumber",
                        "Relieving reference number is required"
                );
            }

            return;
        }

        if (request.getCessationEffectiveDate() == null) {
            throw ClaimException.singleValidationError(
                    "cessationEffectiveDate",
                    "Cessation effective date is required"
            );
        }
    }

    private void validateByCessationTypeForUpdate(NormalClaimDetail existing) {

        String code = existing.getCessationType().getCode();

        if (code == null || code.isBlank()) {
            throw ClaimException.singleValidationError(
                    "cessationTypeId",
                    "Cessation type code is missing"
            );
        }

        code = code.trim().toUpperCase();

        if ("TERMINATION".equals(code)) {

            if (existing.getDateOfTermination() == null) {
                throw ClaimException.singleValidationError("dateOfTermination", "Date of termination is required");
            }

            if (existing.getCessationEffectiveDate() == null) {
                throw ClaimException.singleValidationError("cessationEffectiveDate", "Cessation effective date is required");
            }

            if (existing.getTerminationReasonType() == null) {
                throw ClaimException.singleValidationError("terminationReasonTypeId", "Termination reason is required");
            }

            if (existing.getTerminatedBy() == null || existing.getTerminatedBy().isBlank()) {
                throw ClaimException.singleValidationError("terminatedBy", "Terminated By / Issued By is required");
            }

            return;
        }

        if (isRetirementLike(code)) {

            if (existing.getRelievingOrderNumber() == null || existing.getRelievingOrderNumber().isBlank()) {
                throw ClaimException.singleValidationError("relievingOrderNumber", "Relieving order number is required");
            }

            if (existing.getRelievingOrderDate() == null) {
                throw ClaimException.singleValidationError("relievingOrderDate", "Relieving order date is required");
            }

            if (existing.getCessationEffectiveDate() == null) {
                throw ClaimException.singleValidationError("cessationEffectiveDate", "Cessation effective date is required");
            }

            return;
        }

        if (isExitLike(code)) {

            if (existing.getCessationEffectiveDate() == null) {
                throw ClaimException.singleValidationError("cessationEffectiveDate", "Cessation effective date is required");
            }

            if (existing.getRelievingReferenceNumber() == null || existing.getRelievingReferenceNumber().isBlank()) {
                throw ClaimException.singleValidationError("relievingReferenceNumber", "Relieving reference number is required");
            }

            return;
        }

        if (existing.getCessationEffectiveDate() == null) {
            throw ClaimException.singleValidationError(
                    "cessationEffectiveDate",
                    "Cessation effective date is required"
            );
        }
    }

    private boolean isRetirementLike(String code) {
        return List.of(
                "RETIREMENT",
                "EARLY_RETIREMENT",
                "COMPULORY_RETIREMENT"
        ).contains(code);
    }

    private boolean isExitLike(String code) {
        return List.of(
                "RESIGNATION",
                "SERVICE_RELIEF",
                "CONTRACT"
        ).contains(code);
    }

    private ClaimApplication getClaimApplication(Long id) {
        return claimApplicationRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Claim application",
                        id.toString()
                ));
    }

    private CessationTypeMaster getCessationType(Long id) {
        return cessationTypeMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Cessation type",
                        id.toString()
                ));
    }

    private PayeeTypeMaster getPayeeType(Long id) {
        return payeeTypeMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Payee type",
                        id.toString()
                ));
    }

    private TerminationReasonMaster getTerminationReasonIfPresent(Long id) {

        if (id == null || id == 0) {
            return null;
        }

        return terminationReasonMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Termination reason",
                        id.toString()
                ));
    }
}