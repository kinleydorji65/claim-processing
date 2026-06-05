package com.claim.claim_processing.common.repository.others;

import com.claim.claim_processing.common.entities.others.StatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusMasterRepository extends JpaRepository<StatusMaster, Long> {

    Optional<StatusMaster> findByStatusName(String statusName);

    boolean existsByStatusName(String statusName);
}