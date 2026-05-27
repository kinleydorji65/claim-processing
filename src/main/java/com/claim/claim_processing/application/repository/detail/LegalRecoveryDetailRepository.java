package com.claim.claim_processing.application.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;

@Repository
public interface LegalRecoveryDetailRepository
        extends JpaRepository<LegalRecoveryDetail, Long> {

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    boolean existsByClaimApplication_IdAndIdNot(Long claimApplicationId, Long id);

    Optional<LegalRecoveryDetail> findByClaimApplication_Id(Long claimApplicationId);

    List<LegalRecoveryDetail> findByRecoveryReason_Id(Long recoveryReasonId);

    List<LegalRecoveryDetail> findByPayeeType_Id(Long payeeTypeId);

    List<LegalRecoveryDetail> findBySchemeType_Id(Long schemeTypeId);

    List<LegalRecoveryDetail> findByCurrentStatus_StatusId(Long currentStatusId);

    List<LegalRecoveryDetail> findByLoanType_Id(Long loanTypeId);

    List<LegalRecoveryDetail> findByLoanStatus_Id(Long loanStatusId);

    List<LegalRecoveryDetail> findByMemberCode(String memberCode);

    List<LegalRecoveryDetail> findByNppfNumber(String nppfNumber);

    List<LegalRecoveryDetail> findByAgencyCode(String agencyCode);

    List<LegalRecoveryDetail> findByLoanAccountNumber(String loanAccountNumber);
}