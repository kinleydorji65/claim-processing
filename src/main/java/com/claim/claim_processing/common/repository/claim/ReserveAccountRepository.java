package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveAccountRepository extends JpaRepository<ReserveAccount, Long> {

    // -------------------------------
    // FIND BY MEMBER INFORMATION
    // -------------------------------
    
    /**
     * Find reserve accounts by member code
     */
    List<ReserveAccount> findByMemberCode(String memberCode);

    /**
     * Find reserve accounts by NPPF number
     */
    Optional<ReserveAccount> findByNppfNumber(String nppfNumber);

    /**
     * Find reserve accounts by identity number (CID/Passport/Work Permit)
     */
    /**
     * Find reserve account by identity number and account code
     */
    List<ReserveAccount> findByReserveTypeAndAgencyCode(
            String reserveType, String agencyCode
    );
    Optional<ReserveAccount> findByIdentityNumber(
            String identityNumber
    );

    /**
     * Check if reserve account exists by identity number and account code
     */
    boolean existsByIdentityNumber(
            String identityNumber
    );

    /**
     * Check if reserve account exists by NPPF number and account code
     */
    boolean existsByNppfNumber(
            String nppfNumber
    );

    // -------------------------------
    // FIND BY AGENCY INFORMATION
    // -------------------------------

    /**
     * Find reserve accounts by agency category ID
     */
    List<ReserveAccount> findByAgencyCategoryId(String agencyCategoryId);

    /**
     * Find reserve accounts by agency code
     */
    List<ReserveAccount> findByAgencyCode(String agencyCode);

    /**
     * Find reserve accounts by agency category ID and status
     */
    List<ReserveAccount> findByAgencyCategoryIdAndStatus(
            String agencyCategoryId, String status
    );

    // -------------------------------
    // FIND BY RESERVE ACCOUNT DETAILS
    // -------------------------------

    /**
     * Find reserve accounts by reserve type
     */
    List<ReserveAccount> findByReserveType(String reserveType);

    // -------------------------------
    // FIND BY AMOUNT
    // -------------------------------

    /**
     * Find reserve accounts with total amount greater than given value
     */
    List<ReserveAccount> findByTotalAmountGreaterThan(BigDecimal amount);

    /**
     * Find reserve accounts with total amount between min and max
     */
    List<ReserveAccount> findByTotalAmountBetween(
            BigDecimal minAmount, BigDecimal maxAmount
    );

    /**
     * Find reserve accounts with forfeited amount greater than given value
     */
    List<ReserveAccount> findByForfeitedAmountGreaterThan(BigDecimal amount);

    // -------------------------------
    // FIND BY STATUS
    // -------------------------------

    /**
     * Find reserve accounts by status (ACTIVE, RELEASED, PARTIALLY_RELEASED)
     */
    List<ReserveAccount> findByStatus(String status);

    /**
     * Find reserve accounts by status and isActive
     */
    List<ReserveAccount> findByStatusAndIsActive(String status, String isActive);

    /**
     * Find reserve accounts by isActive
     */
    List<ReserveAccount> findByIsActive(String isActive);

    // -------------------------------
    // FIND BY COMPONENT CODES
    // -------------------------------

    /**
     * Find reserve accounts containing specific component code
     */
    List<ReserveAccount> findByComponentCodesContaining(String componentCode);

    // -------------------------------
    // COMPLEX QUERIES
    // -------------------------------

    /**
     * Find reserve accounts by member NPPF number and status
     */
    List<ReserveAccount> findByNppfNumberAndStatus(
            String nppfNumber, String status
    );

    /**
     * Find reserve accounts by identity number and status
     */
    List<ReserveAccount> findByIdentityNumberAndStatus(
            String identityNumber, String status
    );

    /**
     * Find reserve accounts by reserve type and status
     */
    List<ReserveAccount> findByReserveTypeAndStatus(
            String reserveType, String status
    );

    /**
     * Find all active reserve accounts (isActive = 'Y')
     */
    List<ReserveAccount> findAllByIsActive(String isActive);

    /**
     * Find all active reserve accounts with status
     */
    List<ReserveAccount> findAllByIsActiveAndStatus(
            String isActive, String status
    );
}