package com.claim.claim_processing.application.service.application.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationBankDetailRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.application.mapper.application.ClaimApplicationBankDetailMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationBankDetailRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationBankDetailService;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.others.BankType;
import com.claim.claim_processing.common.entities.others.RelationType;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.repository.others.BankTypeRepository;
import com.claim.claim_processing.common.repository.others.RelationTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimApplicationBankDetailServiceImpl
                implements ClaimApplicationBankDetailService {

        private final ClaimApplicationBankDetailRepository claimApplicationBankDetailRepository;
        private final ClaimApplicationBankDetailMapper claimApplicationBankDetailMapper;
        private final ClaimantTypeRepository claimantTypeRepository;
        private final BankTypeRepository bankTypeRepository;
        private final RelationTypeRepository relationTypeRepository;

        @Override
        public List<ClaimApplicationBankDetail> create(
                        ClaimApplication claimApplication,
                        List<ClaimApplicationBankDetailRequestDto> requestDto) {

                if (claimApplication == null || claimApplication.getId() == null) {
                        throw new RuntimeException("Claim application is required for bank detail.");
                }

                if (requestDto == null || requestDto.isEmpty()) {
                        return Collections.emptyList();
                }

                List<ClaimApplicationBankDetail> bankDetails = requestDto.stream()
                                .filter(Objects::nonNull)
                                .map(request -> {

                                        ClaimantTypeMaster claimantType = null;
                                        RelationType relation = null;
                                        if (request.getClaimantTypeId() != null && request.getClaimantTypeId() > 0) {
                                                claimantType = claimantTypeRepository
                                                                .findById(request.getClaimantTypeId())
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Claimant type not found with id: "
                                                                                                + request.getClaimantTypeId()));
                                        }
                                        if (request.getRelationTypeId() != null && request.getRelationTypeId() > 0) {
                                                relation = relationTypeRepository.findById(request.getRelationTypeId()).orElse(null);

                                        }
                                        BankType bankType = null;
                                        if (request.getBankTypeId() != null && request.getBankTypeId() > 0) {
                                                bankType = bankTypeRepository.findById(request.getBankTypeId())
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Bank type not found with id: "
                                                                                                + request.getBankTypeId()));
                                        }

                                        ClaimApplicationBankDetail claimApplicationBankDetail = claimApplicationBankDetailMapper
                                                        .toEntity(request);
                                        claimApplicationBankDetail.setRelationType(relation);
                                        claimApplicationBankDetail.setClaimApplication(claimApplication);
                                        claimApplicationBankDetail.setClaimantType(claimantType);
                                        claimApplicationBankDetail.setBankType(bankType);
                                        return claimApplicationBankDetail;

                                })
                                .toList();

                return claimApplicationBankDetailRepository.saveAllAndFlush(bankDetails);
        }

        @Override
        public List<ClaimApplicationBankDetail> patch(
                        ClaimApplication claimApplication,
                        List<ClaimApplicationBankDetailRequestDto> requestDto) {

                if (claimApplication == null || claimApplication.getId() == null) {
                        throw new RuntimeException("Claim application is required for bank detail.");
                }

                if (requestDto == null || requestDto.isEmpty()) {
                        return Collections.emptyList();
                }

                List<ClaimApplicationBankDetail> updatedDetails = new ArrayList<>();

                for (ClaimApplicationBankDetailRequestDto request : requestDto) {

                        if (request == null || request.getId() == null) {
                                continue;
                        }

                        ClaimApplicationBankDetail entity = claimApplicationBankDetailRepository
                                        .findById(request.getId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Bank detail not found with id: " + request.getId()));

                        if (!Objects.equals(
                                        entity.getClaimApplication().getId(),
                                        claimApplication.getId())) {
                                throw new RuntimeException(
                                                "Bank detail does not belong to claim application id: "
                                                                + claimApplication.getId());
                        }

                        ClaimantTypeMaster claimantType = null;
                        if (request.getClaimantTypeId() != null && request.getClaimantTypeId() > 0) {
                                claimantType = claimantTypeRepository.findById(request.getClaimantTypeId())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Claimant type not found with id: "
                                                                                + request.getClaimantTypeId()));
                        }

                        BankType bankType = null;
                        if (request.getBankTypeId() != null && request.getBankTypeId() > 0) {
                                bankType = bankTypeRepository.findById(request.getBankTypeId())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Bank type not found with id: "
                                                                                + request.getBankTypeId()));
                        }

                        claimApplicationBankDetailMapper.updateEntity(
                                        entity,
                                        request,
                                        claimantType,
                                        bankType);

                        updatedDetails.add(entity);
                }

                return claimApplicationBankDetailRepository.saveAllAndFlush(updatedDetails);
        }

        public List<ClaimApplicationBankDetail> getByApplicationNumber(String applicationNumber) {
                return claimApplicationBankDetailRepository.findByClaimApplication_ApplicationNumber(applicationNumber);
        }

}
