package com.claim.claim_processing.application.repository.claimDetail;

import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCaseComponentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimSpecialCaseComponentDetailRepository extends JpaRepository<ClaimSpecialCaseComponentDetail, Long> {

    /**
     * Find all component details by special case ID
     */
    List<ClaimSpecialCaseComponentDetail> findBySpecialCaseId(Long specialCaseId);

    /**
     * Find all component details by special case ID and component type
     */
    List<ClaimSpecialCaseComponentDetail> findBySpecialCaseIdAndComponentType(Long specialCaseId, String componentType);

    /**
     * Find all component details by special case ID and is active status
     */
    List<ClaimSpecialCaseComponentDetail> findBySpecialCaseIdAndIsActive(Long specialCaseId, String isActive);

    /**
     * Delete all component details by special case ID
     */
    void deleteBySpecialCaseId(Long specialCaseId);

    /**
     * Count component details by special case ID
     */
    long countBySpecialCaseId(Long specialCaseId);

    /**
     * Find all active component details by special case ID
     */
    default List<ClaimSpecialCaseComponentDetail> findActiveBySpecialCaseId(Long specialCaseId) {
        return findBySpecialCaseIdAndIsActive(specialCaseId, "Y");
    }
}