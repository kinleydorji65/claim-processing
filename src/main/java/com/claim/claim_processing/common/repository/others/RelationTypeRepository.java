package com.claim.claim_processing.common.repository.others;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.RelationType;

@Repository
public interface RelationTypeRepository extends JpaRepository<RelationType, Long> {
    
}
