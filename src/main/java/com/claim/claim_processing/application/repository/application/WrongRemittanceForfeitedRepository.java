package com.claim.claim_processing.application.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.application.WrongRemittanceForfeited;

@Repository
public interface WrongRemittanceForfeitedRepository extends JpaRepository<WrongRemittanceForfeited, Long> {

}
