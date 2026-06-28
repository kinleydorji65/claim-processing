package com.claim.claim_processing.common.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.common.CoaAccountMapping;

import java.util.List;

@Repository
public interface CoaAccountMappingRepository extends JpaRepository<CoaAccountMapping, Long> {

    
     @Query("SELECT c FROM CoaAccountMapping c WHERE c.eventType = :eventType AND c.agencyCategoryId = :agencyCategoryId AND c.isActive = 'Y' ORDER BY c.seqNo ASC")
    List<CoaAccountMapping> findByEventTypeAndAgencyCategoryIdAndIsActiveTrueOrderBySeqNoAsc(
        @Param("eventType") String eventType,
        @Param("agencyCategoryId") String agencyCategoryId
    );
}
