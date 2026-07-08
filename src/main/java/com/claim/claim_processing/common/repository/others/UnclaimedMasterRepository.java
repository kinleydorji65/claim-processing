package com.claim.claim_processing.common.repository.others;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.UnclaimedMaster;


@Repository
public interface UnclaimedMasterRepository extends JpaRepository<UnclaimedMaster, Long> {
    // Empty repository - add custom queries here as needed
    Optional<UnclaimedMaster> findByIsActive(String isActive);
}
