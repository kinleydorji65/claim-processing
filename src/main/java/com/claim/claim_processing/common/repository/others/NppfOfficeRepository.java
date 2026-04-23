package com.claim.claim_processing.common.repository.others;

import com.claim.claim_processing.common.entities.others.NppfOfficeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NppfOfficeRepository extends JpaRepository<NppfOfficeMaster, Long> {

    boolean existsByCode(Long code);

    Optional<NppfOfficeMaster> findByCode(Long code);

    List<NppfOfficeMaster> findByIsActive(String isActive);

    Optional<NppfOfficeMaster> findByCodeAndIsActive(Long code, String isActive);

    List<NppfOfficeMaster> findByIsActiveOrderByNameAsc(String isActive);

    Optional<NppfOfficeMaster> findByIdAndIsActive(Long id, String isActive);
}