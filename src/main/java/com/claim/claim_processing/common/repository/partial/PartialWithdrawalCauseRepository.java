package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartialWithdrawalCauseRepository extends JpaRepository<PartialWithdrawalCauseMaster, Long> {

    // check duplicate code (important for unique constraint handling)
    boolean existsByCode(String code);

    // fetch active records only
    List<PartialWithdrawalCauseMaster> findByIsActive(String isActive);

    // optional: fetch by reason (FK filtering)
    List<PartialWithdrawalCauseMaster> findByReason_Id(Long reasonId);

    // optional: safe fetch by id + active check
    Optional<PartialWithdrawalCauseMaster> findByIdAndIsActive(Long id, String isActive);
}