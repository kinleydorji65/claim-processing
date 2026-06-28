package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationWorkflow;

import java.util.List;

@Repository
public interface ClaimApplicationWorkflowRepository extends JpaRepository<ClaimApplicationWorkflow, Long> {
    List<ClaimApplicationWorkflow> findByClaimApplication_IdOrderByActionAtAsc(Long claimApplicationId);
    List<ClaimApplicationWorkflow> findWorkflowsByAction_Id(Long actionId);
    boolean existsByClaimApplication_Id(Long claimApplicationId);
@Query("SELECT c FROM ClaimApplicationWorkflow c WHERE " +
           "c.action.id = :verifierReviewAction AND c.action.id != :approveAction")
    List<ClaimApplicationWorkflow> findWorkflowsByActionAndNotAction(
        @Param("verifierReviewAction") Long verifierReviewAction,
        @Param("approveAction") Long approveAction
    );    List<ClaimApplicationWorkflow> findByClaimApplication_IdOrderByActionAtDescCreatedAtDesc(
            Long claimApplicationId
    );
}
