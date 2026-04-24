package com.claim.claim_processing.common.repository.agencyRelated;

import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyCategoryRepository extends JpaRepository<AgencyCategory, String> {

    // 🔹 Find by Code
    Optional<AgencyCategory> findByAgencyCategoryCode(String agencyCategoryCode);

    // 🔹 Find by Name
    Optional<AgencyCategory> findByCategoryName(String categoryName);

    // 🔹 Find all active categories
    List<AgencyCategory> findByStatus(String status);

    // 🔹 Check existence by code
    boolean existsByAgencyCategoryCode(String agencyCategoryCode);

    // 🔹 Custom finder for active + code
    Optional<AgencyCategory> findByAgencyCategoryCodeAndStatus(String agencyCategoryCode, String status);
}