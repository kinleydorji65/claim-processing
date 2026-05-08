package com.claim.claim_processing.common.repository.statusMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.PostingEntryStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostingEntryStatusMasterRepository
        extends JpaRepository<PostingEntryStatusMaster, Long> {

    Optional<PostingEntryStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<PostingEntryStatusMaster> findByIsActive(ActivityEnum isActive);
}