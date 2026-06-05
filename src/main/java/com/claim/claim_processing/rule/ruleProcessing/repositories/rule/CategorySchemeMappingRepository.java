package com.claim.claim_processing.rule.ruleProcessing.repositories.rule;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleProcessing.entities.rule.CategorySchemeMapping;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorySchemeMappingRepository
        extends JpaRepository<CategorySchemeMapping, Long> {

    boolean existsByAgencyCategory_CategoryIdAndSchemeType_Id(
            String categoryId,
            Long schemeTypeId
    );

    boolean existsByAgencyCategory_CategoryIdAndSchemeType_IdAndIdNot(
            String categoryId,
            Long schemeTypeId,
            Long id
    );
    boolean existsByCategoryCodeAndSchemeCodeAndIdNot(
            String categoryCode,
            String schemeCode,
            Long id
    );

    boolean existsByCategoryCodeAndSchemeCode(
            String categoryCode,
            String schemeCode
    );

    Optional<CategorySchemeMapping> findByAgencyCategory_CategoryIdAndSchemeTypeIsNull(String categoryId);

    List<CategorySchemeMapping> findBySchemeType_Id(Long schemeTypeId);
    Optional<CategorySchemeMapping> findByCategorySchemeCodeIgnoreCase(String categorySchemeCode);
Optional<CategorySchemeMapping> findBySchemeType_IdAndAgencyCategory_CategoryId(Long id, String categoryId);
//                                                                      ^^
//                                                    Use "id" not "schemeTypeId"
        }