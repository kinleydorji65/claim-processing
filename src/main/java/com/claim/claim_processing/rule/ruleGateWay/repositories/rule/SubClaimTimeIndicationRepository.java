package com.claim.claim_processing.rule.ruleGateWay.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.rule.SubClaimTimeIndication;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubClaimTimeIndicationRepository
                extends JpaRepository<SubClaimTimeIndication, Long> {

        boolean existsByTimeIndicationCodeIgnoreCase(String timeIndicationCode);

        boolean existsByTimeIndicationCodeIgnoreCaseAndIdNot(
                        String timeIndicationCode,
                        Long id);

        Optional<SubClaimTimeIndication> findByTimeIndicationCodeIgnoreCase(
                        String timeIndicationCode);

        // List<SubClaimTimeIndication> findByCondition_ConditionCodeIgnoreCase(
        //                 String conditionCode);
}
