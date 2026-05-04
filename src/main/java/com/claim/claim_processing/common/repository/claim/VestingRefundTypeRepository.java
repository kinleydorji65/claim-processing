package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.VestingRefundType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VestingRefundTypeRepository extends JpaRepository<VestingRefundType, Long> {

    Optional<VestingRefundType> findByCode(String code);

    boolean existsByCode(String code);
}