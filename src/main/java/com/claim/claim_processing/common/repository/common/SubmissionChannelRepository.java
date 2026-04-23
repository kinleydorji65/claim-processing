package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionChannelRepository extends JpaRepository<SubmissionChannelMaster, Long> {

    boolean existsByCode(String code);

    Optional<SubmissionChannelMaster> findByCode(String code);

    List<SubmissionChannelMaster> findByIsActive(String isActive);

    List<SubmissionChannelMaster> findByIsActiveOrderByNameAsc(String isActive);

    Optional<SubmissionChannelMaster> findByIdAndIsActive(Long id, String isActive);

    Optional<SubmissionChannelMaster> findByCodeAndIsActive(String code, String isActive);
}