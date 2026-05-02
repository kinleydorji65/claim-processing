package com.claim.claim_processing.common.repository.others;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.member.MemberDetail;

@Repository
public interface MemberDetailRepository extends JpaRepository<MemberDetail, Long> {
    Optional<MemberDetail> findByMemberCodeAndAgencyCode(String memberCode, String agencyCode);
    
}
