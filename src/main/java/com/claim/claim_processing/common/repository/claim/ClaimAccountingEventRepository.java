package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimAccountingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimAccountingEventRepository extends JpaRepository<ClaimAccountingEvent, Long> {
    Optional<ClaimAccountingEvent> findByClaimDetail_Id(Long claimDetailId);
    Optional<ClaimAccountingEvent> findByClaimDetailId(Long claimDetailId);
    boolean existsByClaimDetailId(Long claimDetailId);

    @Query("SELECT ae FROM ClaimAccountingEvent ae " +
           "LEFT JOIN FETCH ae.claimDetail " +
           "LEFT JOIN FETCH ae.ledgerEntries " +
           "WHERE ae.claimDetailId IN :claimDetailIds")
    List<ClaimAccountingEvent> findByClaimDetailIdIn(@Param("claimDetailIds") List<Long> claimDetailIds);
    
}