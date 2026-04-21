package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CessationTypeRepository extends JpaRepository<CessationTypeMaster, Long> {

    Optional<CessationTypeMaster> findByCode(String code);
    boolean existsByCode(String code);
    List<CessationTypeMaster> findByIsActiveOrderByNameAsc(String isActive);
    Optional<CessationTypeMaster> findByCodeAndIsActive(String code, String isActive);
}