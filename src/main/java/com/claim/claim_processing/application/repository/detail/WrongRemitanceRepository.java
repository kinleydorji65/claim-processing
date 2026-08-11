package com.claim.claim_processing.application.repository.detail;

import com.claim.claim_processing.application.entity.detail.WrongRemitance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WrongRemitanceRepository extends JpaRepository<WrongRemitance, Long> {

    List<WrongRemitance> findByClaimApplication_Id(Long applicationId);
    List<WrongRemitance> findByClaimDetail_Id(Long applicationId);
    /**
     * Find wrong remitance records by application ID
     */
    List<WrongRemitance> findByClaimApplication_IdOrderByCreatedAtDesc(Long applicationId);

    /**
     * Find wrong remitance records by NPPF number
     */
    List<WrongRemitance> findByNppfNumberOrderByCreatedAtDesc(String nppfNumber);

    /**
     * Find wrong remitance records by target year
     */
    List<WrongRemitance> findByTargetYearOrderByCreatedAtDesc(String targetYear);

    /**
     * Find wrong remitance records by NPPF number and target year
     */
    List<WrongRemitance> findByNppfNumberAndTargetYearOrderByCreatedAtDesc(String nppfNumber, String targetYear);

    /**
     * Find wrong remitance records by status
     */
    List<WrongRemitance> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Find wrong remitance records by application ID and status
     */
    List<WrongRemitance> findByClaimApplication_IdAndStatusOrderByCreatedAtDesc(Long applicationId, String status);

    /**
     * Find wrong remitance records by NPPF number and status
     */
    List<WrongRemitance> findByNppfNumberAndStatusOrderByCreatedAtDesc(String nppfNumber, String status);

    /**
     * Find wrong remitance records by target year and status
     */
    List<WrongRemitance> findByTargetYearAndStatusOrderByCreatedAtDesc(String targetYear, String status);

    /**
     * Get latest wrong remitance record for a member
     */
    @Query("SELECT w FROM WrongRemitance w WHERE w.nppfNumber = :nppfNumber ORDER BY w.createdAt DESC")
    WrongRemitance findLatestByNppfNumber(@Param("nppfNumber") String nppfNumber);

    /**
     * Get latest wrong remitance record for an application
     */
    @Query("SELECT w FROM WrongRemitance w WHERE w.claimApplication.Id = :applicationId ORDER BY w.createdAt DESC")
    WrongRemitance findLatestByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * Check if wrong remitance exists for application and year
     */
    boolean existsByClaimApplication_IdAndTargetYear(Long applicationId, String targetYear);
    boolean existsByClaimApplication_Id(Long applicationId);

}
