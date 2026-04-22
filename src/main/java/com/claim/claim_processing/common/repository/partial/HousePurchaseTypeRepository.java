package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.partial.HousePurchaseTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousePurchaseTypeRepository extends JpaRepository<HousePurchaseTypeMaster, Long> {

    boolean existsByCode(String code);

    Optional<HousePurchaseTypeMaster> findByCode(String code);

    List<HousePurchaseTypeMaster> findByIsActive(String isActive);

    Optional<HousePurchaseTypeMaster> findByCodeAndIsActive(String code, String isActive);

    List<HousePurchaseTypeMaster> findByIsActiveOrderByDisplayOrderAscNameAsc(String isActive);
}