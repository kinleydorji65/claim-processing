package com.claim.claim_processing.claim.repository.detail;

import com.claim.claim_processing.claim.entity.detail.NormalClaimDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NormalClaimDetailRepository extends JpaRepository<NormalClaimDetail, Long> {
    Optional<NormalClaimDetail> findByClaimApplication_Id(Long claimApplicationId);

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    boolean existsByClaimApplication_IdAndIdNot(Long claimApplicationId, Long id);

    void deleteByClaimApplication_Id(Long claimApplicationId);
}