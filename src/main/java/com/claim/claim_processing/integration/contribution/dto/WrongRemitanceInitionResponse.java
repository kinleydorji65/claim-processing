package com.claim.claim_processing.integration.contribution.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Data
@Builder
public class WrongRemitanceInitionResponse {
    private String nppfNumber;
    private List<WrongRemitanceInitionResponseDto> wrongRemitances;
    

    @Data
    @Builder
    public static class WrongRemitanceInitionResponseDto {
        private Long id;
        private Long pid;
        private String ruleSource;
        private BigDecimal basicSalary;
        private BigDecimal ec;
        private BigDecimal mc;
        private BigDecimal gc;
        private BigDecimal vc;
        private BigDecimal totalContribution;
        private BigDecimal pensionMc;
        private BigDecimal pensionEc;
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private String month;
    }
}