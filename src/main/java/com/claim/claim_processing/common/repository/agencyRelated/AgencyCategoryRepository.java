package com.claim.claim_processing.common.repository.agencyRelated;

import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgencyCategoryRepository extends JpaRepository<AgencyCategory, String> {

    Optional<AgencyCategory> findByAgencyCategoryCode(String agencyCategoryCode);

    boolean existsByAgencyCategoryCode(String agencyCategoryCode);
}