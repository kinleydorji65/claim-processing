package com.claim.claim_processing.common.repository.others;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.MemberContributionJoiningDateHistory;


@Repository
public interface MemberContributionJoiningDateHistoryRepository 
        extends JpaRepository<MemberContributionJoiningDateHistory, Long> {
    Optional<MemberContributionJoiningDateHistory> findBySourceAgencyNameAndIdentityNumber(String agencyName, String identityName);
    Optional<MemberContributionJoiningDateHistory> findByIdentityNumber(String identityName);
    Optional<MemberContributionJoiningDateHistory> findByMemberCode(String memberCode);
    // Empty repository - custom queries can be added here as needed
}

