package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountTypeMaster, Long> {
    Optional<AccountTypeMaster> findByCode(String code);
    boolean existsByCode(String code);
    List<AccountTypeMaster> findByIsActiveOrderByNameAsc(String isActive);
    Optional<AccountTypeMaster> findByCodeAndIsActive(String code, String isActive);
}