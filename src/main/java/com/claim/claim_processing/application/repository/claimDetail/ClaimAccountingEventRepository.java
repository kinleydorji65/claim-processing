package com.claim.claim_processing.application.repository.claimDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimAccountingEvent;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimAccountingEventRepository extends JpaRepository<ClaimAccountingEvent, Long> {
    Optional<ClaimAccountingEvent> findByClaimDetail_Id(Long claimDetailId);
    boolean existsByClaimDetailId(Long claimDetailId);

    @Query("SELECT ae FROM ClaimAccountingEvent ae " +
           "LEFT JOIN FETCH ae.claimDetail " +
           "LEFT JOIN FETCH ae.ledgerEntries " +
           "WHERE ae.claimDetail.id IN :claimDetailIds")
    List<ClaimAccountingEvent> findByClaimDetailIdIn(@Param("claimDetailIds") List<Long> claimDetailIds);
}