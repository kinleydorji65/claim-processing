package com.claim.claim_processing.common.repository.wrong_remittance;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.wrong_remittance_master.WrongRemittanceReasonMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemittanceReasonRepository extends JpaRepository<WrongRemittanceReasonMaster, Long> {

    boolean existsByCode(String code);

    Optional<WrongRemittanceReasonMaster> findByCode(String code);

    List<WrongRemittanceReasonMaster> findByIsActive(ActivityEnum isActive);

    Optional<WrongRemittanceReasonMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);

    List<WrongRemittanceReasonMaster> findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum isActive);

    Optional<WrongRemittanceReasonMaster> findByIdAndIsActive(Long id, ActivityEnum isActive);
}