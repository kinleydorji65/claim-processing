package com.claim.claim_processing.common.repository.agencyRelated;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyAdditionalType;


public interface AgencyAdditionalTypeRepository extends JpaRepository<AgencyAdditionalType, Long> {
    Optional<AgencyAdditionalType> findByTypeId(Long typeId);
}
