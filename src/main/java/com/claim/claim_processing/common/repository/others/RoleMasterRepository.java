package com.claim.claim_processing.common.repository.others;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.common.entities.common.RoleMaster;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, Long> {
    
}
