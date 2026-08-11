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
    
    // ✅ FIXED: Added JOIN FETCH and DISTINCT
    @Query("SELECT DISTINCT w FROM ClaimApplicationWorkflow w " +
           "LEFT JOIN FETCH w.claimApplication ca " +
           "WHERE w.action.id = :verifierReviewAction AND w.action.id != :approveAction")
    List<ClaimApplicationWorkflow> findWorkflowsByActionAndNotAction(
        @Param("verifierReviewAction") Long verifierReviewAction,
        @Param("approveAction") Long approveAction
    );
    
    // ✅ NEW: Method with JOIN FETCH for action ID
    @Query("SELECT DISTINCT w FROM ClaimApplicationWorkflow w " +
           "LEFT JOIN FETCH w.claimApplication ca " +
           "WHERE w.action.id = :actionId")
    List<ClaimApplicationWorkflow> findWorkflowsByActionWithClaimApplication(
        @Param("actionId") Long actionId
    );
    
    List<ClaimApplicationWorkflow> findByClaimApplication_IdOrderByActionAtDescCreatedAtDesc(
        Long claimApplicationId
    );
    
    // ✅ NEW: Optional - Get distinct application numbers directly
    @Query("SELECT DISTINCT ca.applicationNumber FROM ClaimApplicationWorkflow w " +
           "LEFT JOIN w.claimApplication ca " +
           "WHERE w.action.id = :verifierReviewAction AND w.action.id != :approveAction")
    List<String> findDistinctApplicationNumbersByActionAndNotAction(
        @Param("verifierReviewAction") Long verifierReviewAction,
        @Param("approveAction") Long approveAction
    );
    
    // ✅ NEW: Get distinct application numbers by action
    @Query("SELECT DISTINCT ca.applicationNumber FROM ClaimApplicationWorkflow w " +
           "LEFT JOIN w.claimApplication ca " +
           "WHERE w.action.id = :actionId")
    List<String> findDistinctApplicationNumbersByAction(
        @Param("actionId") Long actionId
    );
}