package com.claim.claim_processing.integration.pension.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.pension.entity.PensionContributionComponent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PensionContributionComponentRepository extends JpaRepository<PensionContributionComponent, Long> {

    // Option 1: Using JPQL with JOIN
    @Query("SELECT pcc FROM PensionContributionComponent pcc " +
           "JOIN pcc.pensionApplication pa " +
           "WHERE pa.memberNppfNo = :nppfNumber " +
           "AND pcc.componentCode = :componentCode " +
           "AND pcc.isActive = 'Y'")
    Optional<PensionContributionComponent> findActiveComponentsByNppfAndComponentCode(
        @Param("nppfNumber") String nppfNumber,
        @Param("componentCode") String componentCode
    );

    // Option 2: Using Native Query
    @Query(value = "SELECT pcc.* FROM PPFMS_PENSION_SERVICE_SCHEMA.PENSION_CONTRIBUTION_COMPONENT pcc " +
           "INNER JOIN PPFMS_PENSION_SERVICE_SCHEMA.PENSION_APPLICATION pa " +
           "ON pcc.PENSION_APPLICATION_ID = pa.PENSION_APPLICATION_ID " +
           "WHERE pa.MEMBER_NPPF_NO = :nppfNumber " +
           "AND pcc.COMPONENT_CODE = :componentCode " +
           "AND pcc.IS_ACTIVE = 'Y'", 
           nativeQuery = true)
    Optional<PensionContributionComponent> findActiveComponentsByNppfAndComponentCodeNative(
        @Param("nppfNumber") String nppfNumber,
        @Param("componentCode") String componentCode
    );

    // Option 3: Check existence (returns boolean)
    @Query("SELECT CASE WHEN COUNT(pcc) > 0 THEN true ELSE false END " +
           "FROM PensionContributionComponent pcc " +
           "JOIN pcc.pensionApplication pa " +
           "WHERE pa.memberNppfNo = :nppfNumber " +
           "AND pcc.componentCode = :componentCode " +
           "AND pcc.isActive = 'Y'")
    boolean existsActiveComponentByNppfAndComponentCode(
        @Param("nppfNumber") String nppfNumber,
        @Param("componentCode") String componentCode
    );
}
