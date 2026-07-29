package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dzongkhags", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dzongkhag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dzongkhag_id")
    private Long dzongkhagId;

    @Column(name = "dzongkhag_name", length = 50)
    private String dzongkhagName;
}