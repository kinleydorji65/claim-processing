package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.partial.DisasterTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisasterTypeRepository extends JpaRepository<DisasterTypeMaster, Long> {

    boolean existsByCode(String code);

    Optional<DisasterTypeMaster> findByCode(String code);

    List<DisasterTypeMaster> findByIsActive(String isActive);

    Optional<DisasterTypeMaster> findByCodeAndIsActive(String code, String isActive);

    List<DisasterTypeMaster> findByIsActiveOrderByNameAsc(String isActive);
}