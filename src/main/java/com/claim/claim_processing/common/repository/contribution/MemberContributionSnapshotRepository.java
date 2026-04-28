package com.claim.claim_processing.common.repository.contribution;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.common.entities.contribution.MemberContributionSnapshot;

public interface MemberContributionSnapshotRepository
        extends JpaRepository<MemberContributionSnapshot, String> {

    Optional<MemberContributionSnapshot> findByMemberCode(String memberCode);
}
