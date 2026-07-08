package com.claim.claim_processing.unclaimed.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.others.ContributionProcessingDetail;
import com.claim.claim_processing.common.entities.others.UserRegistrateredAgencyMapping;
import com.claim.claim_processing.common.mapper.claim.ReserveAccountMapper;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.common.UserRegistrateredAgencyMappingRepository;
import com.claim.claim_processing.common.repository.others.ContributionProcessingDetailRepository;
import com.claim.claim_processing.unclaimed.UnclaimService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnclaimedServiceImpl implements UnclaimService {
    private final ReserveAccountRepository reserveAccountRepository;
    private final UserRegistrateredAgencyMappingRepository userRegistrateredAgencyMappingRepository;
    private final ReserveAccountMapper mapper;
    private final ContributionProcessingDetailRepository contributionProcessingDetailRepository;


    @Override
    public List<ReserveAccountResponseDto> getUnclaimedReserveAccounts(String userCode) {
                List<UserRegistrateredAgencyMapping> userMappings = userRegistrateredAgencyMappingRepository.findByUserCode(userCode);

        List<ReserveAccount> unclaimedAccounts = userMappings.stream()
        .map(mapping -> reserveAccountRepository.findByReserveTypeAndAgencyCode("CONTRIBUTION_STOPPAGE", mapping.getAgencyCode()))
        .flatMap(List::stream)
        .toList();

        return unclaimedAccounts.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ReserveAccountResponseDto> activateUnclaimReserveAccount(String userCode, String nppfNumber) {
        List<UserRegistrateredAgencyMapping> userMappings = userRegistrateredAgencyMappingRepository.findByUserCode(userCode);

        List<ReserveAccount> unclaimedAccounts = userMappings.stream()
        .map(mapping -> reserveAccountRepository.findByReserveTypeAndAgencyCode("CONTRIBUTION_STOPPAGE", mapping.getAgencyCode()))
        .flatMap(List::stream)
        .toList();

        // Activate the unclaimed accounts
        unclaimedAccounts.forEach(account -> {
            account.setStatus("ACTIVE");
            reserveAccountRepository.save(account);
        });
        List<ContributionProcessingDetail> details = contributionProcessingDetailRepository.findByNppfNumber(nppfNumber);
        details.stream()
                .map(cp -> {
                    cp.setPostingStatus("ACTIVE");
                    contributionProcessingDetailRepository.save(cp);
                    return cp;
                })
                .toList();

        return unclaimedAccounts.stream()
                .map(mapper::toResponseDto)
                .toList();
    }



}
