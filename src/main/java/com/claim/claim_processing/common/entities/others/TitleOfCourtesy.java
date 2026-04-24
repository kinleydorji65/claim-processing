package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TITLES_OF_COURTESY", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitleOfCourtesy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TITLE_ID")
    private Long titleId;

    @Column(name = "TITLE_NAME")
    private String titleName;
}
