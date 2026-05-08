package com.claim.claim_processing.common.repository.unclaimed;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedNoticeTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnclaimedNoticeTypeMasterRepository
        extends JpaRepository<UnclaimedNoticeTypeMaster, Long> {

    Optional<UnclaimedNoticeTypeMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<UnclaimedNoticeTypeMaster> findByIsActive(ActivityEnum isActive);
}