package com.claim.claim_processing.application.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;

import java.util.Optional;

@Repository
public interface NormalClaimDetailRepository extends JpaRepository<NormalClaimDetail, Long> {
    Optional<NormalClaimDetail> findByClaimApplication_Id(Long claimApplicationId);
    Optional<NormalClaimDetail> findByClaimDetail_Id(Long claimDetailId);

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    boolean existsByClaimApplication_IdAndIdNot(Long claimApplicationId, Long id);

    void deleteByClaimApplication_Id(Long claimApplicationId);
}