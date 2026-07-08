package com.claim.claim_processing.common.repository.others;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.ContributionProcessingDetail;

import java.util.List;

@Repository
public interface ContributionProcessingDetailRepository extends JpaRepository<ContributionProcessingDetail, Long> {

    // Empty repository - add custom query methods as needed
    
    // Example optional methods (commented out - add when needed):
    List<ContributionProcessingDetail> findByNppfNumber(String nppfNumber);
    // List<ContributionProcessingDetail> findByAgencyCode(String agencyCode);
    // List<ContributionProcessingDetail> findByPostingStatus(String postingStatus);
    // List<ContributionProcessingDetail> findByBatchId(Long batchId);
    // Optional<ContributionProcessingDetail> findByCid(String cid);
}
