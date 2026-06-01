package com.claim.claim_processing.application.repository.detail;

import com.claim.claim_processing.application.entity.detail.WrongRemittanceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WrongRemittanceDetailRepository
        extends JpaRepository<WrongRemittanceDetail, Long> {

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    void deleteByClaimApplication_Id(Long claimApplicationId);

    Optional<WrongRemittanceDetail> findByClaimApplication_Id(Long claimApplicationId);

    List<WrongRemittanceDetail> findByWrongRemittanceReason_Id(Long wrongRemittanceReasonId);

    List<WrongRemittanceDetail> findByContributionType_Id(Long contributionTypeId);

    List<WrongRemittanceDetail> findByAffectedAccountType_Id(Long affectedAccountTypeId);

    List<WrongRemittanceDetail> findByErrorType_Id(Long errorTypeId);

    List<WrongRemittanceDetail> findByPayeeType_Id(Long payeeTypeId);

    List<WrongRemittanceDetail> findByRemittanceYear(Integer remittanceYear);

    List<WrongRemittanceDetail> findByAgencyCode(String agencyCode);
}