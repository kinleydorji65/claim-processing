package com.claim.claim_processing.integration.contribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * T-85: Repository for ARR_CONFIGURATION (read-only from master schema).
 */
@Repository
public interface ArrConfigurationRepository extends JpaRepository<ArrConfiguration, Long> {

    /**
     * Fetch ARR config for an exact accounting year.
     */
    Optional<ArrConfiguration> findByAccountingYear(String accountingYear);

    /**
     * Fetch only DECLARED (or APPLIED) config for a year.
     * Used to get the official rate when the year is fully declared.
     */
    @Query("SELECT a FROM ArrConfiguration a " +
           "WHERE a.accountingYear = :year " +
           "AND a.arrStatus IN ('DECLARED', 'APPLIED')")
    Optional<ArrConfiguration> findDeclaredByAccountingYear(@Param("year") String accountingYear);

    /**
     * Provisional fallback: returns the most recently DECLARED config.
     * Used when the current year has no DECLARED rate yet — the previous
     * year's declared rate is applied as the provisional rate.
     */
    @Query("SELECT a FROM ArrConfiguration a " +
           "WHERE a.arrStatus IN ('DECLARED', 'APPLIED') " +
           "ORDER BY a.yearEndDate DESC")
    List<ArrConfiguration> findLatestDeclared();

    /**
     * All configs ordered newest first — used for the list endpoint.
     */
    @Query("SELECT a FROM ArrConfiguration a ORDER BY a.yearEndDate DESC")
    List<ArrConfiguration> findAllOrderByYearDesc();

    /**
     * Check whether any CALCULATED or POSTED interest credit event exists for a year.
     * Used by InterestCalculationServiceImpl.initiate() to guard duplicate runs.
     * (Queries INTEREST_CREDIT_EVENT via JPQL join — kept here to avoid circular
     * service injection.)
     */
    @Query("SELECT COUNT(i) > 0 FROM InterestCreditEvent i " +
           "WHERE i.accountingYear = :year " +
           "AND i.status IN ('CALCULATED', 'APPROVED', 'POSTED')")
    boolean existsActiveInterestRunForYear(@Param("year") String accountingYear);
}

