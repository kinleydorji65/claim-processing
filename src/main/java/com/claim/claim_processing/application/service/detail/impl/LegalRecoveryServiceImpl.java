package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.LegalRecoveryDetailRequest;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;
import com.claim.claim_processing.application.mapper.detail.LegalRecoveryMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.detail.LegalRecoveryDetailRepository;
import com.claim.claim_processing.application.service.detail.LegalRecoveryService;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.others.Dzongkhag;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.common.repository.others.DzongkhagRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LegalRecoveryServiceImpl implements LegalRecoveryService {

    private final LegalRecoveryDetailRepository legalRecoveryRepository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final PayeeTypeRepository payeeTypeRepository;
    private final LegalRecoveryMapper legalRecoveryMapper;
    private final DzongkhagRepository dzongkhagRepository;

    @Override
    public LegalRecoveryDetail create(LegalRecoveryDetailRequest request, ClaimApplication claimApplication) {

        validateRequired(request);

        if (legalRecoveryRepository.existsByClaimApplication_Id(claimApplication.getId())) {
            throw ClaimException.conflict(
                    "Legal recovery detail already exists for claim application id: "
                            + claimApplication.getId()
            );
        }

        LegalRecoveryDetail entity = legalRecoveryMapper.toEntity(request);
        if (request.getDzongkhagId() > 0) {
            Dzongkhag dzongkhag = dzongkhagRepository.findById(request.getDzongkhagId()).orElse(null);
            entity.setDzongkhag(dzongkhag);
        }
        entity.setClaimApplication(claimApplication);
        entity.setPayeeType(getPayeeType(request.getPayeeTypeId()));
        LegalRecoveryDetail saved = legalRecoveryRepository.saveAndFlush(entity);

        return saved;
    }

    @Override
    public LegalRecoveryDetail update(LegalRecoveryDetailRequest request, ClaimApplication claimApplication) {

        LegalRecoveryDetail existing = legalRecoveryRepository.findById(request.getId())
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Legal recovery detail",
                        request.getId().toString()
                ));

        if (request.getClaimApplicationId() != null) {
            boolean duplicate = legalRecoveryRepository
                    .existsByClaimApplication_IdAndIdNot(claimApplication.getId(), request.getId());

            if (duplicate) {
                throw ClaimException.conflict(
                        "Legal recovery detail already exists for claim application id: "
                                + claimApplication.getId()
                );
            }
        }

        legalRecoveryMapper.updateEntityFromDto(request, existing);

        if (request.getClaimApplicationId() != null) {
            existing.setClaimApplication(getClaimApplication(request.getClaimApplicationId()));
        }

        if (request.getPayeeTypeId() != null) {
            existing.setPayeeType(getPayeeType(request.getPayeeTypeId()));
        }

        LegalRecoveryDetail updated = legalRecoveryRepository.saveAndFlush(existing);

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public LegalRecoveryDetail getById(Long id) {

        LegalRecoveryDetail entity = legalRecoveryRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Legal recovery detail",
                        id.toString()
                ));

        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public LegalRecoveryDetail getByClaimApplicationId(Long claimApplicationId) {

        LegalRecoveryDetail entity = legalRecoveryRepository.findByClaimApplication_Id(claimApplicationId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Legal recovery detail for claim application",
                        claimApplicationId.toString()
                ));

        return entity;
    }

    private void validateRequired(LegalRecoveryDetailRequest request) {

        if (request.getClaimApplicationId() == null) {
            throw ClaimException.singleValidationError(
                    "claimApplicationId",
                    "Claim application id is required"
            );
        }

        if (request.getPayeeTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "payeeTypeId",
                    "Payee type is required"
            );
        }
    }

    private ClaimApplication getClaimApplication(Long id) {
        return claimApplicationRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Claim application",
                        id.toString()
                ));
    }

    private PayeeTypeMaster getPayeeType(Long id) {
        return payeeTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Payee type",
                        id.toString()
                ));
    }
}