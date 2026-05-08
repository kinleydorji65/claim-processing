package com.claim.claim_processing.common.repository.wrongRemittance;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.wrongRemittanceMaster.WrongRemittanceErrorTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemittanceErrorTypeMasterRepository
        extends JpaRepository<WrongRemittanceErrorTypeMaster, Long> {

    Optional<WrongRemittanceErrorTypeMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<WrongRemittanceErrorTypeMaster> findByIsActive(ActivityEnum isActive);
}