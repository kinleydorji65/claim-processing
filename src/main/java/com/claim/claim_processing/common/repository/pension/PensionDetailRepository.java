package com.claim.claim_processing.common.repository.pension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.pension.PensionDetail;

import java.util.Optional;

@Repository
public interface PensionDetailRepository extends JpaRepository<PensionDetail, Long> {

    /**
     * Find pension detail by NPPF number
     */
    Optional<PensionDetail> findByNppfNumber(String nppfNumber);

}
