package com.claim.claim_processing.common.repository.others;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.CutoffServiceMaster;

@Repository
public interface CutoffServiceMasterRepository extends JpaRepository<CutoffServiceMaster, Long> {
    // Empty repository - add custom queries as needed
    boolean existsByStatus(String status);
    boolean existsByNumberOfYears(Integer numberOfYears);
}

